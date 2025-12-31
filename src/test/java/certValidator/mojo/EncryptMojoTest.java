package certValidator.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EncryptMojoTest {

    @TempDir
    Path tempDir;

    @Test
    void testExecuteSuccess() throws Exception {
        System.setProperty("MASTER_KEY", "testkey");
        Path passwords = tempDir.resolve("passwords.txt");
        Files.writeString(passwords, "secret123");
        Path secrets = tempDir.resolve("secrets.dat");

        EncryptMojo mojo = new EncryptMojo();
        mojo.setPasswordsFile(passwords.toString());
        mojo.setSecretsFile(secrets.toString());

        Log mockLog = mock(Log.class);
        mojo.setLog(mockLog);

        mojo.execute();

        assertTrue(Files.exists(secrets));
        verify(mockLog, atLeastOnce()).info(anyString());

        System.clearProperty("MASTER_KEY");
    }

    @Test
    void testExecuteNoMasterKey() {
        System.clearProperty("MASTER_KEY");
        // We need to make sure env is also empty, which is hard.
        // But AppConfig prefers System Property.

        EncryptMojo mojo = new EncryptMojo();
        assertThrows(MojoExecutionException.class, mojo::execute);
    }

    @Test
    void testExecuteNoPasswordsFile() {
        System.setProperty("MASTER_KEY", "testkey");
        EncryptMojo mojo = new EncryptMojo();
        mojo.setPasswordsFile("non_existent.txt");

        assertThrows(MojoExecutionException.class, mojo::execute);
        System.clearProperty("MASTER_KEY");
    }
}
