package com.chat.server;

import java.io.*;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.database.DatabaseAccessor;

public class Server {
    private ClientManager clientManager;
    private DatabaseAccessor databaseAccessor;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int port) throws IOException, SQLException {
        databaseAccessor = new DatabaseAccessor();
        clientManager = new ClientManager(port, databaseAccessor);
    }

    public static void main(String[] args) {
        int port = 8000; // Default port number
        try {
            Server server = new Server(port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.close();
                } catch (Exception e) {
                    logger.error("Error closing server: " + e.getMessage());
                }
            }));
            server.start();
        } catch (IOException | SQLException e) {
            logger.error("Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        clientManager.start();
    }

    public void close() {
        clientManager.close();
        try {
            databaseAccessor.close();
        } catch (SQLException e) {
            logger.error("Error closing database connection: {}", e.getSQLState());
        }
    }
}