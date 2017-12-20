package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.MailModule;
import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.database.JagDaoModule;
import com.naasirjusab.jagclient.fileManagement.PropertiesManager;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class takes care of the setting the main scene and handling the buttons
 * on the toolbar.
 *
 * @author Naasir Jusab
 */
public class MainController implements Initializable {

    @FXML
    private AnchorPane treeViewPane;

    @FXML
    private AnchorPane tableViewPane;

    @FXML
    private AnchorPane editorViewPane;

    @FXML
    private Button mailBtn;

    private TreeViewController treeViewController;
    private TableViewController tableViewController;
    private EditorController ec;
    private ConfigBean cb;
    private PropertiesManager pm;
    private JagDaoModule db;
    private MailModule jagClient;
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private Stage stage;
    private Locale currentLocale = currentLocale = Locale.getDefault();

    /**
     * Initializes the Controller class
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        log.info("inside main initialize");
        pm = new PropertiesManager();
        setConfigBean();
        if (cb != null) {

            setDatabase();
            setClient();
            insertEditorView();
            insertTableView();
            insertTreeView();

        }

    }

    /**
     * This method will load the properties file from the file system and set
     * the configbeans values
     */
    public void setConfigBean() {

        try {
            this.cb = pm.loadTextProperties("src/main/resources", "configuration");
        } catch (IOException ex) {
            alertMsg((ResourceBundle.getBundle("Bundle",
                    currentLocale).getString("errorFile")));
        }
    }

    /**
     * This method will set the client to send/receive mail
     */
    public void setClient() {
        this.jagClient = new MailModule(cb);
        jagClient.setDatabase(db);

        jagClient.receiveEmail();

    }

    /**
     * This method will set the database to operate on the different tables.
     */
    public void setDatabase() {
        this.db = new JagDaoModule(cb);
    }

    /**
     * This method will reset the values when a new config form is loaded
     */
    public void reset() {
        try {
            setConfigBean();

            log.info("Config bean is set");

            setDatabase();

            log.info("Database is set");

            setClient();

            log.info("Client is set");

            treeViewController.setDatabase(db);
            tableViewController.setDatabase(db);

            ec.setJagClient(jagClient);

            tableViewController.setEditorController(ec);
            treeViewController.setTableViewController(tableViewController);

            treeViewController.displayTree();
        } catch (SQLException e) {
            alertMsg((ResourceBundle.getBundle("Bundle",
                    currentLocale).getString("errorReset")));
        }
    }

    /**
     * This will insert the tree view fxml onto its pane.
     */
    private void insertTreeView() {

        try {
            FXMLLoader loader = new FXMLLoader();

            URL path = Paths.get("src/main/resources/fxml/tree.fxml").toUri().toURL();
            loader.setLocation(path);
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            AnchorPane treeView = (AnchorPane) loader.load();

            treeViewController = loader.getController();

            treeViewController.setDatabase(db);
            treeViewController.setTableViewController(tableViewController);
            treeViewController.displayTree();

            treeViewPane.getChildren().add(treeView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will insert the table view fxml onto its pane.
     */
    private void insertTableView() {
        try {
            FXMLLoader loader = new FXMLLoader();

            URL path = Paths.get("src/main/resources/fxml/emailDisplay.fxml").toUri().toURL();
            loader.setLocation(path);
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            AnchorPane View = (AnchorPane) loader.load();

            tableViewController = loader.getController();
            tableViewController.setEditorController(ec);
            tableViewController.setDatabase(db);

            tableViewPane.getChildren().add(View);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will insert the editor view fxml onto its pane.
     */
    private void insertEditorView() {
        try {
            FXMLLoader loader = new FXMLLoader();

            URL path = Paths.get("src/main/resources/fxml/createMail.fxml").toUri().toURL();
            loader.setLocation(path);

            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            BorderPane view = (BorderPane) loader.load();

            ec = loader.getController();
            log.info(jagClient + "");

            ec.setJagClient(jagClient);

            editorViewPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles when the user click Config Load on the menubar.
     */
    @FXML
    private void configHandle() {
        try {
            Stage stage = new Stage();
            Stage st = (Stage) mailBtn.getScene().getWindow();
            st.close();
            URL path = Paths.get("src/main/resources/fxml/config.fxml").toUri().toURL();

            FXMLLoader loader = new FXMLLoader();
            stage.getIcons().add(
                    new Image(MainApp.class
                            .getResourceAsStream("/images/email.png")));
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            loader.setLocation(path);

            Scene scene = new Scene(loader.load());

            ConfigController configController = (ConfigController) loader.getController();

            configController.setMainController(this);

            stage.setTitle("Config");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method allows the creation of a mail
     */
    @FXML
    private void createEmail() {
        log.info(cb.getSmtpServerName());
        log.info("Sending mail");
        ec.sendEmail();

    }

    /**
     * This method adds a folder to the tree view and database. It prompts the
     * user for a folder name and adds it.
     */
    @FXML
    private void addFolder() {
        Stage stage = new Stage();

        Text text = new Text();
        text.setText((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("folderText")));

        TextField textField = new TextField();
        textField.setText("");

        Button btn = new Button();
        btn.setText("OK");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (!textField.getText().equals("")) {
                    createFolder(textField.getText());
                    stage.close();
                }
            }
        });
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));

        grid.add(text, 0, 0, 2, 1);
        grid.add(textField, 0, 1);
        grid.add(btn, 1, 1);

        Scene scene = new Scene(grid, 250, 250);

        stage.setTitle("Information");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * This method will add it to the database and refresh the tree
     *
     * @param foldername
     */
    private void createFolder(String foldername) {
        try {
            if (!db.readFolders().contains(foldername)) {
                db.create(foldername);
                treeViewController.displayTree();
            }

        } catch (SQLException ex) {

            alertMsg((ResourceBundle.getBundle("Bundle",
                    currentLocale).getString("errorSending")));
        }
    }

    /**
     * This method will prompt messages to the user.
     *
     * @param msg
     */
    private void alertMsg(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    /**
     * This method will delete an email from the table and database. It will
     * prompt for confirmation from the user
     */
    @FXML
    private void deleteEmail() {
        log.info("Inside delete email");
        //resource bundle
        Alert alert = new Alert(AlertType.CONFIRMATION, (ResourceBundle.getBundle("Bundle",
                currentLocale).getString("delete")), ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            try {

                if (ec.getJagEmail() != null) {
                    int id = ec.getJagEmail().getID();
                    db.delete(id);
                    tableViewController.displayJagEmailTable();

                }

            } catch (SQLException ex) {
                alertMsg((ResourceBundle.getBundle("Bundle",
                        currentLocale).getString("errorDeleting")));
            }
            alert.close();
        } else {
            alert.close();
        }
    }

    /**
     * This method will delete a folder from the treeview and database. It will
     * prompt for confirmation from the user.
     */
    @FXML
    private void deleteFolder() {
        log.info("delete folder name");
        String folderName = tableViewController.getFolderName();

        if (folderName != null) {
            if (!folderName.equals("Inbox") && !folderName.equals("Sent")) {
                //rs string
                Alert alert = new Alert(AlertType.CONFIRMATION,
                        (ResourceBundle.getBundle("Bundle",
                                currentLocale).getString("delete")),
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {

                    try {
                        db.delete(folderName);
                        treeViewController.displayTree();
                    } catch (SQLException e) {
                        alertMsg((ResourceBundle.getBundle("Bundle",
                                currentLocale).getString("errorDeleting")));
                    }
                } else {
                    alert.close();
                }
            } else {
                alertMsg((ResourceBundle.getBundle("Bundle",
                        currentLocale).getString("specialDelete")));
            }

        }
    }

    /**
     * This method will get the receiveEmails and refresh the displayTable
     */
    @FXML
    private void onRefresh() {
        try {
            jagClient.receiveEmail();
            tableViewController.displayJagEmailTable();
        } catch (SQLException ex) {
            alertMsg("Error refreshing");
        }
    }

    /**
     * Loads about scene
     *
     * @param event
     */
    @FXML
    private void aboutClick(ActionEvent event) {
        try {
            Stage stage = new Stage();
            stage.getIcons().add(
                    new Image(MainApp.class
                            .getResourceAsStream("/images/email.png")));
            URL path = Paths.get("src/main/resources/fxml/About.fxml").toUri().toURL();

            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("Bundle", currentLocale));
            loader.setLocation(path);

            Scene scene = new Scene(loader.load());

            stage.setTitle((ResourceBundle.getBundle("Bundle",
                    currentLocale).getString("about")));
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will run the editor controller's reply method
     */
    @FXML
    private void reply() {
        log.info("Inside reply");
        ec.setReplyFields();

    }

    /**
     * This method will run the editor controller's reply all method
     */
    @FXML
    private void replyAll() {
        log.info("Inside reply all");
        ec.setConfigBean(cb);
        ec.setReplyAllFields();
    }

    /**
     * This method will the editor controller's forward method
     */
    @FXML
    private void forward() {
        log.info("Inside forward");
        ec.setForwardFields();
    }

}
