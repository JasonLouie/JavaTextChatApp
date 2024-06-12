package com.chat.models;

public class FriendRequest {
    private int senderId;
    private int receiverId;

    public FriendRequest(int senderId, int receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }
}
