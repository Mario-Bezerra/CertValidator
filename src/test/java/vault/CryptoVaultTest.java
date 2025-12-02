package vault;

import certValidator.Vault.CryptoVault;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CryptoVaultTest {

    @TempDir
    Path tempDir;

    @Test
    void testVaultRoundTrip() throws IOException {
        Path passwordsFile = tempDir.resolve("passwords.txt");
        Path secretsFile = tempDir.resolve("secrets.dat");
        Files.writeString(passwordsFile, "senha1,senha2,changeit");

        String masterKey = "mySecretKey123";

        CryptoVault.initializeVault(passwordsFile.toString(), secretsFile.toString(), masterKey);

        assertTrue(Files.exists(secretsFile));

        List<String> loadedPasswords = CryptoVault.loadPasswords(secretsFile.toString(), masterKey);

        assertEquals( 3, loadedPasswords.size());
        assertTrue(loadedPasswords.contains("changeit"));
        assertTrue(loadedPasswords.contains("senha1"));
    }

    @Test
    void testLoadWithWrongKey() throws IOException {
        Path passwordsFile = tempDir.resolve("passwords.txt");
        Path secretsFile = tempDir.resolve("secrets.dat");
        Files.writeString(passwordsFile, "senha1");
        
        CryptoVault.initializeVault(passwordsFile.toString(), secretsFile.toString(), "keyA");
        
        List<String> result = CryptoVault.loadPasswords(secretsFile.toString(), "keyB");
        assertTrue(result.isEmpty(), "Deve retornar lista vazia se a senha mestra estiver errada");
    }
}