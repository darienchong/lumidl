package lumidl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lumidl.connections.Api;
import lumidl.connections.Login;

public class Main {
	private static final String PROPERTIES_PATH = "./bin/main/config.properties";
	
	public static void main(String[] args) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(PROPERTIES_PATH));
			// TODO: Add user input for password, saving over username/password.
			
			Login log = new Login();
			log.login(prop.getProperty("username"), prop.getProperty("password"));
			Api api = log.generateApiObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
