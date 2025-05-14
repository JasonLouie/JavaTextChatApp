package com.chat.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.ConversationMessage;
import com.chat.messages.ConversationsMessage;
import com.chat.messages.Message;
import com.chat.messages.RequestMessage;
import com.chat.models.Content;
import com.chat.models.Conversation;

public class ConversationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConversationManager.class);
    private ConnectionManager connectionManager;
    private Client client;

    public ConversationManager(ConnectionManager connectionManager, Client client) {
        this.connectionManager = connectionManager;
        this.client = client;
    }

    public synchronized List<Conversation> getConversations() {
        /*
        if (!client.isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        */
        logger.info("Getting list of conversations for client");
        try {
            RequestMessage request = new RequestMessage(RequestMessage.REQUEST_GET_CONVERSATIONS, client.getUserId());
            connectionManager.sendMessage(request);
            Message response = connectionManager.readResponse();
            if (response instanceof ConversationsMessage) {
                ConversationsMessage conversationsMessage = (ConversationsMessage) response;
                logger.info("Received conversations list");
                return conversationsMessage.getConversations();
            } else {
                logger.error("Invalid response type");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting conversations: {}", e.getMessage());
            return null;
        }
    }

    public synchronized List<Content> getConversation(int userId) {
        /*
        if (!client.isLoggedIn()) {
            throw new IOException("Not logged in");
        }
        */
        logger.info("Getting conversation between client and user {}", userId);
        try {
            RequestMessage request = new RequestMessage(RequestMessage.REQUEST_GET_CONVERSATION, client.getUserId(), userId);
            connectionManager.sendMessage(request);
            Message response = connectionManager.readResponse();
            if (response instanceof ConversationMessage) {
                ConversationMessage conversationMessage = (ConversationMessage) response;
                logger.info("Received conversation");
                return conversationMessage.getConversation();
            } else {
                logger.error("Invalid response type");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting conversation between client and user {}: {}", userId, e.getMessage());
            return null;
        }
    }
}
