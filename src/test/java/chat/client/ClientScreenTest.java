package chat.client;

import com.chat.client.Client;
import com.chat.controller.LoginController;
import com.chat.controller.RegisterController;
import com.chat.controller.HomeController;
import com.chat.controller.UserProfileController;
import com.chat.models.UserProfile;

import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.service.query.EmptyNodeQueryException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;

@ExtendWith(ApplicationExtension.class)
public class ClientScreenTest {

    private Client client;
    private Stage stage;

    @Start
    public void start(Stage stage) {
        this.stage = stage;
        client = new Client();
    }

    
}
