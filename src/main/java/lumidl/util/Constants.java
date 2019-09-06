package lumidl.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Constants {
	// LumiNUS API URLs, paths, etc.
	public static final String API_BASE_URL = "https://luminus.azure-api.net";
	public static final String GET_RESOURCES_PATH = "/module";
	public static final String ALL_FILES_PATH = "/files/%s/allfiles";
	public static final String GET_FILES_PATH = "/files/%s/file";
	public static final String GET_DOWNLOAD_URL_PATH = "/files/file/%s/downloadurl";
	public static final String GET_FOLDERS_PATH = "/files/?ParentID=";
	
	// LumiNUS URLs
	public static final String ADFS_TOKEN_PATH = "/login/adfstoken";
	
	// Params and URLs for accessing ADFS token portal
	public static final String ADFS_OAUTH2_URL = "https://vafs.nus.edu.sg/adfs/oauth2/authorize";
	public static final String ADFS_GRANT_TYPE = "authorization_code";
	public static final String ADFS_CLIENT_ID = "E10493A3B1024F14BDC7D0D8B9F649E9-234390";
	public static final String ADFS_SCOPE = "";
	public static final String ADFS_RESOURCE = "sg_edu_nus_oauth";
	public static final String ADFS_RESPONSE_TYPE = "code";
	public static final String ADFS_REDIRECT_URL = "https://luminus.nus.edu.sg/auth/callback";
	
	// The subscription key to use, and the appropriate header name.
	public static final String OCP_APIM_SUBSCRIPTION_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
	public static final String OCP_APIM_SUBSCRIPTION_KEY = "6963c200ca9440de8fa1eede730d8f7e";
	
	public static final String PROPERTIES_PATH = "./bin/main/config.properties";
	
	// These need reading into from a .properties file, so we can't leave them as static vars.
	// To access them, we should instantiate a Constants object and call getters.
	private String username;
	private String password;
	private String downloadPath;
	
	private Logger logger;
	
	public Constants() {
		logger = LoggerFactory.getLogger(this.getClass());
		Properties properties = new Properties();
		try (FileInputStream fis = new FileInputStream(PROPERTIES_PATH)){
			properties.load(fis);
			
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			downloadPath = properties.getProperty("path");
		} catch (FileNotFoundException e) {
			System.err.println("The .properties file was not found. Unable to retrieve user information.");
		} catch (IOException e) {
			System.err.println("An exception was encountered while trying to open the .properties file.");
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDownloadPath() {
		return downloadPath;
	}
}
