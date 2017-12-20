package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.database.JagDaoModule;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class takes care of the table view functionality
 *
 * @author Naasir Jusab
 */
public class TableViewController {

    private JagDaoModule db;
    private String foldername;

    private EditorController ec;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private ConfigBean cb;

    @FXML
    private AnchorPane tableAnchorPane;

    @FXML
    private TableView<JagEmail> mainTableView;

    @FXML
    private TableColumn<JagEmail, String> fromColumn;

    @FXML
    private TableColumn<JagEmail, String> subjectColumn;

    @FXML
    private TableColumn<JagEmail, String> dateRcvdColumn;

    public TableViewController() {
        super();

        db = new JagDaoModule(cb);

    }

    /**
     * Here we are adding a listener, if a click happens then run the
     * displayJagEmailDetails method
     */
    @FXML
    public void initialize() {

        fromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFrom().toString()));

        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSubject().toString()));

        dateRcvdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReceiveDate().toString()));

        mainTableView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> displayJagEmailDetails(newValue));

    }

    /**
     * This method will check when an email is dragged
     *
     * @param event
     */
    @FXML
    private void emailDrag(MouseEvent event) {
        JagEmail email = mainTableView
                .getSelectionModel().getSelectedItem();
        if (email != null) {
            log.info("Dragged");
            Dragboard dragBoard = mainTableView.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(email.getID() + "");
            dragBoard.setContent(content);
            event.consume();
            log.info("Finished dragging");
        }

    }

    /**
     * This method will run the editor controller's display method
     *
     * @param jagEmail
     */
    public void displayJagEmailDetails(JagEmail jagEmail) {

        ec.setJagEmail(jagEmail);
        ec.displayMail();
    }

    /**
     * This method returns a folder name
     *
     * @return
     */
    public String getFolderName() {
        return foldername;
    }

    /**
     * This method sets a folder name
     *
     * @param foldername
     */
    public void setFolderName(String foldername) {
        this.foldername = foldername;
    }

    /**
     * This method sets a database.
     *
     * @param db
     */
    public void setDatabase(JagDaoModule db) {
        this.db = db;
    }

    /**
     * This method returns the table
     *
     * @return
     */
    public TableView<JagEmail> getTableView() {
        return mainTableView;
    }

    /**
     * This method sets the editor controller
     *
     * @param ec
     */
    public void setEditorController(EditorController ec) {
        this.ec = ec;
    }

    /**
     * This method will get the jagEmails on the table
     *
     * @throws SQLException
     */
    public void displayJagEmailTable() throws SQLException {

        mainTableView.setItems(getJagEmails());
    }

    /**
     * This method forms a list of jagEmails that will go on the table
     *
     * @return
     * @throws SQLException
     */
    private ObservableList<JagEmail> getJagEmails() throws SQLException {
        log.info("Reading mails");
        List<JagEmail> emails = this.db.readJagEmail(foldername);

        ObservableList<JagEmail> jagEmails = FXCollections
                .observableArrayList();

        for (JagEmail email : emails) {
            log.info(email.getFrom() + "");
            log.info(email.getSubject());
            log.info(email.getReceiveDate() + "");
            jagEmails.add(email);

        }

        return jagEmails;

    }

}
