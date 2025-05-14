package com.chat.messages;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorMessage extends Message {
    private String error;

    public ErrorMessage(String error) {
        super(TYPE_ERROR);
        this.error = error;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(error.length() + 4); // 4 is the size of the UTF header
        out.writeUTF(error);
    }

    public static synchronized ErrorMessage readFrom(DataInputStream in) throws IOException {
        Logger logger = LoggerFactory.getLogger(ErrorMessage.class);

        int length = in.readInt();
        logger.info("Length: {}", length);

        String error = in.readUTF();
        logger.info("Error: {}", error);

        return new ErrorMessage(error);
    }

    public String getError() {
        return error;
    }
}
