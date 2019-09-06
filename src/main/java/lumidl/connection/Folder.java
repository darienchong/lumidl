package lumidl.connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.gson.Gson;

import lumidl.model.File;
import lumidl.model.FilesResponse;
import lumidl.model.FolderResponse;
import lumidl.util.Constants;
import lumidl.util.LoggerFactory;

/**
 * 
 * @author dongyu
 *
 */
public class Folder {
	private String currentPath;
	private String currentFolderId;
	private Api api;
	private Logger logger;
	
	/**
	 * 
	 * @param currentPath
	 * @param currentFolderId
	 * @param api
	 */
	public Folder(String currentPath, String currentFolderId, Api api) {
		this.currentPath = sanitise(currentPath);
		this.currentFolderId = currentFolderId;
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.api = api;
 	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<lumidl.model.Folder> getSubfolders() throws IOException {
		HttpRequest currentFolderRequest = api.withAuth(HttpManager.buildGetRequest(Constants.API_BASE_URL + Constants.GET_FOLDERS_PATH + currentFolderId));
		List<lumidl.model.Folder> currentFolderResponse = new Gson().fromJson(currentFolderRequest.execute().parseAsString(), FolderResponse.class).data;
		
		return currentFolderResponse;
	}
	
	/**
	 * Returns a list of file download URLs for all the files in the current folder.
	 * @return
	 * @throws IOException
	 */
	private List<DownloadUrl> getDownloadUrls() throws IOException {
		HttpRequest filesRequest = api.withAuth(HttpManager.buildGetRequest(Constants.API_BASE_URL + String.format(Constants.GET_FILES_PATH, currentFolderId)));
		List<File> filesResponse = new Gson().fromJson(filesRequest.execute().parseAsString(), FilesResponse.class).data;
		
		List<DownloadUrl> toReturn = new ArrayList<>();
		for (File file : filesResponse) {
			HttpRequest downloadUrlRequest = api.withAuth(HttpManager.buildGetRequest(Constants.API_BASE_URL + String.format(Constants.GET_DOWNLOAD_URL_PATH, file.id)));
			GenericUrl downloadUrlResponse = new GenericUrl((String) new Gson().fromJson(downloadUrlRequest.execute().parseAsString(), Map.class).get("data"));
			
			DownloadUrl downloadUrl = new DownloadUrl(currentPath + "\\" + file.fileName, downloadUrlResponse);
			toReturn.add(downloadUrl);
		}
		
		return toReturn;
	}
	
	/**
	 * Returns a list of file download URLs for all the files in the current folder AND any subfolders of.
	 * @return
	 * @throws IOException
	 */
	public List<DownloadUrl> getAllDownloadUrls() throws IOException {
		List<DownloadUrl> masterList = new ArrayList<>();
		
		List<lumidl.model.Folder> subfolders = getSubfolders();
		masterList.addAll(getDownloadUrls());
		for (lumidl.model.Folder subfolder : subfolders) {
			Folder subfolderNavigator = new Folder(currentPath + "\\" + subfolder.name, subfolder.id, api);
			masterList.addAll(subfolderNavigator.getAllDownloadUrls());
		}
		
		return masterList;
	}
	
	// ======================================  Helper functions ====================================== \\
	private static String sanitise(String s) {
		return s.replaceAll(" ", "_");
	}
}
