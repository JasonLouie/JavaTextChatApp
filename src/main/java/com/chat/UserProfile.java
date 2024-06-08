package com.chat;

import java.io.File;

public class UserProfile {
    private String username;
    private String nickname;
    private File picture;
    private String bio;
    private String status;

    public UserProfile(String username, String nickname, File picture, String bio, String status) {
        this.username = username;
        this.nickname = nickname;
        this.picture = picture;
        this.bio = bio;
        this.status = status;
    }

    public UserProfile(String username, String nickname, File picture) {
        this.username = username;
        this.nickname = nickname;
        this.picture = picture;
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
