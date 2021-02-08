package utilities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    Properties configFile;

    public ConfigurationManager getConfigurations() {
        if (configFile == null) {
            System.out.println("Please set a configuration to continue!");
            return null;
        }
        return new ConfigurationManager();
    }

    public void setConfiguration(String configLocation) {
        configFile = new Properties();
        try (InputStream input = new FileInputStream(configLocation)) {
            configFile.load(input);
        } catch (Exception ex) {
            System.out.println("Failed to load the configuration file.");
            ex.printStackTrace();
        }

    }

    public String getProperty(String key) {
        return this.configFile.getProperty(key);
    }
}
