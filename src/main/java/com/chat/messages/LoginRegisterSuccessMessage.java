package com.chat.messages;

import java.io.*;

import com.chat.models.UserProfile;

public class LoginRegisterSuccessMessage extends Message {
    private UserProfile profile;

    public LoginRegisterSuccessMessage(UserProfile profile) {
        super(TYPE_LOGIN_REGISTER_SUCCESS);
        this.profile = profile;
    }

    public UserProfile getProfile() {
        return profile;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        writeProfile(out, LoginRegisterSuccessMessage.class, profile);
    }

    public static synchronized LoginRegisterSuccessMessage readFrom(DataInputStream in) throws IOException {
        return new LoginRegisterSuccessMessage(readProfile(in, LoginRegisterSuccessMessage.class));
    }
}