package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.database.JagDaoModule;
import javafx.scene.input.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This class takes care of the tree view functionality
 *
 * @author Naasir Jusab
 */
public class TreeViewController {

    @FXML
    private TreeView<String> treeViewPane;

    @FXML
    ObservableList<String> allFolders;

    private JagDaoModule db;
    private TableViewController tvc;

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @FXML
    private void initialize() {

    }

    /**
     * This method sets the database
     *
     * @param db
     */
    public void setDatabase(JagDaoModule db) {
        this.db = db;
    }

    /**
     * This method sets the table view
     *
     * @param tvc
     */
    public void setTableViewController(TableViewController tvc) {
        this.tvc = tvc;
    }

    /**
     * This method will list the tree folder and have a click listener which
     * will display a folder's emails on the table hence why we have a setter
     * for the table.
     *
     * @throws SQLException
     */
    public void displayTree() throws SQLException {

        log.info("Getting folders");

        List<String> folders = db.readFolders();

        treeViewPane.setRoot(new TreeItem(new String("Folders")));

        treeViewPane.setCellFactory((e) -> new TreeCell<String>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item);
                    setGraphic(getTreeItem().getGraphic());
                } else {
                    setText("");
                    setGraphic(null);
                }
            }
        });

        allFolders = FXCollections.observableArrayList();
        for (String fold : folders) {
            log.info(fold);
            allFolders.add(fold);

        }

        if (allFolders != null) {
            for (String fd : allFolders) {
                TreeItem<String> item = new TreeItem<>(fd);

                Image img = new Image("images/folder.png");
                ImageView folderImg = new ImageView(img);

                item.setGraphic(folderImg);

                item.setValue(fd);
                treeViewPane.getRoot().getChildren().add(item);
            }
        }

        treeViewPane.getRoot().setExpanded(true);

        treeViewPane.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showDetails(newValue));

    }

    /**
     * Ths method will set the folder name on the tableviewController. Then it
     * will display all the JagEmails from a specific folder
     * @param folder 
     */
    private void showDetails(TreeItem<String> folder) {

        try {
            String folderName = folder.getValue();
            tvc.setFolderName(folderName);
            tvc.displayJagEmailTable();
        } catch (NullPointerException npe) {

        } catch (SQLException e) {
            log.error("Error displaying tree view");
        }
    }

    /**
     * This method takes care of dragging over an email that was previously
     * dragged on the table view
     * @param event 
     */
    @FXML
    private void dragOver(DragEvent event) {
        log.info("Dragging over");
        if (event.getDragboard().hasString()
                && event.getGestureSource() != treeViewPane) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * This method will take care of dropping an email  previously dragged
     * onto a folder and updating its folder name.
     * @param event 
     */
    @FXML
    private void dragDrop(DragEvent event) {
        log.info("Drag drop");
        Dragboard dragBoard = event.getDragboard();
        boolean success = false;
        if (dragBoard.hasString()) {
            try {
                Text folder = (Text) event.getTarget();
                db.updateFolderNames(Integer.parseInt(dragBoard.getString()), folder.getText());
                success = true;
            } catch (SQLException e) {
                log.error("Could not drag");
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

}
