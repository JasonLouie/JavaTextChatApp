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

@ExtendWith(MockitoExtension.class)
public class ServerTest {

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
            Socket socket = new Socket("localhost", 8000);
            Client client = new Client(socket);
            client.connect();
            String response = client.register("username", "nickname", "email", "password", new File("profile_picture.jpg"));
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
            List<Conversation> conversations = client.getConversations();
            assertTrue(conversations.size() > 0);
            client.close();
        } finally {
            server.close();
            serverThread.join();
        }
    }
}
