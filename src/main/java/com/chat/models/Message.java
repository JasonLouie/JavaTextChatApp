package com.chat.models;

import java.io.*;

public abstract class Message {
    public static final byte TYPE_REGISTER = 1;
    public static final byte TYPE_LOGIN = 2;
    public static final byte TYPE_ERROR = 3;

    protected byte type;

    public Message(byte type) {
        this.type = type;
    }

    public abstract void writeTo(DataOutputStream out) throws IOException;

    public static Message readFrom(DataInputStream in) throws IOException {
        byte type = in.readByte();
        int length = in.readInt();
        switch (type) {
            case TYPE_REGISTER:
                return RegisterMessage.readFrom(in, length);
            case TYPE_LOGIN:
                return LoginMessage.readFrom(in, length);
            case TYPE_ERROR:
                return ErrorMessage.readFrom(in, length);
            default:
                throw new IOException("Unknown message type: " + type);
        }
    }
}

