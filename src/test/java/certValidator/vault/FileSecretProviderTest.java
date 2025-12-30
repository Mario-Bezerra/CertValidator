package certValidator.vault;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSecretProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void testInitializeAndGetPasswords() throws Exception {
        Path raw = tempDir.resolve("passwords.txt");
        Path vault = tempDir.resolve("vault.dat");
        Files.writeString(raw, "pass1, pass2, pass3");

        FileSecretProvider provider = new FileSecretProvider(raw.toString(), vault.toString(), "masterKey");

        // Initialize (Create vault)
        provider.initialize();
        assertTrue(Files.exists(vault));

        // Read back
        List<String> passwords = provider.getPasswords();
        assertEquals(3, passwords.size());
        assertTrue(passwords.contains("pass1"));
        assertTrue(passwords.contains("pass3"));
    }

    @Test
    void testGetPasswordsMissingVault() {
        FileSecretProvider provider = new FileSecretProvider("dummy", "missing.dat", "key");
        List<String> passwords = provider.getPasswords();
        assertTrue(passwords.isEmpty());
    }

    @Test
    void testGetPasswordsWrongKey() throws Exception {
        Path raw = tempDir.resolve("passwords.txt");
        Path vault = tempDir.resolve("vault.dat");
        Files.writeString(raw, "secret");

        // Init with correct key
        new FileSecretProvider(raw.toString(), vault.toString(), "correct").initialize();

        // Try decrypt with wrong key
        FileSecretProvider wrong = new FileSecretProvider(raw.toString(), vault.toString(), "wrong");
        List<String> passwords = wrong.getPasswords();

        // Should return empty list on failure
        assertTrue(passwords.isEmpty());
    }
}
