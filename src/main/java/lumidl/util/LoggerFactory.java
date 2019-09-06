package lumidl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerFactory {
	public static final Level LOG_LEVEL = Level.OFF;
	
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(clazz.getSimpleName());
		logger.setLevel(LOG_LEVEL);
		/*
		Properties prop = new Properties();
		FileHandler fh;
		
		// TODO: Fix logging to file
		try (FileInputStream fis = new FileInputStream(Constants.PROPERTIES_PATH)) {
			prop.load(fis);
			String s = prop.getProperty("logPath");
			String logFileName = "LOG_" + new SimpleDateFormat("dd-MM-yyyy").format(new Date()).replaceAll("-", "_") + ".txt";
			String logFileFullPath = s + "\\" + logFileName;
			
			Path path = Paths.get(logFileFullPath);
			if (!Files.exists(path)) {
				Files.createFile(path);
			}
			
			fh = new FileHandler(logFileFullPath, true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return logger;
	}
}
