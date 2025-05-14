package com.chat.models;

import java.io.File;

public class Conversation {
    private String receiverUsername;
    private String senderUsername;
    private String lastMessage;
    private File picture;
    
    public Conversation(String receiverUsername, String senderUsername, String lastMessage, File picture){
        this.receiverUsername = receiverUsername;
        this.senderUsername = senderUsername;
        this.lastMessage = lastMessage;
        this.picture = picture;
    }
    
    public File getProfilePicture() {
        return picture;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }
}
