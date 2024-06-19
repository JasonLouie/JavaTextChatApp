package chat;

import com.chat.client.Client;
import com.chat.messages.*;
import com.chat.models.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class ClientTest {
    /*
    private Client client;

    @Mock
    private DataInputStream input;

    @Mock
    private DataOutputStream output;

    @BeforeEach
    public void setup() {
        client = new Client(input, output);
    }

    @Test
    public void testLoginSuccess() throws IOException {
        // Mock the server response
        UserProfile userProfile = new UserProfile(1, "username", "nickname", "bio", "status", new File("profilePicture.jpg"));
        LoginRegisterSuccessMessage response = new LoginRegisterSuccessMessage(userProfile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        String result = client.login("username", "password");
        assertEquals("success", result);
    }

    @Test
    public void testLoginFailure() throws IOException {
        // Mock the server response
        ErrorMessage response = new ErrorMessage("Incorrect username or password");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        String result = client.login("username", "password");
        assertEquals("failed", result);
    }

    @Test
    public void testRegisterSuccess() throws IOException {
        // Mock the server response
        UserProfile userProfile = new UserProfile(1, "username", "nickname", "bio", "status", new File("profilePicture.jpg"));
        LoginRegisterSuccessMessage response = new LoginRegisterSuccessMessage(userProfile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        String result = client.register("username", "nickname", "email", "password", new File("profilePicture.jpg"));
        assertEquals("success", result);
    }

    @Test
    public void testRegisterUserAlreadyExists() throws IOException {
        // Mock the server response
        ErrorMessage response = new ErrorMessage("User already exists");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        String result = client.register("username", "nickname", "email", "password", new File("profilePicture.jpg"));
        assertEquals("user already exists", result);
    }

    @Test
    public void testSearchUsersWithResults() throws IOException {
        // Mock the server response
        List<UserProfile> users = Arrays.asList(
                new UserProfile(11, "user1", "User 1", "bio1", "status1", new File("profilePicture1.jpg")),
                new UserProfile(12, "user2", "User 2", "bio2", "status2", new File("profilePicture2.jpg"))
        );
        SearchUsersMessage response = new SearchUsersMessage(users);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        List<UserProfile> results = client.searchUsers("query");
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchUsersNoResults() throws IOException {
        // Mock the server response
        NoResultsMessage response = new NoResultsMessage("No users found");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        response.writeTo(dos);
        byte[] responseBytes = bos.toByteArray();

        when(input.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buffer = invocation.getArgument(0);
            System.arraycopy(responseBytes, 0, buffer, 0, responseBytes.length);
            return responseBytes.length;
        });

        List<UserProfile> results = client.searchUsers("query");
        assertEquals(0, results.size());
    }
     */
}
