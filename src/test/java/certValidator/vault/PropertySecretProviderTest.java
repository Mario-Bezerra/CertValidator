package certValidator.vault;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class PropertySecretProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void testInitializeWithValidFile() throws Exception {
        Path propFile = tempDir.resolve("test.properties");
        Properties props = new Properties();
        props.setProperty("mypass", "val1, val2");
        try (FileOutputStream out = new FileOutputStream(propFile.toFile())) {
            props.store(out, null);
        }

        PropertySecretProvider provider = new PropertySecretProvider(propFile.toString(), "mypass");
        provider.initialize();
        List<String> passwords = provider.getPasswords();
        assertEquals(2, passwords.size());
        assertTrue(passwords.contains("val1"));
        assertTrue(passwords.contains("val2"));
    }

    @Test
    void testInitializeWithInvalidFile() {
        PropertySecretProvider provider = new PropertySecretProvider("non_existent.properties", "key");
        provider.initialize();
        assertTrue(provider.getPasswords().isEmpty());
    }

    @Test
    void testInitializeWithEmptyPath() {
        PropertySecretProvider provider = new PropertySecretProvider("", "key");
        provider.initialize();
        assertTrue(provider.getPasswords().isEmpty());
    }

    @Test
    void testInitializeWithMissingKey() throws Exception {
        Path propFile = tempDir.resolve("test2.properties");
        Properties props = new Properties();
        props.setProperty("otherkey", "val1");
        try (FileOutputStream out = new FileOutputStream(propFile.toFile())) {
            props.store(out, null);
        }

        PropertySecretProvider provider = new PropertySecretProvider(propFile.toString(), "mypass");
        provider.initialize();
        assertTrue(provider.getPasswords().isEmpty());
    }
}
