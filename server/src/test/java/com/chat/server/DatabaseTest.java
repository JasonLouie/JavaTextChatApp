package chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chat.models.Conversation;
import com.chat.models.UserProfile;
import com.server.database.DatabaseAccessor;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class DatabaseTest {
    /*
    @Test
    public void testCheckUserExists() throws SQLException {
        DatabaseAccessor database = new DatabaseAccessor();
        boolean exists = database.checkUserExists("username", "email");
        assertTrue(exists);
    }
    
    @Test
    public void testRegisterUser() throws SQLException {
        Database database = new Database();
        database.registerUser("username", "nickname", "email", "password", "profile_picture_path");
        assertTrue(database.checkUserExists("username", "email"));
    }

    @Test
    public void testVerifyUserPassword() throws SQLException {
        Database database = new Database();
        database.registerUser("username", "nickname", "email", "password", "profile_picture_path");
        boolean valid = database.verifyUserPassword("username", "password");
        assertTrue(valid);
    }

    @Test
    public void testGetUserProfile() throws SQLException {
        Database database = new Database();
        database.registerUser("username", "nickname", "email", "password", "profile_picture_path");
        UserProfile profile = database.getUserProfile("username");
        assertNotNull(profile);
    }

    @Test
    public void testGetFriends() throws SQLException {
        Database database = new Database();
        database.registerUser("username", "nickname", "email", "password", "profile_picture_path");
        List<UserProfile> friends = database.getFriends(1);
        assertEquals(0, friends.size());
    }

    @Test
    public void testGetConversations() throws SQLException {
        Database database = new Database();
        database.registerUser("username", "nickname", "email", "password", "profile_picture_path");
        List<Conversation> conversations = database.getConversations(1);
        assertEquals(0, conversations.size());
    }
    */
}
