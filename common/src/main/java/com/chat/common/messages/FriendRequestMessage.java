package com.chat.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.models.UserProfile;

public class FriendRequestMessage extends Message {
    private List<UserProfile> friendRequests;

    public FriendRequestMessage(List<UserProfile> friendRequests) {
        super(TYPE_FRIEND_REQUESTS_LIST);
        this.friendRequests = friendRequests;
    }

    public List<UserProfile> getFriendRequests() {
        return friendRequests;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("Starting serialization of FriendRequestMessage...");

        out.writeByte(getType());
        out.writeInt(friendRequests.size());
        for (UserProfile friendRequest : friendRequests) {
            writeProfile(out, getClass(), friendRequest);
        }
        out.flush();
        logger.info("Serialization of FriendRequestMessage completed.");
    }

    public static FriendRequestMessage readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(FriendRequestMessage.class);
        logger.info("Starting deserialization of FriendRequestMessage...");

        int size = in.readInt();
        logger.info("Size: {}", size);

        List<UserProfile> friendRequests = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            friendRequests.add(readProfile(in, FriendRequestMessage.class));
        }

        logger.info("Deserialization of FriendRequestMessage completed.");

        return new FriendRequestMessage(friendRequests);
    }
}