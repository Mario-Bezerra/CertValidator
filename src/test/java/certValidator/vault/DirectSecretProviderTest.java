package certValidator.vault;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DirectSecretProviderTest {

    @Test
    void testGetPasswords() {
        List<String> passwords = Arrays.asList("pass1", "pass2");
        DirectSecretProvider provider = new DirectSecretProvider(passwords);
        provider.initialize(); // Should do nothing but good for coverage
        assertEquals(passwords, provider.getPasswords());
    }

    @Test
    void testGetPasswordsWithNull() {
        DirectSecretProvider provider = new DirectSecretProvider(null);
        assertNotNull(provider.getPasswords());
        assertTrue(provider.getPasswords().isEmpty());
    }
}
