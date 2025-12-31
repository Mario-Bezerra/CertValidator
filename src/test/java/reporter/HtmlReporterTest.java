package reporter;

import certValidator.model.CertModel;
import certValidator.reporter.HtmlReporter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static org.junit.jupiter.api.Assertions.*;

class HtmlReporterTest {
    @TempDir
    Path tempDir;

    @Test
    void testGenerateReport() throws Exception {
        Path reportPath = tempDir.resolve("report.html");
        Date future = Date.from(Instant.now().plus(100, ChronoUnit.DAYS));

        String longChecksum = "AABBCCDDEEFFAABBCCDDEEFFAABBCCDDEEFF1122334455";
        CertModel model = new CertModel("test.jks", "my-alias", "CN=Issuer", new Date(), future, longChecksum);

        HtmlReporter reporter = new HtmlReporter(reportPath.toString());
        reporter.generate(Collections.singletonList(model));

        assertTrue(Files.exists(reportPath));
        String content = Files.readString(reportPath);

        assertTrue(content.contains("<html"));
        assertTrue(content.contains("my-alias"));
        // Checksum is now truncated at 20 chars
        assertTrue(content.contains("AABBCCDDEEFFAABBCCDD..."));
        assertTrue(content.contains("CN=Issuer"));
    }
}