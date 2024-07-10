package com.chat.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.models.Content;
import com.chat.models.Conversation;
import com.chat.models.User;
import com.chat.models.UserProfile;

import java.io.File;

public class DatabaseAccessor {
    private final Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseAccessor.class);

    public DatabaseAccessor() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdb", "root", "password");
    }

    public void close() throws SQLException {
        synchronized (this) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private ResultSet query(String query, String[] params) throws SQLException {
        synchronized (this) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            return statement.executeQuery();
        }
    }

    private int update(String query, String[] params) throws SQLException {
        synchronized (this) {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setString(i + 1, params[i]);
            }
            return statement.executeUpdate();
        }
    }

    public void setStatusOnline(int userId) throws SQLException {
        String sql = "UPDATE UserProfile SET status = 'Online' WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            synchronized (this) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error setting status online for user with ID " + userId, e);
            throw e;
        }
    }

    public void logout(int userId) throws SQLException {
        String sql = "UPDATE UserProfile SET status = 'Offline' WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            synchronized (this) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error while logging out user with ID " + userId, e);
            throw e;
        }
    }

    public boolean checkUserExists(String username, String email) throws SQLException {
        ResultSet result = query("SELECT * FROM Users WHERE username = ? OR email = ?", new String[]{username, email});
        synchronized (this) {
            return result.next();
        }
    }

    public void registerUser(String username, String nickname, String email, String password, String profilePicturePath) throws SQLException {
        int userId = insertUser(username, nickname, email, password);
        insertProfile(userId, profilePicturePath);
    }

    private int insertUser(String username, String nickname, String email, String password) throws SQLException {
        String query = "INSERT INTO Users (username, nickname, email, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, nickname);
            statement.setString(3, email);
            statement.setString(4, password);
            synchronized (this) {
                int affectedRows = statement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Inserting user failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Inserting user failed, no ID obtained.");
                    }
                }
            }
        }
    }

    private void insertProfile(int userId, String profilePicturePath) throws SQLException {
        update("INSERT INTO UserProfile (user_id, profile_picture, bio, status) VALUES (?, ?, ?, ?)", new String[]{String.valueOf(userId), profilePicturePath, "", "Online"});
    }

    public boolean verifyUserPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, password FROM Users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        String storedPassword = result.getString("password");
                        int userId = result.getInt("id");

                        if (password.equals(storedPassword)) {
                            setStatusOnline(userId);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error verifying user password for username " + username, e);
            throw e;
        }
        return false;
    }

    public boolean checkUserExists(int userId) throws SQLException {
        synchronized (this) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean checkConversationExists(int senderId, int receiverId) throws SQLException {
        synchronized (this) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE sender_id = ? AND receiver_id = ?");
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public List<UserProfile> searchUsers(String query) throws SQLException {
        String sql = "SELECT u.id, u.username, u.nickname, up.bio, up.status, up.profile_picture " +
                     "FROM Users u JOIN UserProfile up ON u.id = up.user_id " +
                     "WHERE u.username LIKE ? OR u.nickname LIKE ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    List<UserProfile> results = new ArrayList<>();
                    while (result.next()) {
                        UserProfile userProfile = new UserProfile(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("nickname"),
                            result.getString("bio"),
                            result.getString("status"),
                            new File(result.getString("profile_picture"))
                        );
                        results.add(userProfile);
                    }
                    return results;
                }
            }
        }
    }

    public UserProfile getUserProfile(String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.id, u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.username = ?")) {
            statement.setString(1, username);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return new UserProfile(result.getInt("id"), username, result.getString("nickname"), result.getString("bio"), result.getString("status"), new File(result.getString("profile_picture")));
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public User getUser(String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, nickname, email FROM Users WHERE username = ?")) {
            statement.setString(1, username);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return new User(result.getInt("id"), username, result.getString("nickname"), result.getString("email"));
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    public List<UserProfile> getFriends(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.id, u.username, u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.id IN (SELECT friend_id FROM Friendships WHERE user_id = ?)")) {
            statement.setInt(1, userId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    List<UserProfile> friends = new ArrayList<>();
                    while (result.next()) {
                        UserProfile userProfile = new UserProfile(result.getInt("id"), result.getString("username"), result.getString("nickname"), result.getString("bio"), result.getString("status"), new File(result.getString("profile_picture")));
                        friends.add(userProfile);
                    }
                    return friends;
                }
            }
        }
    }

    public List<Conversation> getConversations(int userId) throws SQLException {
        String sql = "SELECT u1.username AS sender_username, "
                    + "u2.username AS receiver_username, "
                    + "up1.profile_picture AS sender_profile_picture, "
                    + "up2.profile_picture AS receiver_profile_picture, "
                    + "m.message, "
                    + "m.sender_id, "
                    + "m.receiver_id "
                    + "FROM Messages m "
                    + "JOIN Users u1 ON m.sender_id = u1.id "
                    + "JOIN Users u2 ON m.receiver_id = u2.id "
                    + "JOIN UserProfile up1 ON u1.id = up1.user_id "
                    + "JOIN UserProfile up2 ON u2.id = up2.user_id "
                    + "WHERE (m.sender_id = ? OR m.receiver_id = ?) "
                    + "AND m.timestamp = ("
                    + "SELECT MAX(timestamp) FROM Messages sub "
                    + "WHERE (sub.sender_id = m.sender_id AND sub.receiver_id = m.receiver_id) "
                    + "OR (sub.sender_id = m.receiver_id AND sub.receiver_id = m.sender_id)"
                    + ") "
                    + "GROUP BY u1.username, u2.username, up1.profile_picture, up2.profile_picture, m.message, m.sender_id, m.receiver_id";
    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    List<Conversation> conversations = new ArrayList<>();
                    while (result.next()) {
                        String senderUsername = result.getString("sender_username");
                        String receiverUsername = result.getString("receiver_username");
                        File senderProfilePicture = new File(result.getString("sender_profile_picture"));
                        File receiverProfilePicture = new File(result.getString("receiver_profile_picture"));
                        String lastMessage = result.getString("message");

                        File otherProfilePicture;
                        if (userId == result.getInt("sender_id")) {
                            otherProfilePicture = receiverProfilePicture;
                        } else {
                            otherProfilePicture = senderProfilePicture;
                        }
    
                        Conversation conversation = new Conversation(senderUsername, receiverUsername, lastMessage, otherProfilePicture);
                        conversations.add(conversation);
                    }
                    return conversations;
                }
            }
        }
    }

    public List<Content> getConversation(int userId) throws SQLException {
        String sql = "SELECT u1.username AS sender_username, u2.username AS receiver_username, m.message, m.timestamp " +
                        "FROM Messages m " +
                        "JOIN Users u1 ON m.sender_id = u1.id " +
                        "JOIN Users u2 ON m.receiver_id = u2.id " +
                        "WHERE m.sender_id = ? OR m.receiver_id = ? " +
                        "ORDER BY m.timestamp";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    List<Content> conversations = new ArrayList<>();
                    while (result.next()) {
                        String senderUsername = result.getString("sender_username");
                        String receiverUsername = result.getString("receiver_username");
                        String message = result.getString("message");
                        Timestamp timestamp = result.getTimestamp("timestamp");
                        conversations.add(new Content(senderUsername, receiverUsername, message, timestamp));
                    }
                    return conversations;
                }
            }
        }
    }

    // Gets UserProfiles of users that sent a friend request to current user
    public List<UserProfile> getFriendRequests(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.id, u.username, u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.id IN (SELECT sender_id FROM FriendRequests WHERE receiver_id = ?)")) {
            statement.setInt(1, userId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    List<UserProfile> friends = new ArrayList<>();
                    while (result.next()) {
                        UserProfile userProfile = new UserProfile(result.getInt("id"), result.getString("username"), result.getString("nickname"), result.getString("bio"), result.getString("status"), new File(result.getString("profile_picture")));
                        friends.add(userProfile);
                    }
                    return friends;
                }
            }
        }
    }

    // Check if two users are friends
    public boolean areFriends(int userId, int friendId) throws SQLException {
        String query = "SELECT * FROM Friendships WHERE (user_id = ? AND friend_id = ?) AND (user_id = ? AND friend_id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.setInt(3, friendId);
            statement.setInt(4, userId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        }
    }
    
    // Check if friend request exists in the database
    public boolean checkFriendRequestExists(int senderId, int receiverId) throws SQLException {
        String query = "SELECT * FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        }
    }

    public boolean hasFriendRequest(int senderId, int receiverId) throws SQLException {
        String query = "SELECT * FROM FriendRequests WHERE sender_id = ? and receiver_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            synchronized (this) {
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        }
    }

    // Send friend request
    public boolean sendFriendRequest(int senderId, int receiverId) throws SQLException {
        logger.info("SenderId: {}, ReceiverId: {}", senderId, receiverId);
        String query = "INSERT INTO FriendRequests (sender_id, receiver_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            synchronized (this) {
                return statement.executeUpdate() > 0;
            }
        }
    }

    // Accept friend request
    public void acceptFriendRequest(int senderId, int receiverId) throws SQLException {
        synchronized (this) {
            // Remove the friend request
            PreparedStatement statement = connection.prepareStatement("DELETE FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?");
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.executeUpdate();

            // Add the friendship
            statement = connection.prepareStatement("INSERT INTO Friendships (user_id, friend_id) VALUES (?, ?)");
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            statement.executeUpdate();
            statement.setInt(1, receiverId);
            statement.setInt(2, senderId);
            statement.executeUpdate();
        }
    }
    
    // Used to cancel or deny a friend request
    public boolean denyFriendRequest(int senderId, int receiverId) throws SQLException {
        String query = "DELETE FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, senderId);
            statement.setInt(2, receiverId);
            synchronized (this) {
                return statement.executeUpdate() > 0;
            }
        }
    }

    // Remove friend
    public boolean removeFriend(int userId, int friendId) throws SQLException {
        String query = "DELETE FROM Friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, friendId);
            statement.setInt(3, friendId);
            statement.setInt(4, userId);
            synchronized (this) {
                return statement.executeUpdate() > 0;
            }
        }
    }
}
