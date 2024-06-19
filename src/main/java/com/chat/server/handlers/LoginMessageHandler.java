package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.LoginMessage;
import com.chat.messages.Message;
import com.chat.messages.LoginRegisterSuccessMessage;
import com.chat.database.DatabaseAccessor;
import com.chat.messages.ErrorMessage;

/**
 * Handles login messages.
 */
public class LoginMessageHandler extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginMessageHandler.class);

    public LoginMessageHandler(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        LoginMessage loginMessage = (LoginMessage) message;
        String username = loginMessage.getUsername();
        String password = loginMessage.getPassword();
        if (databaseAccessor.verifyUserPassword(username, password)) {
            logger.info("Login success for user: {}", username);
            new LoginRegisterSuccessMessage(databaseAccessor.getUserProfile(username), databaseAccessor.getUser(username)).writeTo(output);
        } else {
            logger.warn("Failed login attempt for user: {}", username);
            new ErrorMessage("Incorrect username or password").writeTo(output);
        }
    }
}