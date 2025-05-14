package com.chat.messages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chat.models.Conversation;

public class ConversationsMessage extends Message {
    private List<Conversation> conversations;

    public ConversationsMessage(List<Conversation> conversations) {
        super(TYPE_CONVERSATIONS);
        this.conversations = conversations;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(conversations.size());
        for (Conversation conversation : conversations) {
            out.writeUTF(conversation.getReceiverUsername());
            out.writeUTF(conversation.getSenderUsername());
            out.writeUTF(conversation.getLastMessage());
            writeFile(out, conversation.getProfilePicture());
        }
    }

    public static ConversationsMessage readFrom(DataInputStream in) throws IOException {
        int numConversations = in.readInt();
        List<Conversation> conversations = new ArrayList<>();
        for (int i = 0; i < numConversations; i++) {
            String receiverUsername = in.readUTF();
            String senderUsername = in.readUTF();
            String lastMessage = in.readUTF();
            File picture = readFile(in, "received_pfps");
            conversations.add(new Conversation(receiverUsername, senderUsername, lastMessage, picture));
        }
        return new ConversationsMessage(conversations);
    }
}
