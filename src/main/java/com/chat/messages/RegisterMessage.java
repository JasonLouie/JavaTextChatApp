package com.chat.messages;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterMessage extends Message {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private File profilePicture;

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public File getProfilePicture() {
        return profilePicture;
    }

    public RegisterMessage(String username, String nickname, String email, String password, File profilePicture) {
        super(TYPE_REGISTER);
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        Logger logger = LoggerFactory.getLogger(RegisterMessage.class);
        logger.info("Starting serialization of RegisterMessage...");

        out.writeByte(type);
        int totalLength = 4 + username.length() + 2 +  nickname.length() + 2 + email.length() + 2 + password.length() + 2;
        logger.info("Calculated message length: {}", totalLength);

        out.writeInt(totalLength);
        out.writeUTF(username);
        out.writeUTF(nickname);
        out.writeUTF(email);
        out.writeUTF(password);
        writeFile(out, profilePicture);
        out.flush();
        logger.info("Serialization of RegisterMessage completed.");
    }

    public static synchronized RegisterMessage readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(RegisterMessage.class);
        logger.info("Starting deserialization of RegisterMessage...");

        int length = in.readInt();
        logger.info("Length: {}", length);

        String username = in.readUTF();
        logger.info("Username: {}", username);

        String nickname = in.readUTF();
        logger.info("Nickname: {}", nickname);

        String email = in.readUTF();
        logger.info("Email: {}", email);

        String password = in.readUTF();
        logger.info("Password, {}", password);

        File profilePicture = readFile(in, "profile_pictures");
        logger.info("Profile picture: {}", profilePicture != null ? profilePicture.getName() : "No profile picture");

        logger.info("Deserialization of RegisterMessage completed.");

        return new RegisterMessage(username, nickname, email, password, profilePicture);
    }
}