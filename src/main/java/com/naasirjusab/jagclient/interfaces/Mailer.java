//Defines a set of methods that will be used by the MailModule
package com.naasirjusab.jagclient.interfaces;



import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;


/**
 *
 * @author Naasir Jusab
 */
public interface Mailer {
    
  JagEmail sendEmail(String receiverEmail, String subject, String text,String html,
                            String cc, String bcc, String embedded, String attachment);
  
 
  
  JagEmail[] receiveEmail();
  
 
    
}
