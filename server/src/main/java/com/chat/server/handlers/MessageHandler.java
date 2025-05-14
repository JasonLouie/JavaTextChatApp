package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.*;
import com.server.database.DatabaseAccessor;

/**
 * Handles incoming messages from clients and performs the necessary actions.
 */
public class MessageHandler {
    private final DatabaseAccessor databaseAccessor;
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    private final Map<Byte, MessageHandlerStrategy> messageHandlers;

    /**
     * Creates a new instance of MessageHandler.
     *
     * @param databaseAccessor the database accessor
     */
    public MessageHandler(DatabaseAccessor databaseAccessor) {
        this.databaseAccessor = databaseAccessor;
        this.messageHandlers = new HashMap<>();
        initializeHandlers();
    }

    private void initializeHandlers() {
        messageHandlers.put(Message.TYPE_LOGIN, new LoginMessageHandler(databaseAccessor));
        messageHandlers.put(Message.TYPE_REGISTER, new RegisterMessageHandler(databaseAccessor));
        messageHandlers.put(Message.TYPE_REQUEST, new RequestMessageHandler(databaseAccessor));
    }

    /**
     * Handles an incoming message from a client.
     *
     * @param message the incoming message
     * @param output  the output stream to send responses to
     * @throws IOException  if an I/O error occurs
     * @throws SQLException if a database error occurs
     */
    public void handleRequest(Message message, DataOutputStream output) throws IOException, SQLException {
        MessageHandlerStrategy handler = messageHandlers.get(message.getType());
        if (handler != null) {
            handler.handleMessage(message, output);
        } else {
            logger.error("Unknown message type: {}", message.getType());
            throw new UnsupportedOperationException("Unknown message type: " + message.getType());
        }
    }
}