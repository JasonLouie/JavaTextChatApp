package com.chat;

import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.ConversationMessage;
import com.chat.messages.ConversationsMessage;
import com.chat.messages.ErrorMessage;
import com.chat.messages.FriendRequestMessage;
import com.chat.messages.FriendSuccessMessage;
import com.chat.messages.FriendsListMessage;
import com.chat.messages.LoginMessage;
import com.chat.messages.LoginRegisterSuccessMessage;
import com.chat.messages.LogoutMessage;
import com.chat.messages.Message;
import com.chat.messages.NoResultsMessage;
import com.chat.messages.RegisterMessage;
import com.chat.messages.RequestMessage;
import com.chat.messages.SearchUsersMessage;
import com.chat.models.*;

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
        clients.clear();
    }

    private class ClientThread extends Thread {
        private Socket socket;
        private Database database;
        private DataInputStream input;
        private DataOutputStream output;
        private boolean connected = true;

        public ClientThread(Socket socket, Database database) {
            this.socket = socket;
            this.database = database;
        }

        public synchronized void run() {
            try {
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                output.flush();
                while (running && connected) {
                    Message message = Message.readFrom(input);
                    logger.info("Received message from client");
                    handleRequest(message);
                }
            } catch (IOException | SQLException e) {
                logger.error("Error handling client connection: " + e.getMessage());
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
            } finally {
                socket = null;
                input = null;
                output = null;
            }
        }

        private synchronized void handleRequest(Message message) throws IOException, SQLException {
            switch (message.getType()) {
                case Message.TYPE_LOGIN:
                    handleLogin((LoginMessage) message);
                    break;
                case Message.TYPE_REGISTER:
                    handleRegister((RegisterMessage) message);
                    break;
                case Message.TYPE_REQUEST:
                    handleRequestMessage((RequestMessage) message);
                    break;
                default:
                    logger.error("Unknown message type: " + message.getType());
                    break;
            }
        }

        private synchronized void handleLogin(LoginMessage message) throws IOException, SQLException {
            String username = message.getUsername();
            String password = message.getPassword();
            if (database.verifyUserPassword(username, password)) {
                logger.info("Login success");
                new LoginRegisterSuccessMessage(database.getUserProfile(username)).writeTo(output);
                logger.info("Sent user profile");
            } else {
                logger.info("Failed login");
                new ErrorMessage("Incorrect username or password").writeTo(output);
            }
        }

        private synchronized void handleLogout(RequestMessage message) throws IOException, SQLException {
            int userId = message.getIntParam();
            logger.info("Logging out user {}...", userId );
            database.logout(userId);
            logger.info("Log out successful");
            new LogoutMessage(true).writeTo(output);
        }

        private synchronized void handleRegister(RegisterMessage message) throws IOException, SQLException {
            logger.info("Receiving registration info...");
            String username = message.getUsername();
            String nickname = message.getNickname();
            String email = message.getEmail();
            String password = message.getPassword();
            File profilePicture = message.getProfilePicture();
            logger.info("Received registration info");
            if (database.checkUserExists(username, email)) {
                logger.error("User already exists");
                new ErrorMessage("User already exists").writeTo(output);
            } else {
                logger.info("Registering user...");
                database.registerUser(username, nickname, email, password, profilePicture.getAbsolutePath());
                logger.info("Registered user");
                new LoginRegisterSuccessMessage(database.getUserProfile(username)).writeTo(output);
            }
        }

        private synchronized void handleRequestMessage(RequestMessage message) throws IOException, SQLException {
            switch (message.getRequestType()) {
                case RequestMessage.REQUEST_GET_CONVERSATIONS:
                    handleGetConversations(message.getIntParam());
                    break;
                case RequestMessage.REQUEST_GET_CONVERSATION:
                    handleGetConversation(message.getIntParam(), message.getSecondIntParam());
                    break;
                case RequestMessage.REQUEST_GET_FRIENDS:
                    handleGetFriends(message.getIntParam());
                    break;
                case RequestMessage.REQUEST_SEARCH_USERS:
                    handleSearchUsers(message);
                    break;
                case RequestMessage.REQUEST_LOGOUT:
                    handleLogout(message);
                    break;
                case RequestMessage.REQUEST_GET_FRIEND_REQUESTS:
                    handleGetFriendRequests(message);
                    break;
                case RequestMessage.REQUEST_SEND_FRIEND_REQUEST:
                    handleSendFriendRequest(message);
                    break;
                case RequestMessage.REQUEST_ACCEPT_FRIEND_REQUEST:
                    handleAcceptFriendRequest(message);
                    break;
                case RequestMessage.REQUEST_DENY_FRIEND_REQUEST:
                    handleDenyFriendRequest(message);
                    break;
                default:
                    logger.error("Unknown request type: {}", message.getRequestType());
                    break;
            }
        }

        private synchronized void handleGetConversations(int userId) throws IOException, SQLException {
            if (database.checkUserExists(userId)) {
                List<Conversation> conversations = database.getConversations(userId);
                if (conversations.isEmpty()){
                    new NoResultsMessage("User does not have any conversations");
                }
                new ConversationsMessage(conversations).writeTo(output);
            } else {
                new ErrorMessage("User does not exist").writeTo(output);
            }
        }

        private synchronized void handleGetConversation(int senderId, int receiverId) throws IOException, SQLException {
            if (database.checkConversationExists(senderId, receiverId)) {
                List<Content> conversation = database.getConversation(receiverId);
                new ConversationMessage(conversation).writeTo(output);
            } else {
                new NoResultsMessage("User " + senderId + " does not have any conversation with " + receiverId).writeTo(output);
            }
        }

        private synchronized void handleGetFriends(int userId) throws IOException, SQLException {
            List<UserProfile> friends = database.getFriends(userId);
            if (friends.size() != 0){
                logger.info("Successfully retrieved friends list");
                new FriendsListMessage(friends).writeTo(output);
            } else {
                logger.info("No friends found for user {}", userId);
                new NoResultsMessage("No friends found").writeTo(output);
            }
        }

        private synchronized void handleSearchUsers(RequestMessage message) throws IOException, SQLException {
            String query = message.getStringParam();
            List<UserProfile> results = database.searchUsers(query);
            if (results.size() != 0){
                logger.info("Found " + results.size() + " users with the username or nickname " + query);
                new SearchUsersMessage(results).writeTo(output);
            } else {
                logger.info("No user with username or nickname {} found", query);
                new NoResultsMessage("No user with username or nickname " + query).writeTo(output);
            }
        }

        private synchronized void handleGetFriendRequests(RequestMessage message) throws IOException, SQLException {
            int userId = message.getIntParam();
            List<UserProfile> friendRequests = database.getFriendRequests(userId);
            if (friendRequests.size() != 0){
                logger.info("Successfully retrieved friend requests");
                new FriendRequestMessage(friendRequests);
            } else{
                logger.info("No friend requests found for user {}", userId);
                new NoResultsMessage("No friend requests found").writeTo(output);
            }
        }

        private synchronized void handleAcceptFriendRequest(RequestMessage message) throws IOException, SQLException {
            int senderId = message.getIntParam();
            int receiverId = message.getSecondIntParam();
            if (database.checkFriendRequestExists(receiverId, senderId)) {
                database.acceptFriendRequest(receiverId, senderId);
                new FriendSuccessMessage(true).writeTo(output);
            } else {
                new FriendSuccessMessage(false).writeTo(output);
            }
        }

        private synchronized void handleDenyFriendRequest(RequestMessage message) throws IOException, SQLException {
            int senderId = message.getIntParam();
            int receiverId = message.getSecondIntParam();
            if (database.checkFriendRequestExists(receiverId, senderId)) {
                database.denyFriendRequest(receiverId, senderId);
                new FriendSuccessMessage(true).writeTo(output);
            } else {
                new FriendSuccessMessage(false).writeTo(output);
            }
        }

        private synchronized void handleSendFriendRequest(RequestMessage message) throws IOException, SQLException {
            int senderId = message.getIntParam();
            int receiverId = message.getSecondIntParam();
            if (database.checkFriendRequestExists(receiverId, senderId)) {
                new FriendSuccessMessage(false).writeTo(output);
            } else {
                database.sendFriendRequest(senderId, receiverId);
                new FriendSuccessMessage(true).writeTo(output);
            }
        }
    }
}
