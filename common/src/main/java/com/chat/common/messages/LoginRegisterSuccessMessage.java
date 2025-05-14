package com.chat.messages;

import java.io.*;

import com.chat.models.User;
import com.chat.models.UserProfile;

public class LoginRegisterSuccessMessage extends Message {
    private UserProfile profile;
    private User user;

    public LoginRegisterSuccessMessage(UserProfile profile, User user) {
        super(TYPE_LOGIN_REGISTER_SUCCESS);
        this.profile = profile;
        this.user = user;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public User getUser() {
        return user;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        writeProfile(out, LoginRegisterSuccessMessage.class, profile);
        writeUser(out, LoginRegisterSuccessMessage.class, user);
    }

    public static synchronized LoginRegisterSuccessMessage readFrom(DataInputStream in) throws IOException {
        return new LoginRegisterSuccessMessage(readProfile(in, LoginRegisterSuccessMessage.class), readUser(in, LoginRegisterSuccessMessage.class));
    }
}