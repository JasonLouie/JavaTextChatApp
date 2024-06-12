package com.chat.messages;

import java.io.*;

public class FriendSuccessMessage extends Message {
    private boolean success;

    public FriendSuccessMessage(boolean success) {
        super(TYPE_FRIEND_SUCCESS);
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

    public static synchronized FriendSuccessMessage readFrom(DataInputStream in) throws IOException {
        boolean success = in.readBoolean();
        return new FriendSuccessMessage(success);
    }
}
