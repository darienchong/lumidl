package lumidl.connection;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;

import lumidl.model.ModuleResponse;
import lumidl.util.Constants;
import lumidl.util.LoggerFactory;
import lumidl.model.Module;

/**
 * Contains and encapsulates all API-related methods.
 * @author dongyu
 *
 */
public class Api {
	private Logger logger;
	private String accessToken;
	private String subscriptionKey;
	
	/**
	 * C'tor for Api.
	 * @param accessToken The authorization code to use.
	 * @param subscriptionKey The subscription key to use.
	 * @param factory The HTTP request factory to use to build requests.
	 */
	public Api(String accessToken, String subscriptionKey) {
		if (accessToken == null || accessToken.length() <= 0) {
			throw new IllegalArgumentException("Access token cannot be zero-length/null.");
		}
		this.accessToken = accessToken;
		this.subscriptionKey = subscriptionKey;
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	/**
	 * Returns a list of modules being taken in the current semester.
	 * @return
	 * @throws IOException
	 */
	public List<Module> getModules() throws IOException {
		HttpRequest getResourceRequest = withAuth(HttpManager.buildGetRequest(Constants.API_BASE_URL + Constants.GET_RESOURCES_PATH));
		List<Module> modules = new Gson().fromJson(getResourceRequest.execute().parseAsString(), ModuleResponse.class).data;
		
		return modules;
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 * @throws IOException
	 */
	public List<DownloadUrl> getDownloads(Module m) throws IOException {
		Folder rootFolderNavigator = buildModuleFolder(m.name, m.id);
		return rootFolderNavigator.getAllDownloadUrls();
	}
		
	/**
	 * Adds authentication headers to a HTTP request.
	 * @param request The request to alter.
	 * @return The altered request.
	 */
	public HttpRequest withAuth(HttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
		headers.setAuthorization("Bearer " + accessToken);
		return request;
	}
	
	// ======================================  Helper functions ====================================== \\
	
	private Folder buildModuleFolder(String moduleName, String moduleId) {
		return new Folder(new Constants().getDownloadPath() + "\\" + moduleName, moduleId, this);
	}
}
