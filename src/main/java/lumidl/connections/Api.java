package lumidl.connections;

import com.google.api.client.http.HttpRequestFactory;

/**
 * Contains and encapsulates all API-related methods.
 * @author dongyu
 *
 */
public class Api {
	// LumiNUS API URLs, paths, etc.
	private static final String API_BASE_URL = "https://luminus.azure-api.net";
	
	private String accessToken;
	private String subscriptionKey;
	private HttpRequestFactory factory;
	
	public Api(String accessToken, String subscriptionKey, HttpRequestFactory factory) {
		this.accessToken = accessToken;
		this.subscriptionKey = subscriptionKey;
		this.factory = factory;
	}
	
	
}
