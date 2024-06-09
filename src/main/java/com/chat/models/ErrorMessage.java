package com.chat.models;

import java.io.*;

public class ErrorMessage extends Message {
    private String error;

    public ErrorMessage(String error) {
        super(TYPE_ERROR);
        this.error = error;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(error.length() + 4); // 4 is the size of the UTF header
        out.writeUTF(error);
    }

    public static ErrorMessage readFrom(DataInputStream in, int length) throws IOException {
        String error = in.readUTF();
        return new ErrorMessage(error);
    }

    public String getError() {
        return error;
    }
}