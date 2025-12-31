package certValidator.mojo;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScanMojoTest {

    @TempDir
    Path tempDir;

    @Test
    void testExecuteBasic() throws Exception {
        Path scanDir = tempDir.resolve("scan");
        Files.createDirectories(scanDir);

        Path report = tempDir.resolve("report.html");

        ScanMojo mojo = new ScanMojo();
        mojo.setScanPath(scanDir.toFile());
        mojo.setReportPath(report.toFile());
        mojo.setPasswordsFile("non_existent.txt");
        mojo.setSecretsFile("non_existent.dat");
        mojo.setWarningDays(30);

        // Maven doesn't inject defaults in unit tests
        java.lang.reflect.Field field = ScanMojo.class.getDeclaredField("passwordsEnv");
        field.setAccessible(true);
        field.set(mojo, "CERT_PASSWORDS");

        Log mockLog = mock(Log.class);
        mojo.setLog(mockLog);

        mojo.execute();

        assertTrue(Files.exists(report));
        verify(mockLog, atLeastOnce()).info(anyString());
    }

    @Test
    void testExecuteWithDirectPasswords() throws Exception {
        Path scanDir = tempDir.resolve("scan_direct");
        Files.createDirectories(scanDir);
        Path report = tempDir.resolve("report_direct.html");

        ScanMojo mojo = new ScanMojo();
        mojo.setScanPath(scanDir.toFile());
        mojo.setReportPath(report.toFile());
        mojo.setDirectPasswords(Collections.singletonList("direct_pass"));
        mojo.setPasswordsFile("non_existent.txt");
        mojo.setSecretsFile("non_existent.dat");

        java.lang.reflect.Field field = ScanMojo.class.getDeclaredField("passwordsEnv");
        field.setAccessible(true);
        field.set(mojo, "CERT_PASSWORDS");

        Log mockLog = mock(Log.class);
        mojo.setLog(mockLog);

        mojo.execute();

        assertTrue(Files.exists(report));
        verify(mockLog).info(contains("Loaded 1 password(s)"));
    }
}
