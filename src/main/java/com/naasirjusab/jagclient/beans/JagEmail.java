
package com.naasirjusab.jagclient.beans;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.mail.Flags;
import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.ReceivedEmail;

/**
 *
 * @author Naasir Jusab
 * This class is a bean that takes a folder name, flag, attachment message, 
 * message number and receive date.Defines a JagEmail which is the type of email 
 * used for sending and receiving for our JagClient.
 */


public class JagEmail extends Email {
    
    private String folderName;
    private Flags flag;
    private List<ReceivedEmail> attachMsg;
    private int messageNumber;
    private Date receiveDate;
    private boolean typeFlag;
    private int ID;

  

   
    
    
   
    //Calls the Email constructor which is its super constructor to create the 
    //obj
    public JagEmail()
    {
        super();

    }
    
    /**
    * Returns the flag for a certain JagEmail
    * @return the flag of the JagEmail
    */
    public Flags getFlag() {
        return flag;
    }
    
    /**
     * Returns the id for a JagEmail
     * @return integer that contains the id of a JagEmail
     */
    public int getID()
    {
        return ID;
    }
    
    
    /**
    * Returns the attachments
    * @return array list of email attachments which contains attachments
    
    public ArrayList<EmailAttachment> getAttachments() 
    {
        return attachments;
    }
    * 
    * 
    */
    
    
    
    
    /**
     * Returns true if sent else false for received
     * @return boolean true/false for sent/received
     */
    public boolean getTypeFlag() {
        return typeFlag;
    }
    
        
  
    /**
    * Returns the message number
    * @return an integer which contains a message number
    */
    public int getMessageNumber() {
        return messageNumber;
    }

   
    /**
    * Returns the receive date
    * @return a date object which contains the received date
    */
    public Date getReceiveDate() {
        return receiveDate;
    }

    
    /**
    * Returns the folder name
    * @return a string which contains the folder name
    */
    public String getFolderName() {
        return folderName;
    }
    
    /**
    * Returns the attachment message
    * @return list of received email which contains attachment message
    */
    public List<ReceivedEmail> getAttachMsg() {
        return attachMsg;
    }
    
    /**
     * Sets an array list of email attachments.
     * 
     * @param attachments The array list of EmailAttachment objects
     */
    public void setAttachments(ArrayList<EmailAttachment> attachments) {
        this.attachments = attachments;
    }
    
    /**
     * Sets the id of a JagEmail
     * @param ID The value that will be set to a JagEmail
     */
    public void setID(int ID)
    {
        this.ID = ID;
    }
    
    /**
    * Modifies the attachment message of a list of received email
    * @param attachMsg a list of received email that represents the attachment 
    * message
    */
    public void setAttachMsg(List<ReceivedEmail> attachMsg) {
                 
        this.attachMsg = attachMsg;
    }
    
    

    
    
    /**
    * Modifies the message number
    * @param messageNumber integer that represents the message number of an 
    * email
    */
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    
    /**
    * Modifies the folder name
    * @param folderName string that represents the folder name
    */
    public void setFolderName(String folderName) {

        this.folderName = folderName;
    }

    /**
    * Modifies the flag of a JagEmail if it is not null
    * @param flag A flag object that represents the flag of a JagEmail
    */
    public void setFlag(Flags flag) {
        
        
        this.flag = flag;
    }
    
    /**
    * Modifies the receive date
    * @param receiveDate Date object that represents the receive date of an 
    * email
    */
    public void setReceiveDate(Date receiveDate) {
         
        
        this.receiveDate = receiveDate;
    }
    
    
    /**
     * Modifies the typeFlag to either sent or received by setting the values
     * to true or false
     * @param typeFlag boolean that represents the typeFlaq of an email
     */
    public void setTypeFlag(boolean typeFlag) {
        this.typeFlag = typeFlag;
    }
    
    
    /**
    * Compares a JagEmail to another object to check if they are equal
    * on the to, the from, the subject, the attachments, the cc, the message 
    * number
    * @param obj object that will be compared to a JagEmail
    * @return a boolean value if they are equal otherwise false
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        JagEmail other = (JagEmail) obj;
        
        if(this.to == null && other.to != null){
            return false;
        }
        
        if(this.to != null && other.to == null){
            return false;
        }
        if(this.to != null && other.to != null)
            if(this.to.length != other.to.length)
                return false;
            
        
        if(this.to != null && other.to != null)
            for(int i = 0; i < this.to.length; i++){
                if(!Objects.equals(this.to[i].getEmail(), other.to[i].getEmail())){
                    return false;
                }
            }
        
            
        if(!Objects.equals(this.from.getEmail(), other.from.getEmail())){
            return false;
        }
        if(!Objects.equals(this.subject, other.subject)){
            return false;
        }
       
    
        
        
        if(this.messages.size() != other.messages.size()){
            return false;
        }
        
        if(this.messages != null && other.messages != null)
            for(int i=0; i <messages.size();i++)
            {
            EmailMessage thisMessage = this.messages.get(i);
            EmailMessage otherMessage = other.messages.get(i);
            
            if(!(thisMessage.getContent().equals(otherMessage.getContent())))
                return false;
            }
        
      
       if(this.attachments == null && other.attachments != null){
            return false;
        }
       
        if(this.attachments != null && other.attachments == null){
            return false;
        }
        
        if(this.attachments != null && other.attachments != null)
           if(this.attachments.size() != other.attachments.size()) {
            return false;
        }
        
        if(this.attachments != null && other.attachments != null)
           
        {
            
            boolean present = false;
            //Checks actual attachment
            for(int i = 0; i < this.attachments.size(); i++)
            {
                present = false;
                for(int j = 0; j < other.attachments.size(); j++)
                {
                    if(this.attachments.get(i).getName().contains(other.attachments.get(j).getName()))
                    {
                        present = true;
                        
                    }
                }
            }
            
            if(!present)
            {
                return false;
            }
            
        }
        
        
        if(this.cc == null && other.cc != null){
            return false;
        }
       
        if(this.cc != null && other.cc == null){
            return false;
        }
        
        if(this.cc != null && other.cc != null)
            if(this.cc.length != other.cc.length){
            return false;
        }
        
         for(int i = 0; i < this.cc.length; i++){
            if(!Objects.equals(this.cc[i].getEmail(), other.cc[i].getEmail())){
                    return false;
                }
            }
        
        
        return true;
    }
    
    
    /**
    * @return integer that represents the hash code value of an object
    */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 35 * hash + Objects.hashCode(this.to);
        hash = 35 * hash + Objects.hashCode(this.from);
        hash = 35 * hash + Objects.hashCode(this.subject);
        hash = 35 * hash + Objects.hashCode(this.messages);
        hash = 35 * hash + Objects.hashCode(this.cc);
        return hash;
    }
    
    
    
    
}
