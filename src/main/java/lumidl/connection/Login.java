package lumidl.connection;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

// Yes, star imports are a bad pattern, TODO, etc.
import static lumidl.connection.Constants.*;

/**
 * Contains and encapsulates all login-related methods and data.
 * @author dongyu
 */
public class Login {	
	private Logger logger;
	private HttpTransport httpTransport;
	private HttpRequestFactory httpRequestFactory;
	private String accessToken = null;
	private String username;
	private String password;
	
	/**
	 * C'tor for Login object.
	 * @param username The username to be used during actual login attempt.
	 * @param password The password to be used during actual login attempt.
	 */
	public Login(String username, String password) {
		// We use Apache's HTTP transport because it preserves cookies across requests.
		httpTransport = new ApacheHttpTransport();
		httpRequestFactory = httpTransport.createRequestFactory();
		logger = Logger.getLogger(this.getClass().getName());
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Returns the access token.
	 * @return A String representation of the access token.
	 */
	public String getAccessToken() {
		if (accessToken == null) {
			throw new LoginException("No access token found. Please log in before attempting to retrieve access token.");
		}
		
		return accessToken;
	}
	
	/**
	 * Returns the subscription key.
	 * @return A String representation of the subscription key.
	 */
	public String getSubscriptionKey() {
		return OCP_APIM_SUBSCRIPTION_KEY;
	}
	
	/**
	 * Attempts to log into LumiNUS.
	 * Will attempt to cache access token upon successful login.
	 * @param username The NUSNET user-name to use.
	 * @param password The NUSNET password to use.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void execute() throws IOException {
		/*
		 * The flow for login is as follows (?):
		 * 1. Log in with username and password to OAuth2 site.
		 * 2. Get redirected repeatedly until we hit the callback site (with the code parameter in URL for ADFS token retrieval).
		 * 3. Manually submit a request for an access token to the ADFS token endpoint.
		 * 4. Save access token for use in API calls.
		 */
		// The documentation doesn't mention under what conditions the build*Request methods throw IOExceptions, so we'll deal with it as it comes.
		HttpRequest authRequest = httpRequestFactory.buildPostRequest(generateAuthUrl(), generateAuthFormContents(username, password));
		String code = retrieveTokenCode(authRequest, httpRequestFactory);
		
		HttpRequest tokenRequest = withApim(httpRequestFactory.buildPostRequest(generateTokenUrl(), generateTokenFormContents(code)));
		HttpResponse tokenResponse = tokenRequest.execute();
		
		Map<String, Object> tokenResponseJson;
		try {
			tokenResponseJson = (Map<String, Object>) new Gson().fromJson(tokenResponse.parseAsString(), Map.class);
		} catch (JsonSyntaxException jse) {
			logger.severe(jse.toString());
			throw new LoginException("An error was encountered while attempting to parse the JSON response from the token endpoint.");
		}
		
		accessToken = (String) tokenResponseJson.get("access_token").toString();
		
		if (accessToken == null) {
			throw new LoginException("No access token found.");
		}
	}
	
	/**
	 * Returns a new API object using this login session's details.
	 * @return a new API object.
	 */
	public Api generateApiObject() {
		return new Api(getAccessToken(), getSubscriptionKey(), httpRequestFactory);
	}
	
	// ======================================  Helper functions ====================================== \\
	/**
	 * Returns a randomly generated i bytes as a String.
	 * @param i The number of bytes to generate.
	 * @return a String representation of i bytes.
	 */
	private static String generateRandomBytes(int i) {
		byte[] b = new byte[i];
		new Random().nextBytes(b);
		return Base64.getEncoder().encodeToString(b);
	}
	
	/**
	 * Returns the authentication endpoint URL.
	 * @return A GenericUrl form representation of the authentication endpoint URL.
	 */
	private static GenericUrl generateAuthUrl() {
		GenericUrl toReturn = new GenericUrl(ADFS_OAUTH2_URL);
		toReturn.set("response_type", ADFS_RESPONSE_TYPE);
		toReturn.set("client_id", ADFS_CLIENT_ID);
		toReturn.set("state", generateRandomBytes(16));
		toReturn.set("redirect_uri", ADFS_REDIRECT_URL);
		toReturn.set("scope", ADFS_SCOPE);
		toReturn.set("resource",ADFS_RESOURCE);
		toReturn.set("nonce", generateRandomBytes(16));
		
		return toReturn;
	}
	
	/**
	 * Returns the token endpoint URL.
	 * @return A GenericUrl form representation of the token endpoint URL.
	 */
	private static GenericUrl generateTokenUrl() {
		return new GenericUrl(API_BASE_URL + ADFS_TOKEN_PATH);
	}
	/**
	 * Generates the authentication form contents.
	 * @param username The username to use.
	 * @param password The password to use.
	 * @return The data to be submitted.
	 */
	private static HttpContent generateAuthFormContents(String username, String password) {
		Map<String,Object> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		map.put("AuthMethod", "FormsAuthentication");
		
		return new UrlEncodedContent(map);
	}
	
	/**
	 * Generates the ADFS token auth contents.
	 * As indicated in https://docs.microsoft.com/en-us/windows-server/identity/ad-fs/overview/ad-fs-scenarios-for-developers
	 * Values gotten from https://github.com/indocomsoft/fluminurs/blob/master/src/api/authorization.rs
	 * @param code The code to use.
	 * @return The data to be submitted.
	 */
	private static HttpContent generateTokenFormContents(String code) {
		Map<String, Object> map = new HashMap<>();
		map.put("grant_type", ADFS_GRANT_TYPE);
		map.put("client_id", ADFS_CLIENT_ID);
		map.put("resource", ADFS_RESOURCE);
		map.put("code", code);
		map.put("redirect_uri", ADFS_REDIRECT_URL);
		
		return new UrlEncodedContent(map);
	}
	
	/**
	 * This method chases the redirects with GET requests until it finds the particular callback URL to extract the "code" parameter from.
	 * @param request The initial HTTP request to use.
	 * @param factory The HTTP request factory to use to generate the subsequent requests.
	 * @return A String representation of the code parameter.
	 * @throws IOException 
	 * @throws AuthenticationException
	 */
	@SuppressWarnings("unchecked")
	private static String retrieveTokenCode(HttpRequest request, HttpRequestFactory factory) throws IOException, AuthenticationException {
		request.setFollowRedirects(false);
		request.setThrowExceptionOnExecuteError(false);
		
		HttpResponse response = request.execute();
		
		if (response.getStatusCode() == 302) {
			// Indicates a redirection attempt
			GenericUrl redirectionUrl = new GenericUrl(response.getHeaders().getLocation());
			if (redirectionUrl.toString().contains("/auth/callback?code=")) {
				// Extract the code from the URL
				try {
					return ((List<String>)new GenericUrl(redirectionUrl.toString().replaceFirst("#", "?")).get("code")).get(0);
				} catch (NullPointerException npe) {
					throw new AuthenticationException("No code parameter found in callback url.");
				}
			} else {
				return retrieveTokenCode(factory.buildGetRequest(redirectionUrl), factory);
			}
		}
		
		throw new AuthenticationException("No callback url encountered.");
	}
	
	/**
	 * Adds the Ocp-Apim-Subscription-Key field to the header of the given request
	 * @param request The request to alter.
	 * @return The same request, with altered headers (subscription key put in).
	 */
	private static HttpRequest withApim(HttpRequest request) {
		request.getHeaders().set(OCP_APIM_SUBSCRIPTION_KEY_HEADER_NAME, OCP_APIM_SUBSCRIPTION_KEY);
		return request;
	}
}
