package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.LogoutMessage;
import com.chat.messages.RequestMessage;
import com.server.database.DatabaseAccessor;
import com.chat.messages.Message;

/**
 * Handles logout requests.
 */
public class HandleLogoutMessage extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandleLogoutMessage.class);

    public HandleLogoutMessage(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        int userId = requestMessage.getIntParam();
        logger.info("Logging out user {}...", userId);
        databaseAccessor.logout(userId);
        logger.info("Log out successful for user {}", userId);
        new LogoutMessage(true).writeTo(output);
    }
}
