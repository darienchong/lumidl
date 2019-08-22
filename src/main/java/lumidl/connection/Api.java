package lumidl.connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.Gson;

import lumidl.model.ModuleResponse;
import lumidl.model.Module;

//Yes, star imports are a bad pattern, TODO, etc.
import static lumidl.connection.Constants.*;

/**
 * Contains and encapsulates all API-related methods.
 * @author dongyu
 *
 */
public class Api {
	private Logger logger;
	private String accessToken;
	private String subscriptionKey;
	private HttpRequestFactory factory;
	private Gson gson;
	
	/**
	 * C'tor for Api.
	 * @param accessToken The authorization code to use.
	 * @param subscriptionKey The subscription key to use.
	 * @param factory The HTTP request factory to use to build requests.
	 */
	public Api(String accessToken, String subscriptionKey, HttpRequestFactory factory) {
		this.accessToken = accessToken;
		this.subscriptionKey = subscriptionKey;
		this.factory = factory;
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		
		gson = new Gson();
	}
	
	/**
	 * Returns a list of modules being taken in the current semester.
	 * @return
	 * @throws IOException
	 */
	public List<Module> getModules() throws IOException {
		HttpRequest getResourceRequest = withAuth(buildGetRequest(API_BASE_URL + GET_RESOURCES_PATH));
		List<Module> modules = gson.fromJson(getResourceRequest.execute().parseAsString(), ModuleResponse.class).data;
		
		return modules;
	}
	
	public List<DownloadUrl> getDownloads(Module m) {
		List<DownloadUrl> toReturn = new ArrayList<>();
		return toReturn;
	}
	
	// ======================================  Helper functions ====================================== \\
	
	/**
	 * Adds authentication headers to a HTTP request.
	 * @param request The request to alter.
	 * @return The altered request.
	 */
	private HttpRequest withAuth(HttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
		headers.setAuthorization("Bearer " + accessToken);
		return request;
	}
	
	private HttpRequest buildGetRequest(String url) throws IOException {
		return factory.buildGetRequest(new GenericUrl(url));
	}
	
	private FolderNavigator generateFolderNavigator(String currentFolderId) {
		return new FolderNavigator(DOWNLOAD_PATH, currentFolderId, factory, accessToken, subscriptionKey);
	}
}
