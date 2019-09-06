package lumidl.connection;

import com.google.api.client.http.GenericUrl;

public class DownloadUrl {
	private String filePath;
	private GenericUrl url;
	
	public DownloadUrl(String fn, GenericUrl u) {
		filePath = verify(fn);
		url = u;
	}

	public String getFilePath() {
		return filePath;
	}

	public GenericUrl getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "[" + getFilePath() + "|" + getUrl() + "]";
	}
	
	private static String verify(String fn) {
		StringBuilder sb = new StringBuilder();
		String[] components = fn.split("\\\\");
		for (int i = 0; i < components.length; i++) {
			sb.append(components[i]);
			if (i < components.length - 1) {
				sb.append("\\");
			}
		}
		
		return sb.toString();
	}
}
