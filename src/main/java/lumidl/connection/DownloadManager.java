package lumidl.connection;

import java.util.List;

/** 
 * Handles file downloads, recovers from expected situations 
 * e.g. Folder path does not exist, file already exists, etc.
 * @author dongyu
 *
 */
public class DownloadManager {
	private List<DownloadUrl> urlList;
	
	
	public DownloadManager(List<DownloadUrl> urlList) {
		this.urlList = urlList;
	}
	
	// TODO
}
