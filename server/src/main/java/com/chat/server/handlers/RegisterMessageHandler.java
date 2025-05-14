package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.RegisterMessage;
import com.server.database.DatabaseAccessor;
import com.chat.messages.Message;
import com.chat.messages.LoginRegisterSuccessMessage;
import com.chat.messages.ErrorMessage;

/**
 * Handles register messages.
 */
public class RegisterMessageHandler extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(RegisterMessageHandler.class);

    public RegisterMessageHandler(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RegisterMessage registerMessage = (RegisterMessage) message;
        logger.info("Processing registration for user: {}", registerMessage.getUsername());
        String username = registerMessage.getUsername();
        String email = registerMessage.getEmail();
        if (databaseAccessor.checkUserExists(username, email)) {
            logger.error("User already exists: {}", username);
            new ErrorMessage("User already exists").writeTo(output);
        } else {
            databaseAccessor.registerUser(registerMessage.getUsername(), registerMessage.getNickname(), registerMessage.getEmail(), registerMessage.getPassword(), registerMessage.getProfilePicture().getAbsolutePath());
            logger.info("Registered new user: {}", username);
            new LoginRegisterSuccessMessage(databaseAccessor.getUserProfile(username), databaseAccessor.getUser(username)).writeTo(output);
        }
    }
}