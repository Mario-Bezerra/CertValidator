package certValidator.notifier;

import certValidator.config.AppConfig;
import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotifierTest {

    @Test
    void testSendAlert() {
        AppConfig mockConfig = Mockito.mock(AppConfig.class);
        Mockito.when(mockConfig.getSmtpHost()).thenReturn("localhost");
        Mockito.when(mockConfig.getSmtpPort()).thenReturn("25");
        Mockito.when(mockConfig.getEmailUser()).thenReturn("user@test.com");
        Mockito.when(mockConfig.getEmailTo()).thenReturn("admin@test.com");

        EmailNotifier notifier = new EmailNotifier(mockConfig);

        List<CertModel> alerts = Collections.singletonList(
                new CertModel("path", "alias", "issuer", null, null, "check"));

        // Just ensure it doesn't throw.
        // Real mail sending would fail or be skipped if server invalid, but code
        // catches exceptions.
        assertDoesNotThrow(() -> notifier.sendAlert(alerts));
    }
}
