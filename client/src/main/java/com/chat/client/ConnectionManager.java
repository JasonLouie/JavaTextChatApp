package com.chat.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.chat.messages.Message;

public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;

    public void connect() {
        try {
            socket = new Socket("localhost", 8000);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            logger.info("Connected to server");
        } catch (IOException e) {
            logger.error("Error connecting to server: {}", e.getMessage());
        }
    }

    public <T extends Message> void sendMessage(T message) {
        try {
            message.writeTo(output);
        } catch (IOException e) {
            logger.error("Error sending message: {}", e.getMessage());
        }
    }

    public Message readResponse() {
        try {
            return Message.readFrom(input);
        } catch (IOException e) {
            logger.error("Error reading response: {}", e.getMessage());
            return null;
        }
    }

    public void close() {
        logger.info("Closing client connection...");
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        } catch (IOException e) {
            logger.error("Error closing client connection: {}", e.getMessage());
        } finally {
            socket = null;
            input = null;
            output = null;
        }
    }
}
