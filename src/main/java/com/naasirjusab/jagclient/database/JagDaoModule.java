
package com.naasirjusab.jagclient.database;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.interfaces.JagDAO;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Naasir Jusab
 * This class takes a configuration bean object.
 * Defines a JagDaoModule class which does CRUD based on the configurations
 * of the ConfigBean class
 */
public class JagDaoModule implements JagDAO{
    private ConfigBean cb;
    
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    
    //sets the config bean object
    public JagDaoModule(ConfigBean cb)
    {
        super();
        log.info("Creating configbean");
        this.cb = cb;
        
    }
    
    /**
     * 
     * @param email receives a jag email object that will be written to the db
     * @return records returns the number of rows affected by the query
     * @throws SQLException a SQLException may occur when writing to the db
     */
    @Override
    public int create(JagEmail email) throws SQLException
    {
        
        int records=0;
        String query="INSERT into jagEmail(senderEmail,receiverEmail,emailSubject,emailText,emailHtml,cc,bcc,folderName,mailDate) VALUES(?,?,?,?,?,?,?,?,?)";
        log.info("Running jagEmail create");
        try(Connection conn = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(),cb.getSqlPassword());
                PreparedStatement ps = conn.prepareStatement(query, 
                    Statement.RETURN_GENERATED_KEYS);)
        {       
            
            ps.setString(1, email.getFrom().getEmail());
            
            
            String tos = "";
            for(MailAddress to : email.getTo())
                tos += to.getEmail() + " ";
            ps.setString(2, tos);
            if(tos.length() != 0)
                ps.setString(2,tos);
            else
                ps.setString(2, null);
            
            ps.setString(3, email.getSubject());
            
            StringBuilder sBuilder = new StringBuilder();
            
            List<EmailMessage> list = email.getAllMessages();
            for(EmailMessage message : list)
            {
                if(message.getMimeType().equalsIgnoreCase("text/plain"))
                    sBuilder.append(message.getContent());
            }
            if(sBuilder.toString().length() != 0)
                ps.setString(4, sBuilder.toString());
            else
                ps.setString(4, null);
            
            StringBuilder sBuilder2 = new StringBuilder();
            
            for(EmailMessage message : list)
            {
                if(message.getMimeType().equalsIgnoreCase("text/html"))
                    sBuilder2.append(message.getContent());
            }
            
            if(sBuilder2.toString().length() != 0)
                ps.setString(5, sBuilder2.toString());
            else
                ps.setString(5, null);
            
            
            String ccs = "";
            for(MailAddress cc : email.getCc())
                ccs += cc.getEmail() + " ";
            if(ccs.length() != 0)
                ps.setString(6, ccs);
            else
                ps.setString(6,null);
            String bccs = "";
            for(MailAddress bcc : email.getBcc())
                bccs += bcc.getEmail() + " ";
            if(bccs.length() != 0)
                ps.setString(7, bccs);
            else
                ps.setString(7, null);
            ps.setString(8, email.getFolderName());
            if(email.getReceiveDate() != null)
            {
                  Timestamp receivedDate = new Timestamp(email.getReceiveDate()
                        .getTime());
                ps.setTimestamp(9, receivedDate);
            }
            
            else
                ps.setTimestamp(9, null);
            
            
            records = ps.executeUpdate();
            log.info("Added everything but attachments");
            log.info("rows affected: " + records );
            if(records == 1){
                int id = -1;
                ResultSet rs = ps.getGeneratedKeys();
        
                if(rs.next())
                    id = rs.getInt(1);
                if(id != -1){
                    addEmailAttachments(id, email);
                    email.setID(id);
                }
            }    
        }
        
            
        
        
         catch(SQLException e){

             log.error("Error: ", e.getCause());
        }
        
        log.info("Finished adding");
        return records;
        
        
    }
    
    /**
     * 
     * @param folderName receives folder names that will be selected
     * @return selectedJagEmails returns a list of jagEmails that respect a 
     *          condition
     * @throws SQLException a SQLException may occur when reading from the db
     */
    @Override
    public ArrayList<JagEmail> readJagEmail(String folderName) 
            throws SQLException
    {
        log.info("Running readJagEmail");
        ArrayList<JagEmail> readingEmails = new ArrayList<>();
        String query ="SELECT emailId, senderEmail, receiverEmail, emailSubject"
              + ",emailText,emailHtml, cc, mailDate FROM jagEmail WHERE folderName = ?";
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
            ps.setString(1, folderName);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    JagEmail jagEmail = new JagEmail();
                    int id = rs.getInt(1);
                    jagEmail.setID(id);
                    jagEmail.from(rs.getString(2));
                    if(rs.getString(3) != null)
                        jagEmail.to(rs.getString(3));
                    jagEmail.subject(rs.getString(4));
                    if(rs.getString(5) != null)
                        jagEmail.addText(rs.getString(5));
                    if(rs.getString(6) != null)
                        jagEmail.addHtml(rs.getString(6));
                    if(rs.getString(7)!= null)
                        jagEmail.cc(rs.getString(7));                   
                    if(rs.getTimestamp(8) != null)
                        jagEmail.setReceiveDate(rs.getTimestamp(8));
                    readingEmailAttachments(id, jagEmail);
                   readingEmails.add(jagEmail);
                }
            }
            catch(SQLException e){
            log.error("Error: ", e.getCause());
            }
        }
        catch(SQLException e){
            log.error("Error: ", e.getCause());
        }
        
        log.info("Finished reading jagEmails");
        return readingEmails;
    }
    
    /**
     * 
     * @param emailId receives the emailId of a jagEmail that will updated
     * @param folderName receives a folderName that will be updated in the table
     * @return records returns the number of rows affected by the query
     * @throws SQLException a SQLException may occur when you update in the db
     */
    @Override
    public int updateFolderNames(int emailId, String folderName) throws SQLException
    {
        log.info("Updating folder names");
        int records = 0;
        String query = "UPDATE jagEmail SET folderName = ? WHERE emailId =?";
                
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
            ps.setString(1, folderName);
            ps.setInt(2, emailId);
            records = ps.executeUpdate();
            log.info("Updated folder names");
            log.info("rows affected: "+records);
        }
        catch(SQLException e){
            log.error("Error: ", e.getCause());
        }
        return records;
        
    }
    
    
    /**
     * 
     * @param emailId receives the emailId of a jagEmail that will be deleted
     * @return records returns the number of rows affected by the query
     * @throws SQLException a SQLException may occur when you delete in the db
     */
    @Override
    public int delete(int emailId) throws SQLException
    {
        log.info("Deleting records");
        int records = 0;
        String query = "DELETE FROM jagEmail WHERE emailId = ?";
        
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(), 
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
            ps.setInt(1, emailId);
            records = ps.executeUpdate();
            log.info("Delete complete");
            log.info("rows affected: "+ records);
        }
        catch(SQLException e){
            log.error("Error: ", e.getCause());
        }
        return records;
    }
    
    /**
     * 
     * @param folderName receives the folderName that will be written to the 
     *          database jagFolder table
     * @return records returns the number of rows affected by the query
     * @throws SQLException a SQLException may occur when you delete in the db
     */
    @Override
    public int create(String folderName) throws SQLException
    {
        log.info("Creating folders");
        int records = 0;
        String query ="INSERT INTO jagFolder(folderName, userEmail) VALUES(?,?)";
                
        
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(), 
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
            ps.setString(1, folderName);
            ps.setString(2, cb.getSenderEmail());
            records = ps.executeUpdate();
            log.info("Added folder names");
            log.info("rows affected: " + records);
        }
        catch(SQLException e){
            log.error("Error: ", e.getMessage());
        }
        return records;
    }
    
    
    /**
     * 
     * @return createdFolders returns the createdFolders for a specific email
     * @throws SQLException a SQLException may occur when you delete in the db
     */
    @Override
    public ArrayList<String> readFolders() throws SQLException
    {
        log.info("Reading folders");
        
        log.info(cb.getSqlUrl());
        log.info(cb.getSqlUsername());
        log.info(cb.getSqlPassword());
        ArrayList<String> createdFolders = new ArrayList<String>();
        String query = "SELECT folderName FROM jagFolder WHERE userEmail = ?";
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
                ps.setString(1, cb.getSenderEmail());
                ResultSet rs = ps.executeQuery();
            while(rs.next())
            {   
                createdFolders.add(rs.getString("folderName"));
            }
        }
        catch(SQLException e){
            log.error("Error: ", e.getCause());
        }
        log.info("Finished reading folders");
        return createdFolders;
    }
    
    /**
     * 
     * @param folderName the folderName that will be deleted
     * @return  records returns the number of rows affected by the query
     * @throws SQLException a SQLException may occur when you delete in the db
     */
    @Override
    public int delete(String folderName) throws SQLException
    {
        log.info("Deleting folders");
        int records = 0;
        String query = "DELETE FROM jagFolder WHERE folderName = ? AND "
                + "userEmail = ?";
        
        try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(), 
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
            ps.setString(1, folderName);
            ps.setString(2, cb.getSenderEmail());
            records = ps.executeUpdate();
            log.info("Deleted folder");
            log.info("rows affected: " + records);
        }
        catch(SQLException e){
            log.error("Error: ", e.getCause());
        }
        return records;
    }
    
    private void addEmailAttachments(int emailId, JagEmail email) throws SQLException
    {
        log.info("Adding attachments");
        List<EmailAttachment> emailAttachments = email.getAttachments();
        
        if(emailAttachments != null){
            try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(),cb.getSqlPassword())){
                String query = "INSERT INTO jagAttachment VALUES (null,?,?,?)";
                for(EmailAttachment attach : emailAttachments){
                    byte[] attachmentFile = attach.toByteArray();
                    String attachmentName = attach.getName();
                    
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setString(1, attachmentName);
                    ps.setBytes(2, attachmentFile);
                    ps.setInt(3, emailId);  
                    
                    int records = ps.executeUpdate();
                    log.info("Added attachments");
                    log.info("rows affected:"+ records);
                }
            }
            catch(SQLException e){
                log.error("Error: ", e.getCause());
            }
        }         
    }
    
    private void readingEmailAttachments(int emailId, JagEmail jagEmail) throws SQLException{
        log.info("Reading attachments");
        String query = "SELECT attachmentContent, attachmentName FROM "
                + "jagAttachment WHERE emailId = ?";
         try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(), 
                cb.getSqlUsername(), cb.getSqlPassword());
                PreparedStatement ps = connection.prepareStatement(query);){
             ps.setInt(1, emailId);
             try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Blob content = rs.getBlob(1);
                    byte[] byteStream = content.getBytes(
                            1, (int)content.length());
                    String attachmentName = rs.getString(2);
                    
                    jagEmail.attach(EmailAttachment.attachment()
                            .bytes(byteStream).setName(attachmentName).create());
                }
            }
            catch(SQLException e){
                log.error("Error: ", e.getCause());
            }
        }
        catch(SQLException e){
           log.error("Error: ", e.getCause());
        }
         
         log.info("Finsihed Reading attachments");
    }
    
}
