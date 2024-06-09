package com.chat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.io.File;

public class Database {
    private Connection connection;

    public Database() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdb", "root", "password");
    }

    public ResultSet query(String query, String[] params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }
        return statement.executeQuery();
    }

    public int update(String query, String[] params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }
        return statement.executeUpdate();
    }

    public void close() throws SQLException {
        connection.close();
    }

    public void logout(int userId) throws SQLException {
        update("UPDATE Users SET status = 'Offline' WHERE id = ?", new String[]{String.valueOf(userId)});
    }

    public boolean checkUserExists(String username, String email) throws SQLException {
        ResultSet result = query("SELECT * FROM Users WHERE username = ? OR email = ?", new String[]{username, email});
        return result.next();
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

    private void insertProfile(int userId, String profilePicturePath) throws SQLException {
        update("INSERT INTO UserProfile (user_id, profile_picture, bio, status) VALUES (?, ?, ?, ?)", new String[]{String.valueOf(userId), profilePicturePath, "", "Online"});
    }

    public boolean verifyUserPassword(String username, String password) throws SQLException {
        ResultSet result = query("SELECT password FROM Users WHERE username = ?", new String[]{username});
        if (result.next()) {
            String storedPassword = result.getString("password");
            return password.equals(storedPassword);
        }
        return false;
    }

    public List<UserProfile> searchUsers(String query) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.username, u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.username LIKE ? OR u.nickname LIKE ?")) {
            statement.setString(1, "%" + query + "%");
            statement.setString(2, "%" + query + "%");
            try (ResultSet result = statement.executeQuery()) {
                List<UserProfile> results = new ArrayList<>();
                while (result.next()) {
                    UserProfile userProfile = new UserProfile(result.getString("username"), result.getString("nickname"), new File(result.getString("profile_picture")), result.getString("bio"), result.getString("status"));
                    results.add(userProfile);
                }
                return results;
            }
        }
    }

    public UserProfile getUserProfile(String username) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.username = ?")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserProfile(username, result.getString("nickname"), new File(result.getString("profile_picture")), result.getString("bio"), result.getString("status"));
                } else {
                    return null;
                }
            }
        }
    }

    public List<UserProfile> getFriends(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.username, u.nickname, up.profile_picture, up.bio, up.status FROM Users u JOIN UserProfile up ON u.id = up.user_id WHERE u.id IN (SELECT friend_id FROM Friendships WHERE user_id = ?)")) {
            statement.setInt(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                List<UserProfile> friends = new ArrayList<>();
                while (result.next()) {
                    UserProfile userProfile = new UserProfile(result.getString("username"), result.getString("nickname"), new File(result.getString("profile_picture")), result.getString("bio"), result.getString("status"));
                    friends.add(userProfile);
                }
                return friends;
            }
        }
    }

    public List<Conversation> getConversations(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT u.username, up.profile_picture, m.message FROM Users u JOIN UserProfile up ON u.id = up.user_id JOIN Messages m ON u.id = m.sender_id OR u.id = m.receiver_id WHERE m.sender_id = ? OR m.receiver_id = ? GROUP BY u.id")) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            try (ResultSet result = statement.executeQuery()) {
                List<Conversation> conversations = new ArrayList<>();
                while (result.next()) {
                    String username = result.getString("username");
                    File profilePicture = new File (result.getString("profile_picture"));
                    String lastMessage = result.getString("message");
                    Conversation conversation = new Conversation(username, profilePicture, lastMessage);
                    conversations.add(conversation);
                }
                return conversations;
            }
        }
     }
     

    public List<Content> getConversation(int userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Messages WHERE sender_id = ? OR receiver_id = ?")) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            try (ResultSet result = statement.executeQuery()) {
                List<Content> conversations = new ArrayList<>();
                while (result.next()) {
                    int senderId = result.getInt("sender_id");
                    int receiverId = result.getInt("receiver_id");
                    String message = result.getString("message");
                    conversations.add(new Content(senderId, receiverId, message));
                }
                return conversations;
            }
        }
    }
}
