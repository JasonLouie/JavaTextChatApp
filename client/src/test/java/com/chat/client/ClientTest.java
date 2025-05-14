package chat.client;

import com.chat.client.Client;
import com.chat.client.Session;
import com.chat.models.User;
import com.chat.models.UserProfile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {

    private Client client;

    @BeforeEach
    public void setup() {
        client = new Client();
    }

    @AfterEach
    public void tearDown() {
        client = null;
    }

    /**
     * Tests the getUserProfile method.
     */
    @Test
    public void testGetUserProfile() {
        UserProfile userProfile = new UserProfile(1, "username", "nickname", "bio", "status", null);
        Session session = new Session(new User(1, "username", "nickname", "email"), userProfile);
        client.setSession(session);
        assertEquals(userProfile, client.getUserProfile());
    }

    /**
     * Tests the getUserId method.
     */
    @Test
    public void testGetUserId() {
        UserProfile userProfile = new UserProfile(1, "username", "nickname", "bio", "status", null);
        Session session = new Session(new User(1, "username", "nickname", "email"), userProfile);
        client.setSession(session);
        assertEquals(1, client.getUserId());
    }

    /**
     * Tests the isLoggedIn method when the user is logged in.
     */
    @Test
    public void testIsLoggedIn_True() {
        Session session = new Session(new User(1, "username", "nickname", "email"), new UserProfile(1, "username", "nickname", "bio", "status", null));
        client.setSession(session);
        assertTrue(client.isLoggedIn());
    }

    /**
     * Tests the isLoggedIn method when the user is not logged in.
     */
    @Test
    public void testIsLoggedIn_False() {
        client.setSession(null);
        assertFalse(client.isLoggedIn());
    }

    /**
     * Tests the setSession method.
     */
    @Test
    public void testSetSession() {
        Session session = new Session(new User(1, "username", "nickname", "email"), new UserProfile(1, "username", "nickname", "bio", "status", null));
        client.setSession(session);
        assertEquals(session, client.getSession());
    }

    /**
     * Tests the endSession method.
     */
    @Test
    public void testEndSession() {
        Session session = new Session(new User(1, "username", "nickname", "email"), new UserProfile(1, "username", "nickname", "bio", "status", null));
        client.setSession(session);
        client.endSession();
        assertNull(client.getSession());
    }
}