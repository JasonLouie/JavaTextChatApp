package chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.chat.Client;
import com.chat.Conversation;
import com.chat.UserProfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    @Test
    public void testConnect() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        client.connect();
    }

    @Test
    public void testLogin_ValidCredentials() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        String response = client.login("username", "password");
        assertEquals("success", response);
    }

    @Test
    public void testLogin_InvalidCredentials() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        String response = client.login("username", "wrongpassword");
        assertEquals("invalid_credentials", response);
    }

    @Test
    public void testLogin_NonExistentUser() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        String response = client.login("nonexistentuser", "password");
        assertEquals("user_not_found", response);
    }

    @Test
    public void testRegister_NewUser() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        String response = client.register("newusername", "nickname", "email", "password", new File("profile_picture.jpg"));
        assertEquals("success", response);
    }

    @Test
    public void testRegister_ExistingUser() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        String response = client.register("existingusername", "nickname", "email", "password", new File("profile_picture.jpg"));
        assertEquals("username_taken", response);
    }

    @Test
    public void testGetFriends_LoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        client.login("username", "password");
        assert client.getSession() != null;
        List<UserProfile> friends = client.getFriends();
        assertEquals(0, friends.size());
    }

    @Test
    public void testGetFriends_NotLoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        assertThrows(IOException.class, client::getFriends);
    }

    @Test
    public void testSearchUsers_LoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        client.login("username", "password");
        assert client.getSession() != null;
        List<UserProfile> results = client.searchUsers("query");
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchUsers_NotLoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        assertThrows(IOException.class, () -> client.searchUsers("query"));
    }

    @Test
    public void testGetConversations_LoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        client.login("username", "password");
        assert client.getSession() != null;
        List<Conversation> conversations = client.getConversations();
        assertEquals(0, conversations.size());
    }

    @Test
    public void testGetConversations_NotLoggedIn() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        assertThrows(IOException.class, client::getConversations);
    }
}