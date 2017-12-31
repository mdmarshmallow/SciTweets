package properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class RetrieveProperties {

	//function gets a property from a given filename and property name
	private String getProperties(String filename, String property) throws IOException {
		File configFile = new File(getClass().getClassLoader().getResource(filename).getFile());
		FileReader reader = new FileReader(configFile);
		Properties prop = new Properties();
		prop.load(reader);
		return prop.getProperty(property);
	}

	//these functions make it easier to get properties without having to type in the specific filename and property name
	public String getAylienKey() throws FileNotFoundException, IOException {
		return getProperties("Aylien.properties", "KEY");
	}

	public String getAylienAppID() throws FileNotFoundException, IOException {
		return getProperties("Aylien.properties", "APP_ID");
	}

	public String getAdminUsername() throws IOException {
		return getProperties("AdminCredentials.properties", "Username");
	}

	public String getAdminPassword() throws IOException {
		return getProperties("AdminCredentials.properties", "Password");
	}

	public String getDBURL() throws IOException {
		return getProperties("DBCredentials.properties", "dbURL");
	}
	
	public String getDBUsername() throws IOException {
		return getProperties("DBCredentials.properties", "dbUser");
	}
	
	public String getDBPass() throws IOException {
		return getProperties("DBCredentials.properties", "dbPassword");
	}
	
	public String getSciTweetsModelPath() throws IOException {
		return getProperties("scitweets_model.properties", "scitweets_model_path");
	}
}
