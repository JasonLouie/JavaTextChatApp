package com.chat.models;

import java.sql.Timestamp;

public class Content {
    private String receiverUsername;
    private String senderUsername;
    private String message;
    private Timestamp timestamp;

    public Content(String senderUsername, String receiverUsername, String message, Timestamp timestamp) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
