package certValidator.parsers;

import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class X509ParserTest {

    @TempDir
    Path tempDir;

    @Test
    void testSupports() {
        X509Parser parser = new X509Parser();
        assertTrue(parser.supports(Path.of("test.cer")));
        assertTrue(parser.supports(Path.of("test.crt")));
        assertTrue(parser.supports(Path.of("test.pem")));
        assertFalse(parser.supports(Path.of("test.jks")));
    }

    @Test
    void testParseEmptyOrBad() throws Exception {
        Path certPath = tempDir.resolve("empty.cer");
        Files.writeString(certPath, "Navigate");

        X509Parser parser = new X509Parser();
        List<CertModel> list = parser.parse(certPath, Collections.emptyList());
        // Should handle gracefully
        assertTrue(list.isEmpty());
    }
}
