package lumidl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import lumidl.connection.Api;
import lumidl.connection.DownloadManager;
import lumidl.connection.Login;
import lumidl.model.Module;
import lumidl.util.Constants;
import lumidl.util.LoggerFactory;

public class Main {
	private static final boolean logAuthInfo = false;
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(Main.class);
		
		try {
			// TODO: Add user input for password, saving over username/password.
			Constants constants = new Constants();
			Login log = new Login(constants.getUsername(), constants.getPassword());
			log.execute();
			Api api = log.generateApiObject();
			
			if (logAuthInfo) {
				logger.info("Subscription Key = " + log.getSubscriptionKey() + "");
				logger.info("Access Token = " + log.getAccessToken() + "");
			}
			
			List<Module> modules = api.getModules();
			
			for (Module module : modules) {
				System.out.println("\n### " + module.name + " ###\n");
				
				DownloadManager downloadManager = new DownloadManager(api, api.getDownloads(module));
				downloadManager.downloadAllFiles();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\nDone!\n");
	}
}
