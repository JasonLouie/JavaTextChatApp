package chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.Client;
import com.chat.Conversation;
import com.chat.Server;
import com.chat.UserProfile;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ServerClientTest {

    @Test
    public void testLogin() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            String response = client.login("username", "password");
            assertEquals("success", response);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testRegister() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            File profilePictureDir = new File("profile_pictures");
            if (!profilePictureDir.exists()) {
                profilePictureDir.mkdirs(); // Create directory if it doesn't exist
            }
            File profilePicture = new File(profilePictureDir, "profile_picture.jpg");
            if (!profilePicture.exists()) {
                profilePicture.createNewFile(); // Create file if it doesn't exist
            }
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            String response = client.register("username", "nickname", "email", "password", profilePicture);
            assertEquals("success", response);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testSearchUsers() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            client.login("username", "password");
            List<UserProfile> results = client.searchUsers("query");
            assertTrue(results.size() > 0);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetFriends() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            client.login("username", "password");
            List<UserProfile> friends = client.getFriends();
            assertTrue(friends.size() > 0);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetConversations() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            client.login("username", "password");
            List<Conversation> conversations = client.getConversations();
            assertTrue(conversations.size() > 0);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testSearchUsersNotLoggedIn() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            assertThrows(IOException.class, () -> client.searchUsers("query"));
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetFriendsNotLoggedIn() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            assertThrows(IOException.class, client::getFriends);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetConversationsNotLoggedIn() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            assertThrows(IOException.class, client::getConversations);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }
}
