package com.chat.messages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.models.UserProfile;

public class SearchUsersMessage extends Message {
    private List<UserProfile> profiles;

    public SearchUsersMessage(List<UserProfile> profiles) {
        super(TYPE_SEARCH_USERS);
        this.profiles = profiles;
    }

    public List<UserProfile> getProfiles() {
        return profiles;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        Logger logger = LoggerFactory.getLogger(SearchUsersMessage.class);
        logger.info("Starting serialization of SearchUsersMessage...");
        out.writeByte(type);
        out.writeInt(profiles.size());
        logger.info("Wrote message type and profiles size of {}", profiles.size());
        for (UserProfile user : profiles) {
            writeProfile(out, SearchUsersMessage.class, user);
        }
        logger.info("Serialization of SearchUsersMessage completed.");
    }

    public static synchronized SearchUsersMessage readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(SearchUsersMessage.class);
        logger.info("Starting deserialization of SearchUsersMessage...");

        int length = in.readInt();
        logger.info("Profiles length: {}", length);
        List<UserProfile> profiles = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            logger.info("Deserializing profile {}/{}", i + 1, length);
            profiles.add(readProfile(in, SearchUsersMessage.class));
        }

        logger.info("Deserialization of SearchUsersMessage completed.");
        return new SearchUsersMessage(profiles);
    }
}
