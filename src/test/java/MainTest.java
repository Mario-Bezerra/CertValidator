import certValidator.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class MainTest {

    Path passFile = Paths.get("passwords.txt");
    Path secretFile = Paths.get("secrets.dat");
    Path reportFile = Paths.get(".cert_reporter.html");

    @BeforeEach
    void setup() throws IOException {
        Files.deleteIfExists(passFile);
        Files.deleteIfExists(secretFile);
        Files.deleteIfExists(reportFile);
        
        Files.writeString(passFile, "changeit");
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(passFile);
        Files.deleteIfExists(secretFile);
        Files.deleteIfExists(reportFile);
    }

    @Test
    void testMainExecution() {
        Main.main(new String[]{});
    }
}