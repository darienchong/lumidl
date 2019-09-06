package lumidl.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;

import lumidl.util.LoggerFactory;

/** 
 * Handles file downloads, recovers from expected situations 
 * e.g. Folder path does not exist, file already exists, etc.
 * @author dongyu
 *
 */
public class DownloadManager {
	private Api api;
	private List<DownloadUrl> urlList;
	private Logger logger;
	
	public DownloadManager(Api api, List<DownloadUrl> urlList) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.api = api;
		this.urlList = urlList;
	}
	
	/**
	 * Attempts to download a file.
	 * @param url The wrapper object containing both the destination path and download Url.
	 * @return true if download succeeds or file already exists, false otherwise.
	 */
	private boolean downloadFile(DownloadUrl url) {
		String filePath = url.getFilePath();
		GenericUrl fileDownloadUrl = url.getUrl();
		
		logger.info("Attempting to download (" + filePath + ") from ("  + fileDownloadUrl + ").");
		
		// Check first if the destination path exists.
		Path path = Paths.get(getFolderPath(filePath));
		if (!Files.exists(path)) {			
			try {
				Files.createDirectories(path);
				logger.info("Created previously non-existent destination path (" + path.toAbsolutePath() + ").");
			} catch (IOException e) {
				// Directory failed to be created
				logger.severe("Failed to create destination path (" + path.toAbsolutePath() + ").");
				return false;
			}
		}
		
		path = Paths.get(filePath);
		
		if (Files.exists(path)) {
			// File already exists, skip
			logger.info("File (" + path.toAbsolutePath() + ") already exists, skipping download.");
			return true;
		} else {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				logger.severe("Failed to create file (" + path.toAbsolutePath() + ").");
				return false;
			}
		}
		
		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			HttpRequest fileDownloadRequest = api.withAuth(HttpManager.getHttpRequestFactory().buildGetRequest(fileDownloadUrl));
			HttpResponse fileDownloadResponse = fileDownloadRequest.execute();
			InputStream is = fileDownloadResponse.getContent();
			
			ReadableByteChannel rbc = Channels.newChannel(is);
			
			FileChannel fc = fos.getChannel();
			fc.transferFrom(rbc, 0, Long.MAX_VALUE);
			
			System.out.println(filePath);
			
			return true;
		} catch (IOException e) {
			logger.severe("An exception was encountered while attempting to download (" + filePath + ") from (" + fileDownloadUrl.build() + ").");
			try {
				Files.delete(Paths.get(filePath));
			} catch (IOException e1) {
				logger.severe("Failed to clean up empty file (" + filePath + ") after failed download.");
			}
		}
		
		return false;
	}
	
	private String getFolderPath(String filePath) {
		String[] filePathSplit = filePath.split("\\\\");
				
		StringBuilder folderPathBuilder = new StringBuilder();
		for (int i = 0; i < filePathSplit.length - 1; i++) {
			folderPathBuilder.append(filePathSplit[i].trim()).append("\\");
		}
		
		return folderPathBuilder.toString();
	}
	
	public void downloadAllFiles() {
		for (DownloadUrl url : urlList) {
			downloadFile(url);
		}
	}
}
