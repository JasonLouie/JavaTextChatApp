package com.chat.messages;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.models.UserProfile;

public class FriendsListMessage extends Message {
    private List<UserProfile> friends;
    private int userId;

    public FriendsListMessage(int userId) {
        super(TYPE_FRIENDS_LIST);
        this.userId = userId;
    }

    public FriendsListMessage(List<UserProfile> friends) {
        super(TYPE_FRIENDS_LIST);
        this.friends = friends;
    }

    public List<UserProfile> getFriends() {
        return friends;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        Logger logger = LoggerFactory.getLogger(FriendsListMessage.class);
        logger.info("Starting serialization of FriendsListMessage...");
        out.writeByte(type);
        out.writeInt(friends.size()); // Write the number of friends
        for (UserProfile friend : friends) {
            writeProfile(out, FriendsListMessage.class, friend);
        }
        logger.info("Serialization of FriendsListMessage completed.");
    }

    public static synchronized FriendsListMessage readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(FriendsListMessage.class);
        logger.info("Starting deserialization of FriendsListMessage...");

        int length = in.readInt();
        logger.info("Profiles length: {}", length);
        List<UserProfile> friends = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            logger.info("Deserializing profile {}/{}", i + 1, length);
            friends.add(readProfile(in, FriendsListMessage.class));
        }

        logger.info("Deserialization of FriendsListMessage completed.");
        return new FriendsListMessage(friends);
    }
}