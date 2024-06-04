package com.chat;

import java.sql.*;

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

    public String getFriends(int userId) throws SQLException {
        ResultSet result = query("SELECT username FROM Users WHERE id IN (SELECT friend_id FROM Friendships WHERE user_id = ?)", new String[]{String.valueOf(userId)});
        StringBuilder friends = new StringBuilder();
        while (result.next()) {
            friends.append(result.getString("username")).append(", ");
        }
        return friends.toString();
    }
}
