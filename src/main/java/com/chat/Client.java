package com.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.controller.FriendsController;
import com.chat.controller.HomeController;
import com.chat.controller.LoginController;
import com.chat.controller.RegisterController;

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Stage primaryStage;
    private boolean running = true;
    private final LinkedBlockingQueue<String> utfQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Long> longQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Integer> intQueue = new LinkedBlockingQueue<>();
    private final Object inputLock = new Object();
    private final Object outputLock = new Object();
    private Session session;

    public static void main(String[] args) {
        launch(args);
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client() {
    }

    private void initializeThreads() {
        new Thread(() -> {
            while (running) {
                try {
                    synchronized (inputLock) {
                        if (input.available() > 0) {
                            String message = input.readUTF();
                            utfQueue.put(message);
                        } else {
                            Thread.sleep(10); // Wait for 10 milliseconds before checking again
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving UTF message", e);
                    running = false;
                }
            }
        }).start();

        new Thread(() -> {
            while (running) {
                try {
                    synchronized (inputLock) {
                        if (input.available() >= 8) { // long is 8 bytes
                            long value = input.readLong();
                            longQueue.put(value);
                        } else {
                            Thread.sleep(10); // Wait for 10 milliseconds before checking again
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving long value", e);
                    running = false;
                }
            }
        }).start();

        new Thread(() -> {
            while (running) {
                try {
                    synchronized (inputLock) {
                        if (input.available() >= 4) { // int is 4 bytes
                            int value = input.readInt();
                            intQueue.put(value);
                        } else {
                            Thread.sleep(10); // Wait for 10 milliseconds before checking again
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving int value", e);
                    running = false;
                }
            }
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (socket == null) {
            try {
                socket = new Socket("localhost", 8000);
                socket.setSoTimeout(10000);
            } catch (Exception e) {
                logger.error("Error creating socket", e);
                System.exit(1);
            }
        }
        connect();
        showLoginScreen();
    }

    public void connect() {
        try {
            logger.info("Connecting to server...");
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Flush the output stream after creation
            input = new ObjectInputStream(socket.getInputStream());
            logger.info("Connected to server");
            initializeThreads();
        } catch (UnknownHostException e) {
            logger.error("Unknown host", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("Error connecting to server", e);
            System.exit(1);
        }
    }

    public void close() throws IOException {
        logger.info("Closing client...");
        if (session != null) {
            try {
                logout();
            } catch (IOException e) {
                logger.error("Error during logout", e);
            }
        }
        running = false;
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
            logger.error("Error closing client connection", e);
        }
    }

    public Session getSession() {
        return session;
    }

    public boolean isLoggedIn() {
        return session != null;
    }

    private void sendUTF(String message) throws IOException {
        synchronized (outputLock) {
            output.writeUTF(message);
            output.flush();
        }
    }

    private String receiveUTF() throws IOException {
        try {
            logger.info("Waiting to receive UTF message...");
            String message = utfQueue.take();
            logger.info("Received UTF message: " + message);
            return message;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }

    public void sendLong(long value) throws IOException {
        synchronized (outputLock) {
            output.writeLong(value);
            output.flush();
        }
    }

    public long receiveLong() throws IOException {
        try {
            return longQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for long value", e);
        }
    }

    public void sendInt(int value) throws IOException {
        synchronized (outputLock) {
            output.writeInt(value);
            output.flush();
        }
    }

    public int receiveInt() throws IOException {
        try {
            return intQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for int value", e);
        }
    }

    public void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading login screen", e);
        }
    }

    public void showRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load());
            RegisterController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading register screen", e);
        }
    }

    public void showHomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Scene scene = new Scene(loader.load());
            HomeController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading home screen", e);
        }
    }

    public void showFriendsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/friends.fxml"));
            Scene scene = new Scene(loader.load());
            FriendsController controller = loader.getController();
            controller.setClient(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            logger.error("Error loading friends screen", e);
        }
    }

    private void sendFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
        }
        sendUTF(file.getName());
        sendLong(file.length());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            synchronized (outputLock) {
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                output.flush();
            }
        }
    }

    private File receiveFile() throws IOException {
        logger.info("Waiting for file...");
        String fileName = receiveUTF();
        logger.info("Received file name: " + fileName);
        long fileSize = receiveLong();
        logger.info("Received file size: " + fileSize);
        File profilePictureDir = new File("received_pfps");
        if (!profilePictureDir.exists()) {
            profilePictureDir.mkdirs(); // Create directory if it doesn't exist
        }
        File profilePicture = new File(profilePictureDir, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(profilePicture)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            synchronized (inputLock) {
                while (fileSize > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    fileSize -= bytesRead;
                }
            }
        }
        logger.info("File received: " + profilePicture.getAbsolutePath());
        return profilePicture;
    }

    public String login(String username, String password) throws IOException {
        logger.info("Logging in...");
        sendUTF("login");
        sendUTF(username);
        sendUTF(password);
        logger.info("Sent credentials...");
        String response = receiveUTF();
        logger.info("Login response: " + response);
        if (response.equals("success")) {
            logger.info("Login successful, waiting for user profile...");
            session = new Session(new User(username), receiveUserProfile(), generateSessionToken());
            logger.info("User profile received, session created");
        } else {
            logger.info("Login failed");
        }
        return response;
    }

    public String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        logger.info("Registering...");
        sendUTF("register");
        sendUTF(username);
        sendUTF(nickname);
        sendUTF(email);
        sendUTF(password);
        sendFile(profilePicture);
        String response = receiveUTF();
        logger.info("Register response: " + response);
        if (response.equals("success")) {
            session = new Session(new User(username), receiveUserProfile(), generateSessionToken());
        }
        return response;
    }

    public String logout() throws IOException {
        sendUTF("logout");
        return receiveUTF();
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    private UserProfile receiveUserProfile() throws IOException {
        logger.info("Waiting for user profile...");
        String username = receiveUTF();
        logger.info("Received username: " + username);
        String nickname = receiveUTF();
        logger.info("Received nickname: " + nickname);
        File profilePicture = receiveFile();
        logger.info("Received profile picture: " + profilePicture.getAbsolutePath());
        return new UserProfile(username, nickname, profilePicture, receiveUTF(), receiveUTF());
    }

    public List<UserProfile> searchUsers(String query) throws IOException {
        logger.info("Method searchUsers called");
        if (!isLoggedIn()) {
            logger.error("Not logged in");
            throw new IOException("Not logged in");
        }
        try {
            logger.info("Sending search query: " + query);
            sendUTF("search_users");
            sendUTF(query);
            logger.info("Sent search query, waiting for response...");
            List<UserProfile> results = receiveUserProfiles();
            logger.info("Received search results: " + results.size() + " users found");
            return results;
        } catch (IOException e) {
            logger.error("Error searching users", e);
            throw e;
        }
    }

    public List<UserProfile> getFriends() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        try {
            sendUTF("get_friends");
            List<UserProfile> results = receiveUserProfiles();
            logger.info("Received friends: " + results.size());
            return results;
        } catch (IOException e) {
            logger.error("Error getting friends", e);
            throw e;
        }
    }

    private List<UserProfile> receiveUserProfiles() throws IOException {
        String response = receiveUTF();
        if (response.equals("no_results")) {
            logger.info("Received no results message from server");
            return new ArrayList<>(); // Return an empty list
        } else if (response.equals("error")) {
            String errorMessage = receiveUTF();
            logger.error("Error receiving user profiles: " + errorMessage);
            throw new IOException(errorMessage);
        } else {
            int count = receiveInt();
            List<UserProfile> userProfiles = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                UserProfile userProfile = new UserProfile(receiveUTF(), receiveUTF(), receiveFile());
                userProfiles.add(userProfile);
            }
            return userProfiles;
        }
    }

    public List<Content> getConversation(int userId) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        try {
            sendUTF("get_conversation");
            sendInt(userId);
            return receiveMessages();
        } catch (IOException e) {
            logger.error("Error getting conversation", e);
            throw e;
        }
    }

    public List<Conversation> getConversations() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        sendUTF("get_conversations");
        sendInt(session.getUser().getId());
        try {
            int numConversations = receiveInt();
            List<Conversation> conversations = new ArrayList<>();
            for (int i = 0; i < numConversations; i++) {
                String username = receiveUTF();
                File profilePicture = receiveFile();
                String lastMessage = receiveUTF();
                Conversation conversation = new Conversation(username, profilePicture, lastMessage);
                conversations.add(conversation);
            }
            return conversations;
        } catch (IOException e) {
            logger.error("Error getting conversations", e);
            throw e;
        }
    }

    private List<Content> receiveMessages() throws IOException {
        int numMessages = receiveInt();
        List<Content> messages = new ArrayList<>();
        for (int i = 0; i < numMessages; i++) {
            int senderId = receiveInt();
            int receiverId = receiveInt();
            String message = receiveUTF();
            messages.add(new Content(senderId, receiverId, message));
        }
        return messages;
    }
}
