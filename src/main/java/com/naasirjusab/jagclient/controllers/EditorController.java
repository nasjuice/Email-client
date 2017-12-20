package com.naasirjusab.jagclient.controllers;

import com.naasirjusab.jagclient.MailModule;
import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import com.naasirjusab.jagclient.validation.FormValidator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jodd.mail.EmailAddress;
import jodd.mail.EmailAttachment;
import jodd.mail.EmailMessage;
import jodd.mail.MailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * This controller handles the html editor functionality.
 *
 * @author Naasir Jusab
 */
public class EditorController {

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private Label toLabel;
    @FXML
    private Label subjectLabel;
    @FXML
    private Label ccLabel;
    @FXML
    private Label bccLabel;
    @FXML
    private TextField toTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private TextField ccTextField;
    @FXML
    private TextField bccTextField;

    @FXML
    private ImageView attachmentsImg;

    @FXML
    private ImageView saveImg;

    @FXML
    private Button sendBtn;

    @FXML
    private Button attachmentsBtn;

    @FXML
    private Button saveBtn;

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private JagEmail jagEmail;
    private MailModule jagClient;
    private String attachmentPath;
    private ConfigBean cb;
    private Locale currentLocale= currentLocale = Locale.getDefault();

    public EditorController() {
        super();
    }

    /**
     * Here I disable a few buttons that are not necessary to be seen when the
     * editor is added.
     */
    @FXML
    public void initialize() {
        sendBtn.setVisible(false);
        attachmentsBtn.setVisible(false);
        bccTextField.setEditable(false);
        saveBtn.setVisible(false);
    }

    /**
     * Here I am passing a JagEmail to display its details
     *
     * @param jagEmail
     */
    public void setJagEmail(JagEmail jagEmail) {
        this.jagEmail = jagEmail;
    }

    /**
     * This method returns a jagEmail to see what was clicked.
     *
     * @return
     */
    public JagEmail getJagEmail() {
        return jagEmail;
    }

    /**
     * This method sets a JagClient to perform send/receive operations
     *
     * @param jagClient
     */
    public void setJagClient(MailModule jagClient) {

        this.jagClient = jagClient;

    }

    /**
     * This method sets the configBean to get necessary data to perform
     * send/receive operations.
     *
     * @param cb
     */
    public void setConfigBean(ConfigBean cb) {
        this.cb = cb;
    }

    /**
     * This method displays the details of a JagEmail. It is first splitting
     * tos because I allow multiple tos and then checks if a to is not the sender
     * email. Then, I add those excess tos onto the cc field. The subject field
     * is just getting the subject. I disable sending and attaching buttons because
     * I do not want user to send stuff when reading mail. I enable the save
     * button when there are attachments, the user can decide whether they want
     * to save or not.
     */
    public void displayMail() {

        log.info("displaying mail");
        Platform.runLater(() -> {
            String ccs = "";

            if (jagEmail != null) {
                String from = jagEmail.getFrom().getEmail();
                log.info(jagEmail.getTo() + "");
                log.info(jagEmail.getTo().length + "");
                if (jagEmail.getTo() != null) {
                    log.info("Splitting tos");
                    String tos = splitArray(jagEmail.getTo());
                    log.info(tos);
                    List<String> toList = new ArrayList<String>(Arrays.asList(tos.split(" ")));

                    for (String s : toList) {

                        log.info(s);
                        if (!s.equalsIgnoreCase(from)) {
                            ccs += s + ",";
                        }
                    }

                    toTextField.setText(from);
                }

                subjectTextField.setText(jagEmail.getSubject());
                ccTextField.setText(splitArray(jagEmail.getCc()) + ccs);

                sendBtn.setVisible(false);
                bccTextField.setEditable(false);
                attachmentsBtn.setVisible(false);

                htmlEditor.setHtmlText(getText(jagEmail.getAllMessages()));

                if (jagEmail.getAttachments() != null) {
                    if (jagEmail.getAttachments().size() != 0) {
                        saveBtn.setVisible(true);

                    }
                } else {
                    saveBtn.setVisible(false);

                }

            }

        });
    }

    /**
     * This method enables a few buttons and resets text fields to enable
     * a user to send an email.
     */
    public void sendEmail() {
        sendBtn.setVisible(true);
        bccTextField.setEditable(true);
        attachmentsBtn.setVisible(true);
        saveBtn.setVisible(false);
        toTextField.setText("");
        subjectTextField.setText("");
        ccTextField.setText("");
        bccTextField.setText("");
        htmlEditor.setHtmlText("");
        attachmentPath = "";

    }

    /**
     * This method takes care of filling the fields for a reply mechanism
     * to function. Enables sending button to allow a user to send a reply.
     * It does not get the attachments from the JagEmail because generally
     * people do not want to reply with an attachment that they received.
     */
    public void setReplyFields() {

        if (jagEmail != null) {
            log.info("Setting reply fields");

            toTextField.setText(jagEmail.getFrom().getEmail());
            subjectTextField.setText("Re: " + jagEmail.getSubject());
            ccTextField.setText("");

            sendBtn.setVisible(true);
            bccTextField.setText("");
            bccTextField.setEditable(true);
            attachmentsBtn.setVisible(true);
            saveBtn.setVisible(false);

            htmlEditor.setHtmlText(getText(jagEmail.getAllMessages()));

        }

    }

    /**
     * This method takes care of filling the fields for a forward mechanism to 
     * function. Enables sending button to allow a user to send a reply.
     * It does not get the attachments from a JagEmail, it was a design choice 
     * I felt that its not something user's would want.
     */
    public void setForwardFields() {
        if (jagEmail != null) {
            toTextField.setText("");
            subjectTextField.setText("Fwd: " + jagEmail.getSubject());
            ccTextField.setText("");

            htmlEditor.setHtmlText(getText(jagEmail.getAllMessages()));
            sendBtn.setVisible(true);
            bccTextField.setText("");
            bccTextField.setEditable(true);
            attachmentsBtn.setVisible(true);
            saveBtn.setVisible(false);

        }

    }

    /**
     * This method takes care of filling the fields for a reply all mechanism.
     * First, it separates multiple tos and checks whether they are equal
     * to the sender's email if they are then do not add them to the list.
     * This list will be set in the to text field in a comma separated way.
     * Then, we do the same thing for the cc fields. Then, I enable buttons
     * to allow the mail to be sent. The subject and message will also be set
     * on their respective fields.
     * 
     */
    public void setReplyAllFields() {
        if (jagEmail != null) {
            if (jagEmail.getTo() != null) {
                String to = splitArray(jagEmail.getTo());
                String[] tos = to.split(",");
                List<String> toList = new ArrayList<String>();
                for (int i = 0; i < tos.length; i++) {
                    if (!tos[i].equalsIgnoreCase(cb.getSenderEmail())) {
                        toList.add(tos[i]);
                    }
                }
                String toField = "";
                for (String t : toList) {
                    String[] arr = t.split(" ");
                    for (String ts : arr) {
                        if (ts != null && !ts.isEmpty()) {
                            toField += (ts + ",");
                        }
                    }
                }
                toTextField.setText(toField);
            }

            if (jagEmail.getCc() != null) {
                String cc = splitArray(jagEmail.getCc());
                String[] ccs = cc.split(",");
                List<String> ccList = new ArrayList<String>();
                for (int i = 0; i < ccs.length; i++) {
                    if (!ccs[i].equalsIgnoreCase(cb.getSenderEmail())) {
                        ccList.add(ccs[i]);
                    }
                }
                String ccField = "";
                for (String c : ccList) {
                    if (c != null && !c.isEmpty()) {
                        ccField += (c + ",");
                    }
                }
                ccTextField.setText(ccField);
            }
            sendBtn.setVisible(true);
            bccTextField.setText("");
            bccTextField.setEditable(true);
            attachmentsBtn.setVisible(true);
            saveBtn.setVisible(false);
            subjectTextField.setText("Re: " + jagEmail.getSubject());
            htmlEditor.setHtmlText(getText(jagEmail.getAllMessages()));

        }
    }

    /**
     * This method is an event handler which will write an attachment to disk.
     * It will prompt the user with a directory box and allow the user to enter
     * the path to their attachment. 
     */
    @FXML
    private void saveHandler() {
        Stage stage = new Stage();

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("dirTitle")));
        File file = dirChooser.showDialog(stage);

        if (file != null && file.exists()) {
            String path = file.getAbsolutePath();
            saveToFileSystem(path);
        }

        alertMsg((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("saveAlert")));
    }

    /**
     * This method will be the one writing the attachment on the file system.
     * You need a loop because multiple attachments are allowed.
     * @param path 
     */
    private void saveToFileSystem(String path) {
        List<EmailAttachment> attachments = jagEmail.getAttachments();

        try {
            for (EmailAttachment attach : attachments) {
                try (FileOutputStream fos = new FileOutputStream(path + "\\" + attach.getName())) {
                    fos.write(attach.toByteArray());
                }
            }
        } catch (IOException e) {
            alertMsg((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("errorSave")));
        }
    }

    /**
     * This method sets the path of the attachment that a user wants to attach.
     */
    @FXML
    private void attachmentHandler() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("fileTitle")));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null && file.exists()) {
            attachmentPath = file.getAbsolutePath();
        }

    }

    /**
     * This method takes care of sending the email. It will get the data from
     * the textFields then it goes through a regex check. If it is true
     * then the content is html otherwise it is plain text.
     */
    @FXML
    private void sendEmailHandler() {
        String to = toTextField.getText();
        String subject = subjectTextField.getText();
        String cc = ccTextField.getText();
        String bcc = bccTextField.getText();

        String content = htmlEditor.getHtmlText();

        try {
            validatefields(to, subject, cc, bcc, content);
            if (content.matches(".*\\<[^>]+>.*")) {
                jagClient.sendEmail(to, subject, "", content, cc, bcc, "", attachmentPath);
            } else {
                jagClient.sendEmail(to, subject, content, "", cc, bcc, "", attachmentPath);
            }
            alertMsg((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("sentSuccess")));

            toTextField.setText("");
            subjectTextField.setText("");
            ccTextField.setText("");
            bccTextField.setText("");
            htmlEditor.setHtmlText("");
            attachmentPath = "";
        } catch (Exception e) {
            alertMsg((ResourceBundle.getBundle("Bundle",
                currentLocale).getString("errorSending")));
        }
    }

    /**
     * This method will validate each field. It will check if the to,cc,bcc
     * are valid email addresses. It will then check if content is not null.
     * @param to
     * @param sub
     * @param cc
     * @param bcc
     * @param content 
     */
    private void validatefields(String to, String sub, String cc, String bcc, String content) {

        if (to != null) {
            if (to.equals("")) {
                throw new IllegalArgumentException("Give a to field");
            }

            validateEmails(to);
        } else {
            throw new IllegalArgumentException("Give a to field");
        }

        if (cc != null && !cc.equals("")) {
            validateEmails(cc);
        }

        if (bcc != null && !bcc.equals("")) {
            validateEmails(bcc);
        }

        if (content == null || content.equals("")) {
            throw new IllegalArgumentException("Add content");
        }

    }

    /**
     * This method checks if the string gmail.com is in the email. If it is
     * then it checks whether its a valid email address.
     * @param to 
     */
    private void validateEmails(String to) {

        String[] list = to.split(",");
        for (String receiver : list) {
            int i = receiver.indexOf("@gmail.com");

            if (i == -1) {
                throw new IllegalArgumentException("Not a valid gmail account");
            }

            EmailAddress email = new EmailAddress(receiver);
            if (!email.isValid()) {
                throw new IllegalArgumentException("Not a valid email address");
            }

        }

    }

    /**
     * This method just prompts information on the user's screen
     * @param msg 
     */
    private void alertMsg(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    /**
     * This method takes care of parsing message content
     * @param list
     * @return 
     */
    private String getText(List<EmailMessage> list) {

        if (list == null) {
            return "";
        }
        StringBuilder messages = new StringBuilder("");

        for (EmailMessage mess : list) {

            messages.append(mess.getContent());
        }

        return messages.toString();
    }

    /**
     * This method takes care of separating the MailAddress list into a comma-
     * separated list.
     * @param list
     * @return 
     */
    private String splitArray(MailAddress[] list) {

        if (list == null) {
            return "";
        }

        StringBuilder messages = new StringBuilder("");

        for (MailAddress mess : list) {
            messages.append(mess.getEmail() + ",");
        }

        String str = messages.toString();
        log.info(str);

        if (str.equals("")) {
            return "";
        } else {
            return str.substring(0, str.length() - 1);
        }
    }

}
