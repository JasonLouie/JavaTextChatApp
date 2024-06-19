package chat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.chat.client.Client;
import com.chat.server.Server;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SuppressWarnings("unused")
public class ClientServerTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientServerTest.class);
    private static Server server;
    private static Client client;
    /*
    @BeforeAll
    public static void startServer() throws IOException, SQLException {
        server = new Server(8000);
        Thread serverThread = new Thread(server::start);
        serverThread.start();
    }

    @AfterAll
    public static void stopServer() {
        server.close();
    }

    @Test
    public void testLoginSuccess() throws IOException {
        logger.info("Testing login success...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        String response = client.login("username", "password");
        client.close();
        assertEquals("success", response);
    }

    @Test
    public void testLoginFailure() throws IOException {
        logger.info("Testing login failure...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        String response = client.login("invaliduser", "invalidpassword");
        client.close();
        assertEquals("Incorrect username or password", response);
    }

    @Test
    public void testRegisterSuccess() throws IOException {
        logger.info("Testing register success...");
        try {
            File profilePictureDir = new File("profile_pictures");
            if (!profilePictureDir.exists()) {
                profilePictureDir.mkdirs(); // Create directory if it doesn't exist
            }
            File profilePicture = new File(profilePictureDir, "profile_picture.jpg");
            if (!profilePicture.exists()) {
                profilePicture.createNewFile(); // Create file if it doesn't exist
            }
            client = new Client(new Socket("localhost", 8000));
            client.connect();
            String response = client.register("username", "nickname", "email", "password", profilePicture);
            assertEquals("success", response);
        } finally {
            client.close();
        }
    }
*/
    /*
    @Test
    public void testGetFriendsWithoutSession() throws IOException {
        logger.info("Testing get friends without session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        assertThrows(IOException.class, () -> client.getFriends());
    }

    @Test
    public void testGetConversationsWithoutSession() throws IOException {
        logger.info("Testing get conversations without session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        assertThrows(IOException.class, () -> client.getConversations());
    }

    @Test
    public void testGetConversationWithoutSession() throws IOException {
        logger.info("Testing get conversation without session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        assertThrows(IOException.class, () -> client.getConversation(1));
    }

    @Test
    public void testSearchUsersWithoutSession() throws IOException {
        logger.info("Testing search users without session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        assertThrows(IOException.class, () -> client.searchUsers("testquery"));
    }

    @Test
    public void testGetFriendsWithSession() throws IOException {
        logger.info("Testing get friends with session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        client.login("username", "password");
        client.getFriends();
    }

    @Test
    public void testGetConversationsWithSession() throws IOException {
        logger.info("Testing get conversations with session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        client.login("username", "password");
        client.getConversations();
    }

    @Test
    public void testGetConversationWithSession() throws IOException {
        logger.info("Testing get conversation with session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        client.login("username", "password");
        client.getConversation(1);
    }

    @Test
    public void testSearchUsersWithSession() throws IOException {
        logger.info("Testing search users with session...");
        client = new Client(new Socket("localhost", 8000));
        client.connect();
        client.login("username", "password");
        client.searchUsers("testquery");
    }
    */
}