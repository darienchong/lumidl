package lumidl.connection;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

/**
 * Wrapper class for HTTP capabilities.
 * Implements the Singleton pattern for the HTTP transport and request factory.
 * Adds several helper functions for ease of use.
 * @author dongyu
 *
 */
public final class HttpManager {
	private static HttpTransport transport;
	private static HttpRequestFactory factory;
	
	private HttpManager() {
	}
	
	public static HttpTransport getTransport() {
		if (transport == null) {
			transport = new ApacheHttpTransport();
		}
		
		return transport;
	}
	
	public static HttpRequestFactory getHttpRequestFactory() {
		if (factory == null) {
			factory = getTransport().createRequestFactory();
		}
		
		return factory;
	}	
	
	// ======================================  Helper functions ====================================== \\	
	
	public static HttpRequest buildGetRequest(String url) throws IOException {
		return HttpManager.getHttpRequestFactory().buildGetRequest(new GenericUrl(url));
	}
}
