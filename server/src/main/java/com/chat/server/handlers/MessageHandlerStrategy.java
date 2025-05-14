package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import com.chat.messages.Message;

/**
 * Interface for handling messages.
 */
public interface MessageHandlerStrategy {
    void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException;
}