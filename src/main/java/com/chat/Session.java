package com.chat;

public class Session {
    private User user;
    private UserProfile userProfile;
    private String sessionToken;

    public Session(User user, UserProfile userProfile, String sessionToken) {
        this.user = user;
        this.userProfile = userProfile;
        this.sessionToken = sessionToken;
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

    public void invalidate() {
        user = null;
        userProfile = null;
        sessionToken = null;
    }
}