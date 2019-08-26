package lumidl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import lumidl.connection.Api;
import lumidl.connection.DownloadManager;
import lumidl.connection.Login;
import lumidl.model.Module;

import static lumidl.connection.Constants.*;

public class Main {
	private static final boolean logAuthInfo = false;
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("lumidl.Main");
		
		try {
			// TODO: Add user input for password, saving over username/password.
			
			Login log = new Login(USERNAME, PASSWORD);
			log.execute();
			Api api = log.generateApiObject();
			
			if (logAuthInfo) {
				logger.info("Subscription Key = " + log.getSubscriptionKey() + "");
				logger.info("Access Token = " + log.getAccessToken() + "");
			}
			
			List<Module> modules = api.getModules();
			
			for (Module module : modules) {
				DownloadManager downloadManager = new DownloadManager(api.getDownloads(module));
				// TODO
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
