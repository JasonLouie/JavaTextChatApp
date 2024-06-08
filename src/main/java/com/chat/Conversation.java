package com.chat;

import java.io.File;

public class Conversation {
    private String username;
    private String message;
    private File picture;
    
    public Conversation(String username, File picture, String message){
        this.username = username;
        this.picture = picture;
        this.message = message;
    }
    
    public File getProfilePicture() {
        return picture;
    }

    public String getLastMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
