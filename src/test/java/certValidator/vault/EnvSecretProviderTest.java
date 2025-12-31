package certValidator.vault;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EnvSecretProviderTest {

    @Test
    void testInitializeWithResult() {
        EnvSecretProvider provider = new EnvSecretProvider("MY_VAR", k -> " pass1, pass2 ,, pass3 ");
        provider.initialize();
        List<String> passwords = provider.getPasswords();
        assertEquals(3, passwords.size());
        assertEquals("pass1", passwords.get(0));
        assertEquals("pass2", passwords.get(1));
        assertEquals("pass3", passwords.get(2));
    }

    @Test
    void testInitializeWithNull() {
        EnvSecretProvider provider = new EnvSecretProvider("MY_VAR", k -> null);
        provider.initialize();
        assertTrue(provider.getPasswords().isEmpty());
    }

    @Test
    void testInitializeWithEmpty() {
        EnvSecretProvider provider = new EnvSecretProvider("MY_VAR", k -> "");
        provider.initialize();
        assertTrue(provider.getPasswords().isEmpty());
    }
}
