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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ServerClientTest {
    private static final Logger logger = LoggerFactory.getLogger(ServerClientTest.class);

    @Test
    public void testLogin() throws IOException, SQLException, InterruptedException {
        logger.info("Testing login");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);

        Thread serverThread = new Thread(server::start);
        serverThread.start();

        try {
            client.connect();
            logger.info("Client connected");
            String response = client.login("username", "password");
            assertEquals("success", response);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testRegister() throws IOException, SQLException, InterruptedException {
        logger.info("Testing registration");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);

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
            client.connect();
            String response = client.register("username", "nickname", "email", "password", profilePicture);
            assertEquals("user_already_exists", response);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }
/*
    @Test
    public void testSearchUsers() throws IOException, SQLException, InterruptedException {
        logger.info("Searching for users using a valid client session");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        socket.setSoTimeout(10000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            logger.info("Connected to the server");
            String response = client.login("username", "password");
            logger.info("Logged into the server");
            if (response.equals("success")) {
                logger.info("Testing search...");
                List<UserProfile> results = client.searchUsers("EmeraldJason");
                assertTrue(results.isEmpty());
            } else {
                logger.error("Something went wrong!");
            }
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetFriends() throws IOException, SQLException, InterruptedException {
        logger.info("Testing getFriends with a valid client session");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            client.login("username", "password");
            List<UserProfile> friends = client.getFriends();
            assertTrue(friends.size() > 0);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetConversations() throws IOException, SQLException, InterruptedException {
        logger.info("Test5");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            client.login("username", "password");
            List<Conversation> conversations = client.getConversations();
            assertTrue(conversations.size() > 0);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testSearchUsersNotLoggedIn() throws IOException, SQLException, InterruptedException {
        logger.info("Searching for users without a valid client session");
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            assertThrows(IOException.class, () -> client.searchUsers("query"));
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetFriendsNotLoggedIn() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            assertThrows(IOException.class, client::getFriends);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }

    @Test
    public void testGetConversationsNotLoggedIn() throws IOException, SQLException, InterruptedException {
        Server server = new Server(8000);
        Socket socket = new Socket("localhost", 8000);
        Client client = new Client(socket);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
        try {
            client.connect();
            assertThrows(IOException.class, client::getConversations);
        } finally {
            client.close();
            server.close();
            serverThread.join();
        }
    }
    */
}
