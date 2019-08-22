package lumidl.connection;

import com.google.api.client.http.GenericUrl;

public class DownloadUrl {
	private String filePath;
	private GenericUrl url;
	
	public DownloadUrl(String fn, GenericUrl u) {
		filePath = fn;
		url = u;
	}

	public String getFilePath() {
		return filePath;
	}

	public GenericUrl getUrl() {
		return url;
	}	
}
