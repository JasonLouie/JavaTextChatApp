package com.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.database.DatabaseAccessor;
import com.chat.messages.Message;
import com.chat.server.handlers.MessageHandler;

public class ClientThread extends Thread {
    private Socket socket;
    private MessageHandler messageHandler;
    private DataInputStream input;
    private DataOutputStream output;
    private boolean connected = true;
    private static final Logger logger = LoggerFactory.getLogger(ClientThread.class);

    public ClientThread(Socket socket, DatabaseAccessor databaseAccessor) throws IOException, SQLException {
        this.socket = socket;
        this.messageHandler = new MessageHandler(databaseAccessor);
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        try {
            while (connected) {
                Message message = Message.readFrom(input);
                logger.info("Received message from client");
                messageHandler.handleRequest(message, output);
            }
        } catch (IOException e) {
            logger.error("I/O error handling client connection: " + e.getMessage());
        } catch (SQLException e) {
            logger.error("Database error handling client connection: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error handling client connection: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void close() {
        connected = false;
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
        } catch (IOException e) {
            logger.error("Error closing client connection: " + e.getMessage());
        }
    }
}
