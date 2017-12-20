/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naasirjusab.jagclienttest;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.MailModule;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Naasir Jusab
 */

@RunWith(Parameterized.class)
public class SendMailTest {
   
    private String receiverEmail;
    private String subject;
    private String text;
    private String html;
    private String cc;
    private String bcc;
    private String embedded;
    private String attachment;
    
    private final Logger log = LoggerFactory.getLogger(getClass().getName());


    private MailModule mm;

    private ConfigBean cb;
    
    public SendMailTest(String receiverEmail, String subject, String text,String html,
                            String cc, String bcc, String embedded, String attachment) {
        this.receiverEmail = receiverEmail;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.cc = cc;
        this.bcc = bcc;
        this.embedded = embedded;
        this.attachment = attachment;


       
        
         cb = new ConfigBean("Nas","dabaws777@gmail.com","donutman87",
    "smtp.gmail.com","imap.gmail.com", 993,465, "CS1433545", "otrostio","jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/cs1433545", 3306);
        
         mm = new MailModule(cb);
    }
    

    /**
     * not testing embeds because they disappear
     * @return 
     */
    @Parameters 
    public static Collection<Object[]> data()
    {
       
        String[] data = {"dabaws777@gmail.com", "Subject","Text Message", 
                "<html><body><h1>Html</h1></body></html>", 
                "williamngoreceive@gmail.com", "shiftkun662@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
          
        
        String[] data2 = {"dabaws777@gmail.com", "Subject","Text Message", 
                "<html><body><h1>Html</h1></body></html>", 
                null, "dabaws777@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
        
        String[] data3 = {"dabaws777@gmail.com", null,"Text Message", 
                "<html><body><h1>Html</h1></body></html>", 
                "williamngoreceive@gmail.com", "shiftkun662@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
        
        String[] data4 = {"dabaws777@gmail.com", "Subject",null, 
                "<html><body><h1>Html</h1></body></html>", 
                "williamngoreceive@gmail.com", "shiftkun662@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
        
        String[] data5 = {"dabaws777@gmail.com", "Subject","Text Message", 
                null, 
                "williamngoreceive@gmail.com", "shiftkun662@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
        
         String[] data6 = {"dabaws777@gmail.com", "Subject","Text Message", 
                "<html><body><h1>Html</h1></body></html>",  
                null, "shiftkun662@gmail.com", 
                "images\\images2.jpg", "images\\images.jpg"};
         
         String[] data7 = {"dabaws777@gmail.com", "Subject","Text Message", 
                "<html><body><h1>Html</h1></body></html>", 
                "williamngoreceive@gmail.com", null, 
                "images\\images2.jpg", "images\\images.jpg"};
               
         String[] data8 = {"dabaws777@gmail.com", "Subject","Text Message", 
                "<html><body><h1>Html</h1></body></html>", 
                "williamngoreceive@gmail.com", "shiftkun662@gmail.com",  
                "images\\images2.jpg", null};
         
         
         
        
        Object[] [] datas = new Object[][]{
        data2,data3,data4,data5,data6,data7,data8,data
        };
        
        return Arrays.asList(datas);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sendTestExcIllegal() 
    {
         JagEmail emailObtained = mm.sendEmail(null,null, null,null,null,null,null,null);
    }

    /**
     * Testing sendMail and testing for equals if it returns true
     * no illegalArgumentException should occur here
     */
    @Test
    public void sendTestExc() 
    {

       log.info("Testing SEND MAIL");
        
       JagEmail emailObtained = mm.sendEmail(receiverEmail,subject, text,html,cc,bcc,embedded,attachment);
       
       
        log.info("Thread will start sleeping");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("Threaded sleep failed", e);
            System.exit(1);

        }
        
        log.info("Thread finished sleeping");
        
      
       
        
       JagEmail[] expectedEmails = mm.receiveEmail();
       JagEmail expectedEmail = expectedEmails[0];
       
       assertEquals(expectedEmail, emailObtained);
       
    }
    
    
}
