package com.chat.server.handlers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.messages.Message;
import com.chat.messages.RequestMessage;
import com.server.database.DatabaseAccessor;

/**
 * Handles request messages.
 */
public class RequestMessageHandler extends AbstractMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestMessageHandler.class);
    private Map<Byte, AbstractMessageHandler> requestHandlers;

    public RequestMessageHandler(DatabaseAccessor databaseAccessor) {
        super(databaseAccessor);
        requestHandlers = new HashMap<>();
        requestHandlers.put(RequestMessage.REQUEST_GET_CONVERSATIONS, new HandleGetConversationsMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_GET_CONVERSATION, new HandleGetConversationMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_GET_FRIENDS, new HandleGetFriendsMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_SEARCH_USERS, new HandleSearchUsersMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_LOGOUT, new HandleLogoutMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_GET_FRIEND_REQUESTS, new HandleGetIncomingFriendRequestsMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_SEND_FRIEND_REQUEST, new HandleSendFriendRequestMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_ACCEPT_FRIEND_REQUEST, new HandleAcceptFriendRequestMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_DENY_FRIEND_REQUEST, new HandleDenyFriendRequestMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_HAS_FRIEND_REQUEST, new HandleCheckFriendRequestMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_FRIENDS_WITH, new HandleCheckFriendshipStatusMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_CANCEL_FRIEND_REQUEST, new HandleCancelFriendRequestMessage(databaseAccessor));
        requestHandlers.put(RequestMessage.REQUEST_REMOVE_FRIEND, new HandleRemoveFriendMessage(databaseAccessor));
    }

    @Override
    public void handleMessage(Message message, DataOutputStream output) throws IOException, SQLException {
        RequestMessage requestMessage = (RequestMessage) message;
        AbstractMessageHandler requestHandler = requestHandlers.get(requestMessage.getRequestType());
        if (requestHandler != null) {
            requestHandler.handleMessage(message, output);
        } else {
            logger.error("Unknown request type: {}", requestMessage.getRequestType());
            throw new UnsupportedOperationException("Unknown request type: " + requestMessage.getRequestType());
        }
    }
}