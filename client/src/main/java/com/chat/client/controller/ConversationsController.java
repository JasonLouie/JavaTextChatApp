package com.chat.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import com.chat.client.Client;
import com.chat.models.Conversation;
import java.util.List;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationsController {
    @FXML
    private VBox conversationsBox;

    private Client client;
    private HomeController homeController;

    private static final Logger logger = LoggerFactory.getLogger(ConversationsController.class);

    public void setClient(Client client) {
        this.client = client;
        loadConversations();
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void loadConversations() {
        new Thread(() -> {
            try {
                List<Conversation> conversations = client.getConversations();
                Platform.runLater(() -> displayConversations(conversations));
            } catch (Exception e) {
                logger.error("Error loading conversations", e);
                Platform.runLater(() -> {
                    if (homeController != null) {
                        homeController.displayError("Error loading conversations: " + e.getMessage());
                    }
                });
            }
        }).start();
    }

    private void displayConversations(List<Conversation> conversations) {
        conversationsBox.getChildren().clear();
        if (conversations != null && !conversations.isEmpty()) {
            for (Conversation conversation : conversations) {
                Label conversationLabel = new Label(conversation.getSenderUsername());
                conversationsBox.getChildren().add(conversationLabel);
            }
        } else {
            Label noConversationsLabel = new Label("No conversations found.");
            conversationsBox.getChildren().add(noConversationsLabel);
        }
    }
}