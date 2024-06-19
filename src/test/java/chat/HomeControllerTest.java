package chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.client.Client;
import com.chat.controller.HomeController;
import com.chat.models.UserProfile;
import com.chat.messages.LoginRegisterSuccessMessage;
import com.chat.messages.NoResultsMessage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {
    /*
    private HomeController controller;

    @Mock
    private Client client;

    @Mock
    private Label usernameLabel;

    @Mock
    private Label bioLabel;

    @Mock
    private Label statusLabel;

    @Mock
    private Label friendsCountLabel;

    @Mock
    private Button logoutButton;

    @Mock
    private TextField searchField;

    @Mock
    private VBox searchResultsBox;

    @Mock
    private VBox friendsBox;

    @BeforeEach
    public void setup() throws IOException {
        controller = new HomeController();
        controller.setClient(client);
        controller.setUsernameLabel(usernameLabel);
        controller.setBioLabel(bioLabel);
        controller.setStatusLabel(statusLabel);
        controller.setFriendsCountLabel(friendsCountLabel);
        controller.setLogoutButton(logoutButton);
        controller.setSearchField(searchField);
        controller.setSearchResultsBox(searchResultsBox);
        controller.setFriendsBox(friendsBox);

        // Simulate a successful login
        when(client.login("username", "password")).thenReturn("success");
        LoginRegisterSuccessMessage successMessage = new LoginRegisterSuccessMessage(new UserProfile(1, "username", "nickname", "bio", "status", new File("profilePicture.jpg")));
        when(client.getSession().getUserProfile()).thenReturn(successMessage.getProfile());
    }

    @Test
    public void testDisplayCurrentUserProfile() {
        controller.displayCurrentUserProfile();
        verify(usernameLabel).setText("username (nickname)");
        verify(bioLabel).setText("bio");
        verify(statusLabel).setText("status");
    }

    @Test
    public void testHandleSearch() throws IOException {
        when(searchField.getText()).thenReturn("query");
        controller.handleSearch();
        verify(client).searchUsers("query");
    }

    @Test
    public void testHandleLogout() throws IOException {
        controller.handleLogout();
        verify(client).logout();
    }

    @Test
    public void testDisplayUserResults() {
        List<UserProfile> results = Arrays.asList(new UserProfile(1, "user1", "User 1", "bio1", "status1", new File("profilePicture1.jpg")), new UserProfile(2, "user2", "User 2", "bio2", "status2", new File("profilePicture2.jpg")));
        controller.displayUserResults(results, searchResultsBox);
        assertEquals(2, searchResultsBox.getChildren().size());
    }

    @Test
    public void testSendFriendRequest() throws IOException {
        UserProfile user = new UserProfile(1, "user", "User", "bio", "status", new File("profilePicture.jpg"));
        controller.handleSendFriendRequest(user);
        verify(client).sendFriendRequest(user.getUserId());
    }

    @Test
    public void testGetFriendsWithFriends() throws IOException {
        List<UserProfile> friends = Arrays.asList(new UserProfile(1, "friend1", "Friend 1", "bio1", "status1", new File("profilePicture1.jpg")), new UserProfile(2, "friend2", "Friend 2", "bio2", "status2", new File("profilePicture2.jpg")));
        when(client.getFriends()).thenReturn(friends);
        controller.handleFriends();
        assertEquals(2, friendsBox.getChildren().size());
    }

    @Test
    public void testGetFriendsWithoutFriends() throws IOException {
        NoResultsMessage noResultsMessage = new NoResultsMessage("No friends found");
        when(client.getFriends()).thenReturn(new ArrayList<>());
        controller.handleFriends();
        assertEquals(1, friendsBox.getChildren().size());
        assertTrue(friendsBox.getChildren().get(0) instanceof Label);
        assertEquals("No friends", ((Label) friendsBox.getChildren().get(0)).getText());
    }

    @Test
    public void testLogoutButton() throws IOException {
        controller.handleLogout();
        verify(client).logout();
        // Verify that the login screen is displayed
    }

    @Test
    public void testSearchButton() throws IOException {
        when(searchField.getText()).thenReturn("query");
        controller.handleSearch();
        verify(client).searchUsers("query");
        // Verify that the search results are displayed
    }
    */
}