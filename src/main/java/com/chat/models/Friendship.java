package com.chat.models;

public class Friendship {
    private int userId;
    private int friendId;

    public Friendship(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getUserId() {
        return userId;
    }

    public int getFriendId() {
        return friendId;
    }
}
