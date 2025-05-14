package com.chat.messages;

import java.io.*;

public class LoginMessage extends Message {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LoginMessage(String username, String password) {
        super(TYPE_LOGIN);
        this.username = username;
        this.password = password;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.flush();
        out.writeInt(username.length() + password.length() + 8); // 8 is the size of the two UTF headers
        out.flush();
        out.writeUTF(username);
        out.flush();
        out.writeUTF(password);
        out.flush();
    }

    public static synchronized LoginMessage readFrom(DataInputStream in) throws IOException {
        @SuppressWarnings("unused")
        int length = in.readInt();
        String username = in.readUTF();
        String password = in.readUTF();
        return new LoginMessage(username, password);
    }
}