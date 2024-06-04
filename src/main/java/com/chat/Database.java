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

    public void registerUser(String username, String nickname, String email, String password) throws SQLException {
        update("INSERT INTO Users (username, nickname, email, password) VALUES (?, ?, ?, ?)", new String[]{username, nickname, email, password});
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