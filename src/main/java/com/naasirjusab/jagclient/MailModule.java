
package com.naasirjusab.jagclient;


import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.database.JagDaoModule;
import com.naasirjusab.jagclient.interfaces.Mailer;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.mail.*;


/*
* @author Naasir Jusab
* This class takes a config bean object to perform send and receive operations.
* Defines a MailModule class which does sending and receiving of emails based
* on the configurations of the ConfigBean class.
*/
public class MailModule implements Mailer  {
    
    private ConfigBean cb;
    private JagDaoModule db;
    
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    
    //Sets the config bean object
    public MailModule(ConfigBean c)
    {
        super();
        log.info("Creating configbean");
        cb = c;
        db = new JagDaoModule(cb);
        
    }
    
    
    
    /**
     * Forwards an email to recipient(s) with the attributes received by the user
     * @param receiverEmail the email that will be sent to
     * @param subject the subject of the email
     * @param text the text of the email
     * @param html the html of the email
     * @param cc the other cc recipients of the email
     * @param bcc the other bcc recipients of the email
     * @param embedded the embedded files
     * @param attachment the attached files
     * @return JagEmail which contains the email that was just sent
     */
    @Override        
    public JagEmail sendEmail(String receiverEmail, String subject, String text,String html,
                            String cc, String bcc, String embedded, String attachment)
    {
       //creates the email from the attributes received
       log.info("Creating mail object");
       JagEmail email = createTheEmail(receiverEmail,subject, text,html,
                            cc, bcc,  embedded,  attachment);
       
       //creates the smtp server
       log.info("Creating smtp server");
       SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(cb.getSmtpServerName(), cb.getSmtpPort())
                .authenticateWith(cb.getSenderEmail(), 
                                  cb.getSenderPassword());
       log.info("Smtp:" + cb.getSmtpServerName());
       log.info("Port:" + cb.getSmtpPort());
       log.info("SenderEmail:" + cb.getSenderEmail());
       log.info("SenderPassword:" + cb.getSenderPassword());
       
       //smtpServer.debug(true);
       
       //creates the session
       log.info("Creating smtp session");
       SendMailSession session = smtpServer.createSession();
       log.info("Created smtp session");

       
       //opens the session, sends the mail and closes the session
       log.info("Opening session");
       session.open();
       session.sendMail(email);
       session.close();
       log.info("Closing session");
       log.info("Sent mail");
       
       
        
       email.setFolderName("Sent");
       try{
            db.create(email);
        }catch(SQLException e)
        {
            log.info("Failed to insert in database");
        }
       log.info("Successfully sent");
       return email;
       
      
        
        
    }
    
    @Override
    public JagEmail[] receiveEmail()
    {
        JagEmail[] jag = null;
        
        //creates the imap server
        log.info("Creating imap server");
    
        ImapSslServer imapServer = new ImapSslServer(cb.getImapServerName(), 
                cb.getImapPort(), cb.getSenderEmail(), cb.getSenderPassword());
       
        
        //imapServer.setProperty("mail.debug", "true");

        //creates the session
        log.info("Creating session");
        ReceiveMailSession session = imapServer.createSession();
         
         session.open();
        // We only want messages that have not be read yet.
        // Messages that are delivered are then marked as read on the server
        log.info("Receiving emails");
        ReceivedEmail[] emails = session.receiveEmailAndMarkSeen(EmailFilter
                .filter().flag(Flags.Flag.SEEN, false));
        
        //write to file attachments
        
        //if emails is not empty convert them to a JagEmail[] else return null
        if(emails != null)
            jag = convert(emails);
        
        if(jag!= null)
        {
            
            try{
                for(JagEmail email:jag)
                    db.create(email);
            }catch(SQLException e)
            {
                log.info("Failed to write to database");
            }

            
        }
        
        
        log.info("Received all emails");
        return jag;
    }
    
    private JagEmail[] convert(ReceivedEmail[] re)
    {
        
        log.info("Converting emails");
        
        //length of the received email array
        int length = re.length;
        //instantiates a JagEmail[]
        JagEmail[] jagArray = new JagEmail[length];
        
        //does the conversion of the received email to a jag email by setting
        //the values of the jag email to be the same as the received email
        for(int i =0; i < length; i++)
        {
            jagArray[i] = new JagEmail();
            if(re[i].getAttachments() != null)
                jagArray[i].setAttachments(new ArrayList<> (re[i].getAttachments()));
            jagArray[i].setAttachMsg(re[i].getAttachedMessages());
            jagArray[i].setFlag(re[i].getFlags());
            jagArray[i].setMessageNumber(re[i].getMessageNumber());
            jagArray[i].setReceiveDate(re[i].getReceiveDate());
            jagArray[i].setFolderName("Inbox");
            jagArray[i].setCc(re[i].getCc());
            jagArray[i].setBcc(re[i].getBcc());
            jagArray[i].setTo(re[i].getTo());
            jagArray[i].setSubject(re[i].getSubject());
            jagArray[i].setFrom(re[i].getFrom());
            
            List<EmailMessage> content = re[i].getAllMessages();
            for(int j = 0; j < content.size(); j++)
            {
                //Check if message is plain text or html, then set correct type
                if(content.get(j).getMimeType().equalsIgnoreCase("TEXT/PLAIN"))
                    jagArray[i].addText(content.get(j).getContent().replace("\n", "").replace("\r", ""));
                else if(content.get(j).getMimeType().equalsIgnoreCase("TEXT/HTML"))
                    jagArray[i].addHtml(content.get(j).getContent().replace("\n", "").replace("\r", ""));
            }
            
            
        }
        log.info("Converting emails complete");
        return jagArray;
    }
        
    
    
    private JagEmail createTheEmail(String receiverEmail, String subject, String text,String html,
                            String cc, String bcc, String embedded, String attachment)
    {
       log.info("Creating emails");
       
       
       if(receiverEmail == null  || receiverEmail.length()==0 )
            throw new IllegalArgumentException("Invalid arguments");
        
       //if no subject is provided then it is defaulted to no subject
       if(subject == null || subject.length() == 0)
       {
           subject = "No subject";
       }
       
       JagEmail email = new JagEmail();
       //creates the email object with the attributes received
       if(receiverEmail == null || receiverEmail.length()==0)
       {
        email.from(cb.getSenderEmail())
                .subject(subject);
        log.info("Added subject");
       }
       
       else
       {
           String[] receiverEmails = receiverEmail.split(",");
            email.from(cb.getSenderEmail())
                .to(receiverEmails)
                .subject(subject);
            log.info("Added subject and destination email");
       }
           
       
       
          
       // must have an html or text body, throws an exception if they are not 
       //valid. At least one of them needs to be valid so the email has a message
       //body
       if((text == null || text.length() == 0) && (html == null || html.length() == 0))
           throw new IllegalArgumentException("Must have a body argument");
       
       //if text is valid then add it to the email
       if(text != null && text.length() != 0)
       { 
           email.addText(text);
           log.info("Text Added");
       }
       //if html is valid then add it to the email
       if(html != null && html.length() != 0)
       {   
           
           email.addHtml(html);
           log.info("Html added");
       }
       
       
       
      
       //if cc is valid then split the string to get each cc and add them to the
       //email
       if(cc != null && cc.length() !=0)
       {
           String[] ccs = cc.split(",");
           email.cc(ccs);
           log.info("CC added");
       }
       
       //if bcc is valid then split the string to get each bcc and add them to 
       //the email
       if(bcc != null && bcc.length() !=0)
       {
           String[] bccs = bcc.split(",");
           email.bcc(bccs);
           log.info("BCC added");
       }
       
       
       //if embedded is valid then split the string and add each embed 
       //recursively to the email
       if(embedded != null&& embedded.length() !=0)
       {
           String[] embeds = embedded.split(",");
           
           for(String embed : embeds)
           {
               email.embed(EmailAttachment.attachment().bytes(new File(embed)));
               log.info("Embed added");
           
           }
       }
       
       //if attachment is valid then split the string and add each attachment 
       //recursively to the email
       
       if(attachment != null && attachment.length() !=0)
       {
           String[] attachments = attachment.split(",");
           for(String attach : attachments )
           {
               email.attach(EmailAttachment.attachment().file(attach));
               log.info("Attach added");
           }
       }
        
        log.info("Creating mail complete");
        return email;
        
    }

  
  
     public void setDatabase(JagDaoModule db)
    {
        this.db = db; 
    }
  
    
    
  
    
}
