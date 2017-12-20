
package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.fileManagement.PropertiesManager;
import com.naasirjusab.jagclient.validation.FormValidator;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of handling the configcontroller. This controller
 * opens a form which will be saved to the filesystem. It will hold the proper 
 * configuration to run the Jag Client.
 * @author Naasir Jusab
 */
public class ConfigController implements Initializable {
    
    @FXML 
    private TextField nameField;
    
    @FXML 
    private TextField smtpNameField;
    
    @FXML 
    private TextField imapNameField;
    
    @FXML 
    private TextField emailField;
    
    @FXML
    private PasswordField emailPasswordField;
    
    @FXML
    private TextField sqlUrlField;
    
    @FXML
    private TextField sqlPortField;
    
    @FXML 
    private TextField sqlUsernameField;
    
    @FXML
    private PasswordField sqlPasswordField;
    
    @FXML
    private TextField imapPortField;
    
    @FXML
    private TextField smtpPortField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button submitBtn;
    
    private ConfigBean cb;
    private PropertiesManager pm;
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private MainController mc;
    private Locale currentLocale= currentLocale = Locale.getDefault();

   
    
    
    
    
    
    /**
      * Initializes the Controller class
      * We are binding the fields form the form to the configuration getter
      * so they get updated as soon as values are entered.
      * @param url 
      * @param rb 
      */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        pm = new PropertiesManager();
        cb = new ConfigBean();
        
        if(cb != null)
        {
            
            Bindings.bindBidirectional(nameField.textProperty(), cb.getNameProperty());
            Bindings.bindBidirectional(smtpNameField.textProperty(), cb.getStmpServerNameProperty());
            Bindings.bindBidirectional(imapNameField.textProperty(), cb.getImapServerNameProperty());
            Bindings.bindBidirectional(emailField.textProperty(), cb.getSenderEmailProperty());
            Bindings.bindBidirectional(emailPasswordField.textProperty(), cb.getSenderPasswordProperty());
            Bindings.bindBidirectional(sqlUrlField.textProperty(), cb.getSqlUrlProperty());
            Bindings.bindBidirectional(sqlPortField.textProperty(), cb.getSqlPortProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(sqlUsernameField.textProperty(), cb.getSqlUsernameProperty());
            Bindings.bindBidirectional(sqlPasswordField.textProperty(), cb.getSqlPasswordProperty());
            Bindings.bindBidirectional(imapPortField.textProperty(), cb.getImapPortProperty(), new NumberStringConverter());
            Bindings.bindBidirectional(smtpPortField.textProperty(), cb.getSmtpPortProperty(), new NumberStringConverter());
            
          
        }
        
        
       
    }
    
    /**
     * This method runs when the form is submitted. First it will validate, then
     * it will check whether the method call was from the root page or if the user
     * did not have a properties file thus the reason this page was popped.
     * @param event 
     */
    @FXML
    private void submit(ActionEvent event) 
    {
        errorLabel.setText("");
    
       try
        {
 
            FormValidator validator = new FormValidator(cb);
            log.info("Created validation obj");
            
            validator.validateFormFields();
            log.info("Validated form fields");
            validator.validateEmail();
            log.info("Validated email");
            validator.validatePorts();
            log.info("Validated ports");
            validator.validateImapString();
            log.info("Validated imap string");
            validator.validateSmtpString();
            log.info("Validated smtp string");
            validator.validateDatabase();
            log.info("Validated database string");
            
           
            
            pm.writeTextProperties("src/main/resources/", "configuration", cb);
            log.info(cb.getSmtpServerName());
            
            log.info("Wrote to file");
            //first time running the app, could not find the text file
            if(mc ==null)
            {
                 try {
                         
                        Stage stage = new Stage();
                        URL path = Paths.get("src/main/resources/fxml/main.fxml")
                                .toUri().toURL();
                    
                        FXMLLoader loader = new FXMLLoader();

                        loader.setLocation(path);
                        stage.getIcons().add(
                            new Image(MainApp.class
                        .getResourceAsStream("/images/email.png")));
                        loader.setResources(ResourceBundle.getBundle("Bundle",currentLocale));
                        loader.setBuilderFactory(new JavaFXBuilderFactory());
                      
                        Scene scene = new Scene(loader.load());

                        stage.setResizable(false);
                        stage.setScene(scene);
                        stage.show();
                        
                        Stage s = (Stage) submitBtn.getScene().getWindow();
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                
                
            }
            //app was already running but the user wanted to modify data
            else
            {
                mc.reset();
                
                Stage s = (Stage) submitBtn.getScene().getWindow();
                s.close();
                
                  try {
                         
                        Stage stage = new Stage();
                        URL path = Paths.get("src/main/resources/fxml/main.fxml")
                                .toUri().toURL();
                        
                        FXMLLoader loader = new FXMLLoader();
                        stage.getIcons().add(
                        new Image(MainApp.class
                        .getResourceAsStream("/images/email.png")));
                        loader.setResources(ResourceBundle.getBundle("Bundle",currentLocale));
                        loader.setLocation(path);
                        loader.setBuilderFactory(new JavaFXBuilderFactory());
                
                        Scene scene = new Scene(loader.load());

                        stage.setResizable(false);
                        stage.setScene(scene);
                        stage.show();
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

            
        }
        
        
        catch(Exception e)
        {
            errorLabel.setText((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("errorlbl")));
        }

        
    }
    
    /**
     * This method sets the main/root controller to reset the values
     * @param mc 
     */
    public void setMainController(MainController mc)
    {
        this.mc = mc;
    }
    
 
    
   
 
    
    
}
