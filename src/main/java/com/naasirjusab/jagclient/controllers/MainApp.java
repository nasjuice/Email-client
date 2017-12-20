package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.fileManagement.PropertiesManager;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class is run when the application first runs.
 *
 * @author Naasir Jusab
 */
public class MainApp extends Application {

    private Stage stage;
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Locale currentLocale;

    /**
     * Sets the scene when the application is executed
     *
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        log.info("Inside Start ");
        PropertiesManager pm = new PropertiesManager();
        currentLocale = Locale.getDefault();
        //loads properties
        ConfigBean cb = pm.loadTextProperties("src/main/resources", "configuration");
        log.info("Loaded Properties ");

        //if file was loaded properly, open the main controller
        if (cb != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            URL path2 = Paths.get("src/main/resources/fxml/main.fxml").toUri().toURL();
            loader.setLocation(path2);
            stage.getIcons().add(
                    new Image(MainApp.class
                            .getResourceAsStream("/images/email.png")));

            Scene scene2 = new Scene(loader.load());

            stage.setTitle("Jag Client");
            stage.setResizable(false);
            stage.setScene(scene2);
            stage.show();
        } else { 
            //if file was not loaded properly and information was incorrect then
            //load config form
            URL path = Paths.get("src/main/resources/fxml/config.fxml").toUri().toURL();
            FXMLLoader fxmlloader = new FXMLLoader();
            fxmlloader.setLocation(path);

            fxmlloader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));

            Scene scene = new Scene(fxmlloader.load());

            stage.setTitle("Config");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();

        }

    }

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
