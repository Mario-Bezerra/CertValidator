package vault;

import certValidator.Vault.FileSecretProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FileSecretProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void testInitializeAndGetPasswords() throws IOException {
        Path rawPath = tempDir.resolve("passwords.txt");
        Path vaultPath = tempDir.resolve("secrets.dat");
        Files.writeString(rawPath, "senha1,changeit,admin");

        String masterKey = "master123";

        FileSecretProvider provider = new FileSecretProvider(
            rawPath.toString(),
            vaultPath.toString(),
            masterKey
        );

        provider.initialize();
        assertTrue(Files.exists(vaultPath), "O arquivo secrets.dat deveria ter sido criado");

        List<String> passwords = provider.getPasswords();
        
        assertEquals(3, passwords.size());
        assertTrue(passwords.contains("changeit"));
    }

    @Test
    void testHandlesMissingFileGracefully() {
        FileSecretProvider provider = new FileSecretProvider(
            "arquivo_inexistente.txt",
            "secrets.dat",
            "key"
        );
        assertDoesNotThrow(provider::initialize);
        
        assertTrue(provider.getPasswords().isEmpty());
    }

    @Test
    void testHandlesWrongPassword() throws IOException {
        Path rawPath = tempDir.resolve("p.txt");
        Path vaultPath = tempDir.resolve("s.dat");
        Files.writeString(rawPath, "abc");

        new FileSecretProvider(rawPath.toString(), vaultPath.toString(), "KeyA").initialize();

        FileSecretProvider wrongProvider = new FileSecretProvider(
            rawPath.toString(),
            vaultPath.toString(),
            "KeyB"
        );

        List<String> result = wrongProvider.getPasswords();
        assertTrue(result.isEmpty(), "Deve retornar vazio se a senha estiver errada");
    }
}