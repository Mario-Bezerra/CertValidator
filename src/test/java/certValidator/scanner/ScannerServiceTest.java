package certValidator.scanner;

import certValidator.interfaces.ICertificateParser;
import certValidator.model.CertModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ScannerServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testScan() throws Exception {
        // Setup
        Path certFile = tempDir.resolve("test.cer");
        Files.createFile(certFile);

        ICertificateParser mockParser = mock(ICertificateParser.class);
        // Fix: Use generic any() or specific class matcher to avoid ambiguity
        when(mockParser.supports(ArgumentMatchers.<Path>any())).thenReturn(true);
        when(mockParser.parse(ArgumentMatchers.<Path>any(), anyList())).thenReturn(
                Collections.singletonList(new CertModel("path", "OK")));

        List<String> passwords = Collections.emptyList();

        // Fix: ScannerService(List<String> passwords, List<ICertificateParser> parsers)
        ScannerService scanner = new ScannerService(
                passwords,
                Collections.singletonList(mockParser));

        // Act
        // Fix: scan(String rootDir)
        List<CertModel> results = scanner.scan(tempDir.toString());

        // Assert
        assertEquals(1, results.size());
        verify(mockParser).parse(ArgumentMatchers.<Path>any(), anyList());
    }
}
