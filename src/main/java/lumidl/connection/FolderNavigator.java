package lumidl.connection;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.gson.Gson;

import lumidl.model.File;
import lumidl.model.FilesResponse;
import lumidl.model.Folder;
import lumidl.model.FolderResponse;

//Yes, star imports are a bad pattern, TODO, etc.
import static lumidl.connection.Constants.*;

public class FolderNavigator {
	private Deque<String> pathStack;
	private String currentPath;
	private Deque<String> folderStack;
	private String currentFolderId;
	private HttpRequestFactory factory;
	private String accessToken;
	private String subscriptionKey;
	private Logger logger;
		
	public FolderNavigator(String currentPath, String currentFolderId, HttpRequestFactory factory, String accessToken, String subscriptionKey) {
		this.pathStack = new ArrayDeque<>();		
		this.folderStack = new ArrayDeque<>();
		
		this.currentPath = currentPath;
		this.currentFolderId = currentFolderId;
		this.factory = factory;
		this.accessToken = accessToken;
		this.subscriptionKey = subscriptionKey;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
 	}
	
	public List<Folder> getPreviousFolders() throws IOException {
		if (folderStack.peek() == null) {
			throw new RuntimeException("No previous folder id found.");
		}
		
		String previousFolderId = folderStack.peek();
		HttpRequest previousFolderRequest = withAuth(buildGetRequest(API_BASE_URL + GET_FOLDERS_PATH + previousFolderId));
		List<Folder> previousFolderResponse = new Gson().fromJson(previousFolderRequest.execute().parseAsString(), FolderResponse.class).data;
		
		folderStack.pop();
		currentFolderId = previousFolderId;
		
		currentPath = pathStack.pop();
		
		return previousFolderResponse;
	}
	
	public List<Folder> getCurrentFolders() throws IOException {
		HttpRequest currentFolderRequest = withAuth(buildGetRequest(API_BASE_URL + GET_FOLDERS_PATH + currentFolderId));
		List<Folder> currentFolderResponse = new Gson().fromJson(currentFolderRequest.execute().parseAsString(), FolderResponse.class).data;
		
		return currentFolderResponse;
	}
	
	public List<Folder> getFolder(String folderName, String folderId) throws IOException {
		HttpRequest folderRequest = withAuth(buildGetRequest(API_BASE_URL + GET_FOLDERS_PATH + folderId));
		List<Folder> folderResponse = new Gson().fromJson(folderRequest.execute().parseAsString(), FolderResponse.class).data;
		
		folderStack.push(currentFolderId);
		pathStack.push(currentPath);
		
		currentFolderId = folderId;
		currentPath = currentPath += folderName + "\\";
		
		return folderResponse;
	}
	
	public List<DownloadUrl> getDownloadUrls() throws IOException {
		HttpRequest filesRequest = withAuth(buildGetRequest(API_BASE_URL + String.format(GET_FILES_PATH, currentFolderId)));
		List<File> filesResponse = new Gson().fromJson(filesRequest.execute().parseAsString(), FilesResponse.class).data;
		
		List<DownloadUrl> toReturn = new ArrayList<>();
		for (File file : filesResponse) {
			HttpRequest downloadUrlRequest = withAuth(buildGetRequest(API_BASE_URL + String.format(GET_DOWNLOAD_URL_PATH, file.id)));
			GenericUrl downloadUrlResponse = new GenericUrl((String) new Gson().fromJson(downloadUrlRequest.execute().parseAsString(), Map.class).get("data"));
			
			DownloadUrl downloadUrl = new DownloadUrl(currentPath + file.fileName, downloadUrlResponse);
			toReturn.add(downloadUrl);
		}
		
		return toReturn;
	}
	
	public List<DownloadUrl> getAllDownloadUrls() throws IOException {
		// TODO
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
}
