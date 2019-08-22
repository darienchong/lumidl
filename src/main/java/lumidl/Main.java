package lumidl;

import java.io.IOException;
import java.util.logging.Logger;

import lumidl.connection.Api;
import lumidl.connection.Login;

import static lumidl.connection.Constants.*;

public class Main {
	public static void main(String[] args) {
		Logger logger = Logger.getLogger("lumidl.Main");
		
		try {
			// TODO: Add user input for password, saving over username/password.
			
			Login log = new Login(USERNAME, PASSWORD);
			log.execute();
			Api api = log.generateApiObject();
			
			logger.info("Subscription Key = " + log.getSubscriptionKey() + "");
			logger.info("Access Token = " + log.getAccessToken() + "");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
