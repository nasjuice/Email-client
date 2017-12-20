
package com.naasirjusab.jagclient.fileManagement;

import com.naasirjusab.jagclient.beans.ConfigBean;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import java.nio.file.Path;
import java.util.Properties;
import static java.nio.file.Paths.get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class takes care of reading and writing to a properties file that will
 * insert its data to the ConfigBean.
 * @author Naasir Jusab
 */
public class PropertiesManager {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Returns a ConfigBean object with the contents of the properties file
     *
     * @param path of the properties file that it will read from
     * @param propFileName name of the properties file
     * @return The bean that contains the properties
     * @throws IOException
     */
    public final ConfigBean loadTextProperties(final String path, final String propFileName) throws IOException {

        Properties prop = new Properties();

        Path txtFile = get(path, propFileName + ".properties");

        ConfigBean mailConfig = new ConfigBean();

        // File must exist
        if (Files.exists(txtFile)) {
            try (InputStream propFileStream = newInputStream(txtFile);) {
                prop.load(propFileStream);
            }
            mailConfig.setName(prop.getProperty("name"));
            mailConfig.setSenderEmail(prop.getProperty("senderEmail"));
            mailConfig.setSenderPassword(prop.getProperty("senderPassword"));
            mailConfig.setSmtpServerName(prop.getProperty("smtpServerName"));
            mailConfig.setImapServerName(prop.getProperty("imapServerName"));
            mailConfig.setImapPort(Integer.parseInt(prop.getProperty("imapPort")));
            mailConfig.setSmtpPort(Integer.parseInt(prop.getProperty("smtpPort")));
            mailConfig.setSqlUsername(prop.getProperty("sqlUserName"));
            mailConfig.setSqlPassword(prop.getProperty("sqlPassword"));
            mailConfig.setSqlUrl(prop.getProperty("sqlUrl")); 
            mailConfig.setSqlPort(Integer.parseInt(prop.getProperty("sqlPort")));
        }
        
        else
            return null;
        
        return mailConfig;
    }

    /**
     * Creates a plain text properties file based on the parameters
     * of the ConfigBean object
     *
     * @param path of the properties that will be written to
     * @param propFileName name of the properties file
     * @param mailConfig The bean to store into the properties
     * @throws IOException
     */
    public final void writeTextProperties(final String path, final String propFileName, final ConfigBean mailConfig) throws IOException {

        Properties prop = new Properties();

        prop.setProperty("name", mailConfig.getName());
        prop.setProperty("senderEmail", mailConfig.getSenderEmail());
        prop.setProperty("senderPassword", mailConfig.getSenderPassword());
        prop.setProperty("smtpServerName", mailConfig.getSmtpServerName());
        prop.setProperty("imapServerName", mailConfig.getImapServerName());
        prop.setProperty("imapPort", mailConfig.getImapPort() + "");
        prop.setProperty("smtpPort", mailConfig.getSmtpPort() + "");
        prop.setProperty("sqlUserName", mailConfig.getSqlUsername());
        prop.setProperty("sqlPassword", mailConfig.getSqlPassword());
        prop.setProperty("sqlUrl", mailConfig.getSqlUrl());
        prop.setProperty("sqlPort", mailConfig.getSqlPort()+ "");

        Path txtFile = get(path, propFileName + ".properties");

        // Creates the file or if file exists it is truncated to length of zero
        // before writing
        try (OutputStream propFileStream = newOutputStream(txtFile)) {
            prop.store(propFileStream, "ConfigBean Properties");
        }
    }
    
}
