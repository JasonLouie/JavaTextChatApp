package com.chat.messages;

import java.io.*;

public class RequestMessage extends Message {
    public static final byte REQUEST_GET_FRIENDS = 1;
    public static final byte REQUEST_GET_CONVERSATIONS = 2;
    public static final byte REQUEST_GET_CONVERSATION = 3;
    public static final byte REQUEST_SEARCH_USERS = 4;
    public static final byte REQUEST_LOGOUT = 5;
    public static final byte REQUEST_SEND_FRIEND_REQUEST = 6;
    public static final byte REQUEST_REMOVE_FRIEND = 7;
    public static final byte REQUEST_FRIENDS_WITH = 8;
    public static final byte REQUEST_HAS_FRIEND_REQUEST = 9;
    public static final byte REQUEST_DENY_FRIEND_REQUEST = 10;
    public static final byte REQUEST_ACCEPT_FRIEND_REQUEST = 11;
    public static final byte REQUEST_CANCEL_FRIEND_REQUEST = 12;
    public static final byte REQUEST_GET_FRIEND_REQUESTS = 13;

    private byte requestType;
    private int intParam;
    private int secondIntParam;
    private String stringParam;
    private String secondStringParam;

    public RequestMessage(byte requestType, int intParam) {
        super(TYPE_REQUEST);
        this.requestType = requestType;
        this.intParam = intParam;
    }

    public RequestMessage(byte requestType, String stringParam) {
        super(TYPE_REQUEST);
        this.requestType = requestType;
        this.stringParam = stringParam;
    }

    public RequestMessage(byte requestType, int intParam, int secondIntParam) {
        super(TYPE_REQUEST);
        this.requestType = requestType;
        this.intParam = intParam;
        this.secondIntParam = secondIntParam;
    }

    public byte getRequestType() {
        return requestType;
    }

    public int getIntParam() {
        return intParam;
    }

    public int getSecondIntParam() {
        return secondIntParam;
    }

    public String getStringParam() {
        return stringParam;
    }

    public String getSecondStringParam() {
        return secondStringParam;
    }

    @Override
    public synchronized void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeByte(requestType);
        if (requestType == REQUEST_GET_FRIENDS || requestType == REQUEST_GET_CONVERSATIONS || requestType == REQUEST_LOGOUT) {
            out.writeInt(intParam);
        } else if (requestType == REQUEST_SEARCH_USERS) {
            out.writeUTF(stringParam);
        } else if (requestType == REQUEST_GET_CONVERSATION || requestType == REQUEST_FRIENDS_WITH || requestType == REQUEST_DENY_FRIEND_REQUEST || requestType == REQUEST_SEND_FRIEND_REQUEST || requestType == REQUEST_HAS_FRIEND_REQUEST || requestType == REQUEST_REMOVE_FRIEND || requestType == REQUEST_ACCEPT_FRIEND_REQUEST || requestType == REQUEST_CANCEL_FRIEND_REQUEST){
            out.writeInt(intParam);
            out.writeInt(secondIntParam);
        }
    }

    public static synchronized RequestMessage readFrom(DataInputStream in) throws IOException {
        byte requestType = in.readByte();
        if (requestType == REQUEST_GET_FRIENDS || requestType == REQUEST_GET_CONVERSATIONS || requestType == REQUEST_LOGOUT || requestType == REQUEST_SEND_FRIEND_REQUEST || requestType == REQUEST_REMOVE_FRIEND) {
            int intParam = in.readInt();
            return new RequestMessage(requestType, intParam);
        } else if (requestType == REQUEST_SEARCH_USERS) {
            String stringParam = in.readUTF();
            return new RequestMessage(requestType, stringParam);
        } else if (requestType == REQUEST_GET_CONVERSATION || requestType == REQUEST_FRIENDS_WITH || requestType == REQUEST_DENY_FRIEND_REQUEST || requestType == REQUEST_SEND_FRIEND_REQUEST || requestType == REQUEST_HAS_FRIEND_REQUEST || requestType == REQUEST_REMOVE_FRIEND || requestType == REQUEST_ACCEPT_FRIEND_REQUEST || requestType == REQUEST_CANCEL_FRIEND_REQUEST){
            int intParam = in.readInt();
            int secondIntParam = in.readInt();
            return new RequestMessage(requestType, intParam, secondIntParam);
        }else {
            throw new IOException("Unknown request type");
        }
    }
}