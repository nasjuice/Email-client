
package com.naasirjusab.jagclient.beans;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Naasir Jusab
 * This class takes a name, smtpServerName, imapServerName, senderEmail,
 * senderPassword,imapPort, smtpPort. Defines a ConfigBean class which holds all 
 * the configurations necessary for the sending/receiving of a mail.
 */
public class ConfigBean  {
    private StringProperty name;
    private StringProperty smtpServerName;
    private StringProperty imapServerName;
    private StringProperty senderEmail;
    private StringProperty senderPassword;
    private StringProperty sqlUrl;
    private IntegerProperty sqlPort;
    private StringProperty sqlUsername;
    private StringProperty sqlPassword;
    private IntegerProperty imapPort;
    private IntegerProperty smtpPort;
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

   
    //Sets all the values of the fields by calling their setters to do the 
    //necessary validation
    public ConfigBean(String name,String senderEmail, String senderPassword, 
            String smtpServerName,String imapServerName, int imapPort, 
            int smtpPort, String sqlUsername, String sqlPassword, String
                    sqlUrl, int sqlPort)
    {
        super();
        this.name = new SimpleStringProperty(name);
        this.smtpServerName = new SimpleStringProperty(smtpServerName);
        this.imapServerName = new SimpleStringProperty(imapServerName);
        this.senderEmail = new SimpleStringProperty(senderEmail);
        this.senderPassword = new SimpleStringProperty(senderPassword);
        this.imapPort = new SimpleIntegerProperty(imapPort);
        this.smtpPort = new SimpleIntegerProperty(smtpPort);
        this.sqlUsername = new SimpleStringProperty(sqlUsername);
        this.sqlPassword = new SimpleStringProperty(sqlPassword);
        this.sqlUrl = new SimpleStringProperty(sqlUrl);
        this.sqlPort = new SimpleIntegerProperty(sqlPort);
    }
    
    public ConfigBean()
    {
        this("","dabaws777@gmail.com","donutman87","smtp.gmail.com","imap.gmail.com",993,465,"CS1433545","otrostio","jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/cs1433545",3306);
        
    }
    
    /**
     * Returns the port of SQL
     * @return integer that contains the port of SQL
     */
    public int getSqlPort() {
        return sqlPort.get();
    }
    
    /**
     * Returns the SQL port property
     * @return IntegerProperty that contains the SQL port property
     */
    public IntegerProperty getSqlPortProperty()
    {
        return sqlPort;
    }
    
    
    
    /** Returns the URL of SQL
     * 
     * @return string that contains the URL of SQL 
     */
    public String getSqlUrl() {
        return sqlUrl.get();
    }
    
    /**
     * Returns the SQL URL property
     * @return StringProperty that contains the SQL URL property
     */
    public StringProperty getSqlUrlProperty()
    {
        return sqlUrl;
    }
    
     
    /**
    * Returns the name given to the configuration bean
    * @return string that contains the name
    */
    public String getName() {
        return name.get();
    }
    
    /**
     * Returns the name property of the configuration bean
     * @return StringProperty that contains the name property
     */
    public StringProperty getNameProperty()
    {
        return name;
    }

    /**
    * Returns the name of an SMTP server
    * @return string that contains the name of an SMTP server
    */
    public String getSmtpServerName() {
        return smtpServerName.get();
    }
    
    /**
     * Returns the name property of an SMTP server
     * @return StringProperty that contains the name property of an SMTP server
     */
    public StringProperty getStmpServerNameProperty()
    {
        return smtpServerName;
    }

    /**
    * Returns the name of an IMAP server
    * @return string that contains the name of an IMAP server
    */
    public String getImapServerName() {
        return imapServerName.get();
    }
    
    /**
     * Returns the name property of an IMAP server
     * @return StringProperty that contains the name property of an IMAP server
     */
    public StringProperty getImapServerNameProperty()
    {
        return imapServerName;
    }

    /**
    * Returns the sender email, the email address that will be sending from
    * @return string that contains the sender email
    */
    public String getSenderEmail() {
        return senderEmail.get();
    }
    
    /**
     * Returns the sender email property
     * @return StringProperty that contains the sender email property
     */
    public StringProperty getSenderEmailProperty()
    {
        return senderEmail;
    }

    /**
    * Returns the sender password, the password that will be sending from
    * @return string that contains the sender password
    */
    public String getSenderPassword() {
        return senderPassword.get();
    }
    
    /**
     * Returns the sender password property
     * @return StringProperty that contains the sender password property
     */
    public StringProperty getSenderPasswordProperty()
    {
        return senderPassword;
    }

    /**
    * Returns IMAP port that will be used
    * @return integer that contains the IMAP port
    */
    public int getImapPort() {
        return imapPort.get();
    }
    
    /**
     * Returns the IMAP port property
     * @return IntegerProperty that contains the IMAP port property
     */
    public IntegerProperty getImapPortProperty()
    {
        return imapPort;
    }
    
    /**
     * Returns the username of the SQL database
     * @return String that contains the SQL database username
     */
    public String getSqlUsername() {
        return sqlUsername.get();
    }
    
    /**
     * Returns the username property of the SQL database
     * @return StringProperty that contains the SQL database username property
     */
    public StringProperty getSqlUsernameProperty()
    {
        
        return sqlUsername;
    }
    
   
    /**
     * Returns the password of the SQL database
     * @return String that contains the SQL database password
     */
    public String getSqlPassword() {
        return sqlPassword.get();
    }
    
    /**
     * Returns the password property of the SQL database
     * @return StringProperty that contains the SQL database password property
     */
    public StringProperty getSqlPasswordProperty()
    {
        return sqlPassword;
    }

    /**
    * Returns SMTP port that will be used
    * @return integer that contains the SMTP port
    */
    public int getSmtpPort() {
        return smtpPort.get();
    }
    
    /**
     * Returns SMTP port property
     * @return IntegerProperty that contains the SMTP port property
     */
    public IntegerProperty getSmtpPortProperty()
    {
        return smtpPort;
    }
    
    /**
     * Modifies the name 
     * @param name String that will modify the name 
     */
    public void setName(String name) {

        this.name.set(name);
    }
    
  
    
    /**
     * Modifies the username of the SQL database
     * @param sqlUsername String that will modify the SQL username
     */
    public void setSqlUsername(String sqlUsername) {
        this.sqlUsername.set(sqlUsername);
    }
    

    
    /**
     * Modifies the password of the SQL database
     *
     * @param sqlPassword String that will modify the SQL password 
     */
    public void setSqlPassword(String sqlPassword) {
        this.sqlPassword.set(sqlPassword);
    }
    

    
    /**
     * Modifies the URL of SQL
     * @param sqlUrl String that modifies the URL of SQL
     */
    public void setSqlUrl(String sqlUrl) {
        this.sqlUrl.set(sqlUrl);
    }
   
    
    
    
    /**
     * Modifies the SQL port 
     * @param sqlPort Integer that modifies the SQL port
     */
     public void setSqlPort(int sqlPort) {
        this.sqlPort.set(sqlPort);
    }
     

    /**
    * Modifies the SMTP server name 
    * @param smtpServerName String that modifies the SMTP server name
    */
    public void setSmtpServerName(String smtpServerName) 
    {
        
        this.smtpServerName.set(smtpServerName);
    }
    

    /**
    * Modifies the IMAP server name 
    * @param imapServerName String that modifies the IMAP server name
    */
    public void setImapServerName(String imapServerName) 
    {
        this.imapServerName.set(imapServerName);
    }
    


    /**
    * Modifies the sender email 
    * @param senderEmail String that modifies the sender email 
    */
    public void setSenderEmail(String senderEmail) 
    {
        this.senderEmail.set(senderEmail);
    }

    /**
    * Modifies the sender password 
    * @param senderPassword String that modifies the sender password
    */
    public void setSenderPassword(String senderPassword) 
    {
        this.senderPassword.set(senderPassword);
    }
    

    /**
    * Modifies the IMAP port 
    * @param imapPort Integer that modifies the IMAP port
    */
    public void setImapPort(int imapPort) 
    {      
        this.imapPort.set(imapPort);

    }

    /**
    * Modifies the SMTP port 
    * @param smtpPort Integer that modifies the SMTP port
    */
    public void setSmtpPort(int smtpPort) 
    {
 
        this.smtpPort.set(smtpPort);
    }
    

    
    
    
}
