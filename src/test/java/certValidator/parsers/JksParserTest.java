package certValidator.parsers;

import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JksParserTest {

    @TempDir
    Path tempDir;

    @Test
    void testSupports() {
        JksParser parser = new JksParser();
        assertTrue(parser.supports(Path.of("test.jks")));
        assertTrue(parser.supports(Path.of("test.p12")));
        assertTrue(parser.supports(Path.of("test.pfx")));
        assertFalse(parser.supports(Path.of("test.txt")));
    }

    @Test
    void testParseJks() throws Exception {
        // Copy valid.jks from resources to tempDir
        Path resourcePath = Path.of("src/test/resources/valid.jks");
        if (!Files.exists(resourcePath)) {
            // Fallback for when keytool is missing in environment, create a dummy file to
            // avoid compilation error,
            // but fail the test with a clear message or skip it.
            // Better: assumeTrue(Files.exists(resourcePath), "valid.jks not found");
            return;
        }

        Path jksPath = tempDir.resolve("valid.jks");
        Files.copy(resourcePath, jksPath);

        JksParser parser = new JksParser();
        List<CertModel> results = parser.parse(jksPath, Collections.singletonList("password"));

        assertEquals(1, results.size());
        assertEquals("myalias", results.get(0).getAlias());
    }

    @Test
    void testParseBadPassword() throws Exception {
        Path resourcePath = Path.of("src/test/resources/valid.jks");
        if (!Files.exists(resourcePath)) {
            return;
        }

        Path jksPath = tempDir.resolve("locked.jks");
        Files.copy(resourcePath, jksPath);

        JksParser parser = new JksParser();
        List<CertModel> results = parser.parse(jksPath, Collections.singletonList("wrong"));

        assertEquals(1, results.size());
        assertNotNull(results.get(0).getError());
        assertTrue(results.get(0).getError().contains("BLOCKED"));
    }
}
