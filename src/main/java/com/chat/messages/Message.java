package com.chat.messages;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.models.FriendRequest;
import com.chat.models.Friendship;
import com.chat.models.UserProfile;

public abstract class Message {
    public static final byte TYPE_REGISTER = 1;
    public static final byte TYPE_LOGIN = 2;
    public static final byte TYPE_LOGIN_REGISTER_SUCCESS = 3;
    public static final byte TYPE_ERROR = 4;
    public static final byte TYPE_REQUEST = 5;
    public static final byte TYPE_SEARCH_USERS = 6;
    public static final byte TYPE_FRIENDS_LIST = 7;
    public static final byte TYPE_LOGOUT = 8;
    public static final byte TYPE_CONVERSATION = 9;
    public static final byte TYPE_CONVERSATIONS = 10;
    public static final byte TYPE_NO_RESULTS = 11;
    public static final byte TYPE_FRIEND_SUCCESS = 12;
    public static final byte TYPE_FRIEND_REQUESTS_LIST = 13;

    protected byte type;

    public Message(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    } 

    public abstract void writeTo(DataOutputStream out) throws IOException;

    public static synchronized Message readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(Message.class);
        byte type = in.readByte();
        logger.info("Got type: {}", type);
        switch (type) {
            case TYPE_REGISTER:
                return RegisterMessage.readFrom(in);
            case TYPE_LOGIN:
                return LoginMessage.readFrom(in);
            case TYPE_LOGIN_REGISTER_SUCCESS:
                return LoginRegisterSuccessMessage.readFrom(in);
            case TYPE_ERROR:
                return ErrorMessage.readFrom(in);
            case TYPE_REQUEST:
                return RequestMessage.readFrom(in);
            case TYPE_NO_RESULTS:
                return NoResultsMessage.readFrom(in);
            case TYPE_SEARCH_USERS:
                return SearchUsersMessage.readFrom(in);
            case TYPE_FRIENDS_LIST:
                return FriendsListMessage.readFrom(in);
            case TYPE_LOGOUT:
                return LogoutMessage.readFrom(in);
            case TYPE_FRIEND_REQUESTS_LIST:
                return FriendRequestMessage.readFrom(in);
            default:
                throw new IOException("Unknown message type: " + type);
        }
    }

    protected static synchronized void writeFile(DataOutputStream out, File file) throws IOException {
        if (file != null && file.exists()) {
            out.writeUTF(file.getName());
            out.writeLong(file.length());
            out.flush();
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            out.flush();
        } else {
            out.writeUTF("");
            out.writeLong(0);
        }
    }

    protected static synchronized File readFile(DataInputStream in, String directoryName) throws IOException {
        String fileName = in.readUTF();
        long fileSize = in.readLong();
        if (fileName.isEmpty() || fileSize == 0) {
            return null;
        }
        File file = new File(directoryName, fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            long remaining = fileSize;
            int bytesRead;
            while (remaining > 0 && (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
        }
        return file;
    }

    protected synchronized <T extends Message> void writeProfile(DataOutputStream out, Class<T> clazz, UserProfile profile) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting serialization of UserProfile...");

        // Calculate total length dynamically based on actual string lengths
        int totalLength = 4 + profile.getUsername().length() + 2 +
                        profile.getNickname().length() + 2 +
                        (profile.getBio() != null ? profile.getBio().length() : 0) + 2 +
                        (profile.getStatus() != null ? profile.getStatus().length() : 0) + 2;
        
        out.writeInt(totalLength);
        out.writeInt(profile.getUserId());
        out.writeUTF(profile.getUsername());
        out.writeUTF(profile.getNickname() != null ? profile.getNickname() : profile.getUsername());
        out.writeUTF(profile.getBio() != null ? profile.getBio() : "");
        out.writeUTF(profile.getStatus() != null ? profile.getStatus() : "");
        writeFile(out, profile.getProfilePicture());
        out.flush();
        logger.info("Serialization of UserProfile completed.");
    }

    protected static synchronized <T extends Message> UserProfile readProfile(DataInputStream in, Class<T> clazz) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting deserialization of UserProfile...");

        int length = in.readInt();
        logger.info("Length: {}", length);

        // Read each field with logging
        int userId = in.readInt();
        logger.info("UserId: {}", userId);

        String username = in.readUTF();
        logger.info("Username: {}", username);

        String nickname = in.readUTF();
        logger.info("Nickname: {}", nickname);

        String bio = in.readUTF();
        logger.info("Bio: {}", bio);

        String status = in.readUTF();
        logger.info("Status: {}", status);

        File profilePicture = readFile(in, "received_pfps");
        logger.info("Profile picture: {}", profilePicture != null ? profilePicture.getName() : "No profile picture");

        logger.info("Deserialization of UserProfile completed.");

        return new UserProfile(userId, username, nickname, bio, status, profilePicture);
    }

    protected synchronized <T extends Message> void writeFriendship(DataOutputStream out, Class<T> clazz, Friendship friendship) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting serialization of Friendship...");

        out.writeInt(8); // Total length of 2 integers
        out.writeInt(friendship.getUserId());
        out.writeInt(friendship.getFriendId());
        out.flush();
        logger.info("Serialization of Friendship completed.");
    }

    protected static synchronized <T extends Message> Friendship readFriendship(DataInputStream in, Class<T> clazz) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting deserialization of Friendship...");

        int length = in.readInt();
        logger.info("Length: {}", length);

        int userId = in.readInt();
        logger.info("UserId: {}", userId);

        int friendId = in.readInt();
        logger.info("FriendId: {}", friendId);

        logger.info("Deserialization of Friendship completed.");

        return new Friendship(userId, friendId);
    }

    protected synchronized <T extends Message> void writeFriendRequest(DataOutputStream out, Class<T> clazz, FriendRequest friendRequest) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting serialization of FriendRequest...");

        out.writeInt(8); // Total length of 2 integers
        out.writeInt(friendRequest.getSenderId());
        out.writeInt(friendRequest.getReceiverId());
        out.flush();
        logger.info("Serialization of FriendRequest completed.");
    }

    protected static synchronized <T extends Message> FriendRequest readFriendRequest(DataInputStream in, Class<T> clazz) throws IOException {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("Starting deserialization of FriendRequest...");

        int length = in.readInt();
        logger.info("Length: {}", length);

        int senderId = in.readInt();
        logger.info("SenderId: {}", senderId);

        int receiverId = in.readInt();
        logger.info("ReceiverId: {}", receiverId);

        logger.info("Deserialization of FriendRequest completed.");

        return new FriendRequest(senderId, receiverId);
    }
}
