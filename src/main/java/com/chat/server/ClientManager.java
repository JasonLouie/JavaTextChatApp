package com.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.database.DatabaseAccessor;

public class ClientManager {
    private ServerSocket serverSocket;
    private List<ClientThread> clients;
    private boolean running;
    private static final Logger logger = LoggerFactory.getLogger(ClientManager.class);
    private DatabaseAccessor databaseAccessor; // Add databaseAccessor here

    public ClientManager(int port, DatabaseAccessor databaseAccessor) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clients = new CopyOnWriteArrayList<>(); // Use a thread-safe list
        this.running = true;
        this.databaseAccessor = databaseAccessor; // Initialize databaseAccessor
    }

    public void start() {
        logger.info("Server started on port " + serverSocket.getLocalPort());
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                if (!running) break;
                ClientThread client = new ClientThread(socket, databaseAccessor);
                clients.add(client);
                client.start();
            } catch (IOException e) {
                if (!running) break; // Ignore errors if we are stopping the server
                logger.error("Error accepting client connection: " + e.getMessage());
            } catch (SQLException e) {
                logger.error("Database error: " + e.getMessage());
            }
        }
    }

    public synchronized void close() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Error closing server socket: " + e.getMessage());
        }
        for (ClientThread client : clients) {
            client.close();
        }
        clients.clear();
    }
}