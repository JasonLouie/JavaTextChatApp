package com.chat.models;

import java.io.File;

public class UserProfile {
    private int userId;
    private String username;
    private String nickname;
    private File picture;
    private String bio;
    private String status;

    public UserProfile(int userId, String username, String nickname, String bio, String status, File picture) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.bio = bio;
        this.status = status;
        this.picture = picture;
    }

    public UserProfile(String username, String nickname, File picture) {
        this.username = username;
        this.nickname = nickname;
        this.picture = picture;
    }

    public int getUserId() {
        return userId;
    }

    public File getProfilePicture() {
        return picture;
    }

    public String getBio() {
        return bio;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }
}
