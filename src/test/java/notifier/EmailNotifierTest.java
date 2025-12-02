package notifier;

import certValidator.Config.AppConfig;
import certValidator.Model.CertModel;
import certValidator.Notifier.EmailNotifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailNotifierTest {

    @Test
    void testSendAlertAttemptsToSend() {
        AppConfig mockConfig = Mockito.mock(AppConfig.class);
        when(mockConfig.getSmtpHost()).thenReturn("localhost");
        when(mockConfig.getSmtpPort()).thenReturn("2525");
        when(mockConfig.getEmailUser()).thenReturn("user@test.com");
        when(mockConfig.getEmailPass()).thenReturn("pass");
        when(mockConfig.getEmailTo()).thenReturn("admin@test.com");

        EmailNotifier notifier = new EmailNotifier(mockConfig);
        CertModel fakeCert = new CertModel("file.jks", "alias", "Issuer", null, "hash");

        assertDoesNotThrow(() -> notifier.sendAlert(Collections.singletonList(fakeCert)));
    }
}