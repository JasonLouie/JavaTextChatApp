package com.chat.client;

import java.util.UUID;

import com.chat.models.User;
import com.chat.models.UserProfile;

public class Session {
    private User user;
    private UserProfile userProfile;
    private String sessionToken;

    public Session(User user, UserProfile userProfile) {
        this.user = user;
        this.userProfile = userProfile;
        sessionToken = UUID.randomUUID().toString();;
    }

    public User getUser() {
        return user;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void inValidate() {
        user = null;
        userProfile = null;
        sessionToken = null;
    }
}