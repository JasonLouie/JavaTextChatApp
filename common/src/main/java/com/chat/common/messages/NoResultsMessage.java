package com.chat.messages;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class NoResultsMessage extends Message {
    private String msg;

    public NoResultsMessage(String msg) {
        super(TYPE_NO_RESULTS);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeUTF(msg);
    }

    public static synchronized NoResultsMessage readFrom(DataInputStream in) throws IOException {
        String message = in.readUTF();
        return new NoResultsMessage(message);
    }
}