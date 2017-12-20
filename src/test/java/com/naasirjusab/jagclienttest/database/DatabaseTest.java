/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.naasirjusab.jagclienttest.database;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.database.JagDaoModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import jodd.mail.EmailAttachment;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author 1433545
 */
@RunWith(Parameterized.class)
public class DatabaseTest {
    
    private JagDaoModule db; 
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private ConfigBean cb;
            
    private JagEmail jagEmail;
    private String folderName; 
    
    
    public DatabaseTest(JagEmail jagEmail, String folderName) {
        
        this.jagEmail = jagEmail; 
        this.folderName = folderName; 
        cb = new ConfigBean("Nas","dabaws777@gmail.com","donutman87",
        "smtp.gmail.com","imap.gmail.com", 993,465, "CS1433545", "otrostio",
        "jdbc:mysql://waldo2.dawsoncollege.qc.ca:3306/cs1433545", 3306);
        
        db = new JagDaoModule(cb);

        
    }
    
    @Parameterized.Parameters 
    public static Collection<Object[]> data()
    {       
        String path1 = "images\\images.jpg";  
      
        //test with text and html
        JagEmail test1 = new JagEmail();
        test1.to("nasjoseph7@gmail.com");
        test1.from("dabaws777@gmail.com");
        test1.subject("H3110");
        test1.addText("Can't wait to finish");
        test1.addHtml("<h1> heye </h1>");
        test1.setReceiveDate(new java.util.Date());
        test1.setFolderName("Sent");


        //test with cc and bcc but no to
        JagEmail test2 = new JagEmail();
        test2.from("dabaws777@gmail.com");
        test2.subject("hehe");
        test2.addText("ecksdee");
        test2.cc("williamngoreceive@hotmail.com");
        test2.bcc("shiftkun662@gmail.com");
        test2.setReceiveDate(new java.util.Date());
        test2.setFolderName("Sent");

        //test with attachments
        JagEmail test3 = new JagEmail();
        test3.to("nasjoseph7@gmail.com");
        test3.from("dabaws777@gmail.com");
        test3.subject("bahahah");
        test3.addText("hiihihihi");
        test3.cc("williamngoreceive@hotmail.com");
        test3.setReceiveDate(new java.util.Date());
        test3.setFolderName("Sent");
        
        test3.attach(EmailAttachment.attachment().file(path1)); 


        Object[] data1 = {test1, "trashcan"};
        Object[] data2 = {test2, "draft"};
        Object[] data3 = {test3, "woohoo"};

        Object[][] data = new Object[][]{data1,data2, 
            data3};

        return Arrays.asList(data);
    }
    
    @Test
    public void TestCreate() {
            
        
      ArrayList<JagEmail> list = new ArrayList<>();
      int index = 0; 
      try{
        db.create(jagEmail);
        list = db.readJagEmail(jagEmail.getFolderName());       
         
        int id = jagEmail.getID();
        for(int i = 0; i < list.size(); i++)
        {
            if(id == list.get(i).getID())
                index = i;
        } 
        
        
      }
      
      catch(Exception e){
          log.info(e.getMessage() + "failed");
      }

      

        assertEquals(jagEmail, list.get(index));            
    }
    
    
    @Test
    public void TestUpdate()
    {
       //number of records affected should be 1
       int expected = 1;
       int obtained = 0;
        try{
            obtained = db.updateFolderNames(1,"Sent");
        }
        catch(SQLException e)
        {
            log.info(e.getMessage() + "failed");
        }      
        assertEquals(expected, obtained);       
    }
    
     @Test
    public void TestCreateFolder()
    {
        boolean folderCreated; 
        boolean expected= true;
        
        try{
            db.create(folderName);
            folderCreated = true;
        }
        catch(SQLException e)
        {
            folderCreated = false; 
        }
       
        if(folderCreated)
        {
            String query = "SELECT folderName, userEmail from jagFolder WHERE userEmail = ? AND folderName = ?"; 
        
            try(Connection connection = DriverManager.getConnection(cb.getSqlUrl(), cb.getSqlUsername(), cb.getSqlPassword()); 
                PreparedStatement stmt = connection.prepareStatement(query))
            {                
                stmt.setString(1,cb.getSenderEmail());
                stmt.setString(2, folderName);
                
                ResultSet rs = stmt.executeQuery();
                folderCreated = rs.next();
            }
            catch(SQLException e)
            {
                folderCreated = false; 
            }    
        }   

        assertEquals(folderCreated, expected); 
       
    }
    
    @Test
    public void TestDelete()
    {
      
       int expected = 1;
       int obtain = 0;
        try{
            obtain = db.delete(1);
        }
        
        catch(SQLException e)
        {
            log.info(e.getMessage() + "failed to delete");
        }      
        assertEquals(expected, obtain);       
    }
    
    @Test
    public void TestDeleteFolders()
    {
      
       int expected = 1;
       int obtain = 0;
        try{
            //sent folder
            obtain = db.delete("Sent");
        }
        
        catch(SQLException e)
        {
        log.info(e.getMessage() + "failed to delete folder");
        }      
        assertEquals(expected, obtain);       
    }
    
    @Test 
    public void TestGetUsersFolders()
    {    
        String[] expected = {"Sent", "Trash", "Spam"}; 
        boolean valid = true; 
        boolean expectedAnswer = true;
        
        try{
            
            ArrayList<String> folders = new ArrayList<>();
            
            folders = db.readFolders();
            
            if(folders.size() == expected.length)
            {
                for(int i = 0; i < expected.length; i++)
                {
                   
                    if(!(folders.get(i).equals(expected[i])))
                        valid = false; 
                }
                
            }      
            
            else
            {
                valid = false;
            }
        }
        catch(SQLException e)
        {
            valid = false;
        }
        
        assertEquals(valid, expectedAnswer);
    }
    

    
    
    
    
    @Before
     public void seedDatabase() {
        
        final String seedDataScript = loadAsString("tables.sql");
        try (Connection connection = DriverManager.getConnection(cb.getSqlUrl(),
                cb.getSqlUsername(),cb.getSqlPassword())) {
            for (String statement : splitStatements(new StringReader(seedDataScript), ";")) {
                    connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
                throw new RuntimeException("Failed seeding database", e);
        }
    }
     
    private String loadAsString(final String path) {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path);
                        Scanner scanner = new Scanner(inputStream)) {
                return scanner.useDelimiter("\\A").next();
        } 
        catch (IOException e) {
                throw new RuntimeException("Unable to close input stream.", e);
        }
    } 
     
    private List<String> splitStatements
        (Reader reader, String statementDelimiter) {
        
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<String>();
        try {
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || isComment(line)) {
                                continue;
                        }
                        sqlStatement.append(line);
                        if (line.endsWith(statementDelimiter)) {
                                statements.add(sqlStatement.toString());
                                sqlStatement.setLength(0);
                        }
                }
                return statements;
        } catch (IOException e) {
                throw new RuntimeException("Failed parsing sql", e);
        }
    }
    
    private boolean isComment(final String line) {
            return line.startsWith("--") || line.startsWith("//") || 
                    line.startsWith("/*");
    }


    
}
