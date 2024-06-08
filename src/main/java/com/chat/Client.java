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

public class Client extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Stage primaryStage;
    private final LinkedBlockingQueue<String> utfQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Long> longQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Integer> intQueue = new LinkedBlockingQueue<>();
    private Session session;

    public static void main(String[] args) {
        launch(args);
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Client() {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        if (socket == null) {
            try {
                socket = new Socket("localhost", 8000);
            } catch (Exception e) {
                System.exit(1);
            }
        }
        connect();

        new Thread(() -> {
            while (true) {
                try {
                    String message = input.readUTF();
                    utfQueue.put(message);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving UTF message", e);
                    break;
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    long value = input.readLong();
                    longQueue.put(value);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving long value", e);
                    break;
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    int value = input.readInt();
                    intQueue.put(value);
                } catch (IOException | InterruptedException e) {
                    logger.error("Error receiving int value", e);
                    break;
                }
            }
        }).start();

        showLoginScreen();
    }

    public void connect() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // Flush the output stream after creation
            input = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            System.exit(1);
        }
    }

    public void close() {
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
            System.out.println("Error closing client connection: " + e.getMessage());
        }
    }

    public Session getSession() {
        return session;
    }

    public boolean isLoggedIn() {
        return session != null;
    }

    private synchronized void sendUTF(String message) throws IOException {
        output.writeUTF(message);
        output.flush();
    }

    private synchronized String receiveUTF() throws IOException {
        try {
            return utfQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response", e);
        }
    }

    public synchronized void sendLong(long value) throws IOException {
        output.writeLong(value);
        output.flush();
    }

    public synchronized long receiveLong() throws IOException {
        try {
            return longQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for long value", e);
        }
    }

    public synchronized void sendInt(int value) throws IOException {
        output.writeInt(value);
        output.flush();
    }

    public synchronized int receiveInt() throws IOException {
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
            System.out.println("Error loading login screen: " + e.getMessage());
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
            System.out.println("Error loading register screen: " + e.getMessage());
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
            System.out.println("Error loading home screen: " + e.getMessage());
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
            System.out.println("Error loading friends screen: " + e.getMessage());
        }
    }

    private synchronized void sendFile(File file) throws IOException {
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
        output.flush();
    }

    private synchronized File receiveFile() throws IOException {
        String fileName = receiveUTF();
        long fileSize = receiveLong();
        File profilePictureDir = new File("received_pfps");
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
        return profilePicture;
    }

    public synchronized String login(String username, String password) throws IOException {
        System.out.println("Logging in...");
        sendUTF("login");
        sendUTF(username);
        sendUTF(password);
        System.out.println("Sent credentials...");
        String response = receiveUTF();
        if (response.equals("success")) {
            session = new Session(new User(username), receiveUserProfile(input), generateSessionToken());
        }
        return response;
    }

    public synchronized String register(String username, String nickname, String email, String password, File profilePicture) throws IOException {
        System.out.println("Registering...");
        sendUTF("register");
        sendUTF(username);
        sendUTF(nickname);
        sendUTF(email);
        sendUTF(password);
        sendFile(profilePicture);
        String response = receiveUTF();
        if (response.equals("success")) {
            session = new Session(new User(username), receiveUserProfile(input), generateSessionToken());
        }
        return response;
    }

    public synchronized String logout() throws IOException {
        sendUTF("logout");
        return receiveUTF();
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    private synchronized UserProfile receiveUserProfile(ObjectInputStream input) throws IOException {
        File profilePicture = receiveFile();
        return new UserProfile(receiveUTF(), receiveUTF(), profilePicture, receiveUTF(), receiveUTF());
    }

    public synchronized List<UserProfile> searchUsers(String query) throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        try {
            sendUTF("search_users");
            sendUTF(query);
            return receiveUserProfiles();
        } catch (IOException e) {
            logger.error("Error searching users", e);
            throw e;
        }
    }

    public synchronized List<UserProfile> getFriends() throws IOException {
        if (!isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        try {
            sendUTF("get_friends");
            return receiveUserProfiles();
        } catch (IOException e) {
            logger.error("Error getting friends", e);
            throw e;
        }
    }

    private synchronized List<UserProfile> receiveUserProfiles() throws IOException {
        int count = receiveInt();
        List<UserProfile> userProfiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserProfile userProfile = new UserProfile(receiveUTF(), receiveUTF(), receiveFile());
            userProfiles.add(userProfile);
        }
        return userProfiles;
    }

    public synchronized List<Message> getConversation(int userId) throws IOException {
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

    public synchronized List<Conversation> getConversations() throws IOException {
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

    private synchronized List<Message> receiveMessages() throws IOException {
        int numMessages = receiveInt();
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < numMessages; i++) {
            int senderId = receiveInt();
            int receiverId = receiveInt();
            String message = receiveUTF();
            messages.add(new Message(senderId, receiverId, message));
        }
        return messages;
    }
}
