package certValidator.reporter;

import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HtmlReporterTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateReport() throws java.io.IOException {
        Path report = tempDir.resolve("report.html");
        HtmlReporter reporter = new HtmlReporter(report.toString());

        java.util.Date future = new java.util.Date(System.currentTimeMillis() + (100L * 24 * 60 * 60 * 1000));
        List<CertModel> models = Collections.singletonList(
                new CertModel("test/path", "alias", "issuer", new java.util.Date(), future, "checksum"));
        reporter.generate(models);

        assertTrue(Files.exists(report));
        try {
            String content = Files.readString(report);
            assertTrue(content.contains("test/path"));
            assertTrue(content.contains("VÁLIDO"));
        } catch (Exception e) {
            fail("Should be readable");
        }
    }
}
