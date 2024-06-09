package com.chat.models;

import java.io.*;

public class LoginMessage extends Message {
    private String username;
    private String password;

    public LoginMessage(String username, String password) {
        super(TYPE_LOGIN);
        this.username = username;
        this.password = password;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(username.length() + password.length() + 8); // 8 is the size of the two UTF headers
        out.writeUTF(username);
        out.writeUTF(password);
    }

    public static LoginMessage readFrom(DataInputStream in, int length) throws IOException {
        String username = in.readUTF();
        String password = in.readUTF();
        return new LoginMessage(username, password);
    }
}