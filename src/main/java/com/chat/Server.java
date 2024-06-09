package com.chat;

import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
    private ServerSocket serverSocket;
    private Database database;
    private List<ClientThread> clients;
    private boolean running;
    private int port;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);
        database = new Database();
        clients = new ArrayList<>();
        running = true;
        this.port = port;
    }

    public Logger getLogger() {
        return logger;
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
        logger.info("Server started on port " + port);
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                if (!running) break;
                ClientThread client = new ClientThread(socket, database);
                clients.add(client);
                client.start();
            } catch (IOException e) {
                if (!running) break; // Ignore errors if we are stopping the server
                logger.error("Error accepting client connection: " + e.getMessage());
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
    }

    private class ClientThread extends Thread {
        private Socket socket;
        private Database database;
        private User user;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private boolean connected = true;

        public ClientThread(Socket socket, Database database) {
            this.socket = socket;
            this.database = database;
        }

        private synchronized void sendUTF(String message) throws IOException {
            logger.info("Sending UTF message: " + message);
            output.writeUTF(message);
            output.flush();
        }

        private synchronized String receiveUTF() throws IOException {
            return input.readUTF();
        }

        private synchronized void sendLong(long value) throws IOException {
            logger.info("Sending long value: " + value);
            output.writeLong(value);
            output.flush();
        }

        private synchronized long receiveLong() throws IOException {
            return input.readLong();
        }

        private synchronized void sendInt(int value) throws IOException {
            logger.info("Sending int value: " + value);
            output.writeInt(value);
            output.flush();
        }

        private synchronized int receiveInt() throws IOException {
            return input.readInt();
        }

        public void run() {
            try {
                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                while (running && connected) {
                    String command = receiveUTF();
                    switch (command) {
                        case "login":
                            try {
                                logger.info("Login attempted");
                                login();
                            } catch (IOException | SQLException e) {
                                logger.error("Login error: " + e);
                            }
                            break;
                        case "register":
                            try {
                                logger.info("Registration attempted");
                                register();
                            } catch (IOException | SQLException e) {
                                logger.error("Registration error: " + e);
                            }
                            break;
                        case "get_friends":
                            getFriends();
                            break;
                        case "logout":
                            try {
                                logger.info("Initiated logout");
                                logout();
                            } catch (IOException | SQLException e) {
                                logger.error("Logout error: " + e);
                            }
                            break;
                        case "search_users":
                            try {
                                logger.info("Searching for users...");
                                searchUsers();
                            } catch (IOException | SQLException e) {
                                logger.error("Searching error: " + e);
                            }
                            break;
                        case "get_conversations":
                            try {
                                logger.info("Getting conversations for user...");
                                getConversations();
                            } catch (IOException | SQLException e) {
                                logger.error("Getting conversations error: " + e);
                            }
                            break;
                        case "get_conversation":
                            try {
                                logger.info("Getting a particular conversation...");
                                getConversation();
                            } catch (IOException | SQLException e) {
                                logger.error("Error getting conversation: " + e);
                            }
                            break;
                    }
                }
            } catch (IOException | SQLException e) {
                logger.error("Error handling client connection: " + e.getMessage());
            } finally {
                close();
            }
        }

        public void close() {
            connected = false;
            try {
                socket.close();
                input.close();
                output.close();
            } catch (IOException e) {
                logger.error("Error closing client connection: " + e.getMessage());
            }
        }

        private void login() throws IOException, SQLException {
            String username = receiveUTF();
            String password = receiveUTF();
            if (database.verifyUserPassword(username, password)) {
                logger.info("Login success");
                sendUTF("success");
                sendUserProfile(database.getUserProfile(username));
            } else {
                logger.info("Failed login");
                sendUTF("Incorrect username or password");
            }
        }

        private void register() throws IOException, SQLException {
            String username = receiveUTF();
            String nickname = receiveUTF();
            String email = receiveUTF();
            String password = receiveUTF();
            String fileName = receiveUTF();
            long fileSize = receiveLong();
            File profilePictureDir = new File("profile_pictures");
            if (!profilePictureDir.exists()) {
                profilePictureDir.mkdirs(); // Create directory if it doesn't exist
            }
            File profilePicture = new File(profilePictureDir, fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(profilePicture)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (fileSize > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    fileSize -= bytesRead;
                }
            }
            if (database.checkUserExists(username, email)) {
                sendUTF("user_already_exists");
            } else {
                database.registerUser(username, nickname, email, password, profilePicture.getAbsolutePath());
                sendUTF("success");
                sendUserProfile(database.getUserProfile(username));
            }
        }

        private void logout() throws IOException, SQLException {
            database.logout(user.getId());
            sendUTF("success");
            logger.info("User id " + user.getId() + " logged out successfully");
        }

        private void searchUsers() throws IOException, SQLException {
            try {
                String query = receiveUTF();
                List<UserProfile> results = database.searchUsers(query);
                logger.info("Found " + results.size() + " users matching the query: " + query);
                if (results.isEmpty()) {
                    sendUTF("no_results");
                    logger.info("No users exist with a username or nickname like " + query);
                } else {
                    logger.info("Sent the list of users with a username or nickname ");
                    sendUserProfiles(results);
                }
            } catch (SQLException e) {
                logger.error("Error searching users", e);
                sendUTF("error");
                sendUTF("Error searching users: " + e.getMessage());
            }
        }

        private void getFriends() throws IOException, SQLException {
            try {
                int userId = receiveInt();
                List<UserProfile> friends = database.getFriends(userId);
                logger.info("Sent the list of friends to user");
                sendUserProfiles(friends);
            } catch (SQLException e) {
                logger.error("Error getting friends", e);
                sendUTF("error");
                sendUTF("Error getting friends: " + e.getMessage());
            }
        }

        private void getConversation() throws IOException, SQLException {
            int userId = receiveInt();
            List<Content> conversations = database.getConversation(userId);
            output.writeInt(conversations.size());
            for (Content message : conversations) {
                sendInt(message.getSenderId());
                sendInt(message.getReceiverId());
                sendUTF(message.getMessage());
            }
            output.flush();
        }

        private void getConversations() throws IOException, SQLException {
            int userId = receiveInt();
            List<Conversation> conversations = database.getConversations(userId);
            output.writeInt(conversations.size());
            for (Conversation conversation : conversations) {
                sendUTF(conversation.getUsername());
                sendFile(conversation.getProfilePicture());
                sendUTF(conversation.getLastMessage());
            }
            output.flush();
        }

        private void sendUserProfile(UserProfile profile) throws IOException {
            logger.info("Sending user profile...");
            sendUTF(profile.getUsername());
            sendUTF(profile.getNickname());
            sendFile(profile.getProfilePicture());
            sendUTF(profile.getBio());
            sendUTF(profile.getStatus());
            logger.info("User profile sent");
        }

        private void sendUserProfiles(List<UserProfile> profiles) throws IOException {
            output.writeInt(profiles.size());
            for (UserProfile profile : profiles) {
                sendUTF(profile.getUsername());
                sendUTF(profile.getNickname());
                sendFile(profile.getProfilePicture());
                sendUTF(profile.getBio());
                sendUTF(profile.getStatus());
            }
            output.flush();
        }

        private void sendFile(File file) throws IOException {
            if (!file.exists() || !file.isFile()) {
                throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
            }
            logger.info("Sending file: " + file.getAbsolutePath());
            sendUTF(file.getName());
            sendLong(file.length());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.flush();
            logger.info("File sent: " + file.getAbsolutePath());
        }        
    }
}
