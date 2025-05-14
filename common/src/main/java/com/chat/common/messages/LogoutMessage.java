package com.chat.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LogoutMessage extends Message {
    private boolean success;

    public LogoutMessage(boolean success) {
        super(TYPE_LOGOUT);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeBoolean(success);
    }

    public static synchronized LogoutMessage readFrom(DataInputStream in) throws IOException {
        boolean success = in.readBoolean();
        return new LogoutMessage(success);
    }
}
