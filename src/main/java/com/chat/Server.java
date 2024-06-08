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
    private volatile boolean running;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);
        database = new Database();
        clients = new ArrayList<>();
        running = true;
    }

    public Server() throws IOException, SQLException {
        this(8000);
    }

    public Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) {
        int port = 8000; // Default port number
        try {
            Server server = new Server(port);
            System.out.println("Server started on port " + port);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.close();
                } catch (Exception e) {
                    System.out.println("Error closing server: " + e.getMessage());
                }
            }));
            server.start();
        } catch (IOException | SQLException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                if (!running) break;
                ClientThread client = new ClientThread(socket, database);
                clients.add(client);
                client.start();
            } catch (IOException e) {
                if (!running) break; // Ignore errors if we are stopping the server
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    public synchronized void close() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
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

        public ClientThread(Socket socket, Database database) {
            this.socket = socket;
            this.database = database;
        }

        private synchronized void sendUTF(String message) throws IOException {
            output.writeUTF(message);
            output.flush();
        }

        private synchronized String receiveUTF() throws IOException {
            return input.readUTF();
        }

        private synchronized void sendLong(long value) throws IOException {
            output.writeLong(value);
            output.flush();
        }

        private synchronized long receiveLong() throws IOException {
            return input.readLong();
        }

        private synchronized void sendInt(int value) throws IOException {
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
                while (true) {
                    String command = receiveUTF();
                    System.out.println("New command");
                    switch (command) {
                        case "login":
                            try {
                                System.out.println("Login attempted");
                                login(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Login error: " + e);
                            }
                            break;
                        case "register":
                            try {
                                System.out.println("Registration attempted");
                                register(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Registration error: " + e);
                            }
                            break;
                        case "get_friends":
                            getFriends(input, output);
                            break;
                        case "logout":
                            try {
                                System.out.println("Initiated logout");
                                logout(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Logout error: " + e);
                            }
                            break;
                        case "search_users":
                            try {
                                System.out.println("Searching for users...");
                                searchUsers(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Searching error: " + e);
                            }
                            break;
                        case "get_conversations":
                            try {
                                System.out.println("Getting conversations for user...");
                                getConversations(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Getting conversations error: " + e);
                            }
                            break;
                        case "get_conversation":
                            try {
                                System.out.println("Getting a particular conversation...");
                                getConversation(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Error getting conversation: " + e);
                            }
                            break;
                    }
                }
            } catch (IOException | SQLException e) {
                System.out.println("Error handling client connection: " + e.getMessage());
            } finally {
                close();
            }
        }

        public void close() {
            try {
                socket.close();
                input.close();
                output.close();
            } catch (IOException e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
        }

        private void login(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            String username = receiveUTF();
            String password = receiveUTF();
            if (database.verifyUserPassword(username, password)) {
                System.out.println("Login success");
                sendUTF("success");
                sendUserProfile(output, database.getUserProfile(username));
            } else {
                System.out.println("Failed login");
                sendUTF("Incorrect username or password");
            }
        }

        private void register(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
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
                sendUserProfile(output, database.getUserProfile(username));
            }
        }

        private void logout(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            database.logout(user.getId());
            sendUTF("success");
            System.out.println("User id " + user.getId() + " logged out successfully");
        }

        private void searchUsers(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            try {
                String query = receiveUTF();
                List<UserProfile> results = database.searchUsers(query);
                sendUserProfiles(output, results);
            } catch (SQLException e) {
                logger.error("Error searching users", e);
                sendUTF("error");
                sendUTF("Error searching users: " + e.getMessage());
            }
        }

        private void getFriends(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            try {
                int userId = receiveInt();
                List<UserProfile> friends = database.getFriends(userId);
                sendUserProfiles(output, friends);
            } catch (SQLException e) {
                logger.error("Error getting friends", e);
                sendUTF("error");
                sendUTF("Error getting friends: " + e.getMessage());
            }
        }

        private void getConversation(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            int userId = receiveInt();
            List<Message> conversations = database.getConversation(userId);
            output.writeInt(conversations.size());
            for (Message message : conversations) {
                sendInt(message.getSenderId());
                sendInt(message.getReceiverId());
                sendUTF(message.getMessage());
            }
            output.flush();
        }

        private void getConversations(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            int userId = receiveInt();
            List<Conversation> conversations = database.getConversations(userId);
            output.writeInt(conversations.size());
            for (Conversation conversation : conversations) {
                sendUTF(conversation.getUsername());
                sendFile(conversation.getProfilePicture(), output);
                sendUTF(conversation.getLastMessage());
            }
            output.flush();
        }

        private void sendUserProfile(ObjectOutputStream output, UserProfile profile) throws IOException {
            sendUTF(profile.getUsername());
            sendUTF(profile.getNickname());
            sendFile(profile.getProfilePicture(), output);
        }

        private void sendUserProfiles(ObjectOutputStream output, List<UserProfile> profiles) throws IOException {
            output.writeInt(profiles.size());
            for (UserProfile profile : profiles) {
                sendUTF(profile.getUsername());
                sendUTF(profile.getNickname());
                sendFile(profile.getProfilePicture(), output);
                sendUTF(profile.getBio());
                sendUTF(profile.getStatus());
            }
            output.flush();
        }

        private void sendFile(File file, ObjectOutputStream output) throws IOException {
            if (!file.exists() || !file.isFile()) {
                throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
            }
            sendUTF(file.getName());
            sendLong(file.length());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
