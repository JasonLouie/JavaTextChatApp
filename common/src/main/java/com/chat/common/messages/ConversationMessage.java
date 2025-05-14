package com.chat.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.chat.models.Content;

public class ConversationMessage extends Message {
    private List<Content> conversation;

    public ConversationMessage(List<Content> conversation) {
        super(TYPE_CONVERSATION);
        this.conversation = conversation;
    }

    public List<Content> getConversation() {
        return conversation;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(conversation.size());
        for (Content content : conversation){
            out.writeUTF(content.getReceiverUsername());
            out.writeUTF(content.getSenderUsername());
            out.writeUTF(content.getMessage());
            out.writeLong(content.getTimestamp().getTime());
        }
    }

    public static ConversationMessage readFrom(DataInputStream in) throws IOException {
        int size = in.readInt();
        List<Content> conversation = new ArrayList<>(size);
        for (int i = 0; i < size; i++){
            String recipientUsername = in.readUTF();
            String senderUsername = in.readUTF();
            String lastMessage = in.readUTF();
            Timestamp timestamp = new Timestamp(in.readLong());
            conversation.add(new Content(recipientUsername, senderUsername, lastMessage, timestamp));
        }
        return new ConversationMessage(conversation);
    }
}
