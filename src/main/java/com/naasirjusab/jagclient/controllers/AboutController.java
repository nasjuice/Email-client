
package com.naasirjusab.jagclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * This class takes care of handling the about fxml page.
 * @author Naasir Jusab
 */
public class AboutController {
    
    @FXML
    private Button close;
    
    
    @FXML
    public void initialize() {
     
    }
    
    /**
     * When the button close is clicked then close the about page.
     */
    @FXML
    private void onClose()
    {
        Stage s = (Stage)close.getScene().getWindow();
        s.close();
    }
    
    
    
    
}
