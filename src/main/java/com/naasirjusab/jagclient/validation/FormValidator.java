package com.naasirjusab.jagclient.validation;

import com.naasirjusab.jagclient.beans.ConfigBean;
import com.naasirjusab.jagclient.beans.JagEmail;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import jodd.mail.EmailAddress;
import jodd.mail.ImapSslServer;
import jodd.mail.ReceiveMailSession;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import jodd.mail.SmtpSslServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of validating the fields in the configuration form to
 * avoid having problems.
 *
 * @author Naasir Jusab
 */
public class FormValidator {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private ConfigBean cb;

    public FormValidator(ConfigBean cb) {
        this.cb = cb;
    }

    /**
     * This method checks if all the fields are set
     */
    public void validateFormFields() {

        if (cb.getName() == null || cb.getName().length() == 0) {
            throw new IllegalArgumentException("Invalid name");
        }

        if (cb.getSmtpServerName() == null || cb.getSmtpServerName().length() == 0) {
            throw new IllegalArgumentException("Invalid smtp name");
        }

        if (cb.getImapServerName() == null || cb.getImapServerName().length() == 0) {
            throw new IllegalArgumentException("Invalid imap name");
        }

        if (cb.getSenderEmail() == null || cb.getSenderEmail().length() == 0) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (cb.getSenderPassword() == null || cb.getSenderPassword().length() == 0) {
            throw new IllegalArgumentException("Invalid password address");
        }

        if (cb.getSqlUrl() == null || cb.getSqlUrl().length() == 0) {
            throw new IllegalArgumentException("Invalid sql url");
        }

        if (cb.getSqlUsername() == null || cb.getSqlUsername().length() == 0) {
            throw new IllegalArgumentException("Invalid sql username");
        }

        if (cb.getSqlPassword() == null || cb.getSqlPassword().length() == 0) {
            throw new IllegalArgumentException("Invalid sql password");
        }

    }

    /**
     * This method checks if the inputted email is valid
     */
    public void validateEmail() {

        String emailString = cb.getSenderEmail();

        int i = emailString.indexOf("@gmail.com");

        if (i == -1) {
            throw new IllegalArgumentException("Not a valid gmail account");
        }

        EmailAddress email = new EmailAddress(emailString);
        if (!email.isValid()) {
            throw new IllegalArgumentException("Not a valid email address");
        }

    }

    /**
     * This method checks if the ports are in the valid range
     */
    public void validatePorts() {
        int smtpPort = cb.getSmtpPort();
        int imapPort = cb.getImapPort();
        int sqlPort = cb.getSqlPort();

        if (smtpPort < 0 || smtpPort > 65535) {
            throw new IllegalArgumentException("Invalid SMTP Port");
        }

        if (imapPort < 0 || imapPort > 65535) {
            throw new IllegalArgumentException("Invalid IMAP Port");
        }

        if (sqlPort < 0 || sqlPort > 65535) {
            throw new IllegalArgumentException("Invalid SQL Port");
        }
    }

    /**
     * This method checks if a SMTP string is valid by trying to send an email
     * through a session. If the email fails to send then there was invalid
     * data.
     */
    public void validateSmtpString() {

        log.info("Inside smtp");
        String senderEmail = cb.getSenderEmail();
        String senderPassword = cb.getSenderPassword();

        JagEmail jagEmail = new JagEmail();

        jagEmail.from(senderEmail).to(senderEmail).subject("Login")
                .addText("Welcome! You gave valid information and were able to log in.");

        SmtpServer<SmtpSslServer> smtpServer = SmtpSslServer
                .create(cb.getSmtpServerName(), cb.getSmtpPort())
                .authenticateWith(senderEmail, senderPassword);
        log.info("Will start session");

        SendMailSession session = smtpServer.createSession();
        session.open();
        session.sendMail(jagEmail);
        session.close();
        log.info("Closed session");
    }

    /**
     * This function does the pretty much the same thing as the one before
     * however, instead of sending mail its receiving them.
     */
    public void validateImapString() {

        ImapSslServer imapServer = new ImapSslServer(cb.getImapServerName(),
                cb.getImapPort(), cb.getSenderEmail(),
                cb.getSenderPassword());

        ReceiveMailSession session = imapServer.createSession();
        session.open();
        session.close();
    }

    /**
     * This method checks if the database credentials are valid. If they are
     * invalid an SQLException is thrown.
     *
     * @throws SQLException
     */
    public void validateDatabase() throws SQLException {

        log.info(cb.getSqlUrl());
        log.info(cb.getSqlUsername());
        log.info(cb.getSqlPassword());
        Connection connection = DriverManager.getConnection(cb.getSqlUrl(), cb.getSqlUsername(), cb.getSqlPassword());
    }

}
