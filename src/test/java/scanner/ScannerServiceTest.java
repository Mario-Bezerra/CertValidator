package scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import certValidator.Model.CertModel;
import certValidator.Scanner.ScannerService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScannerServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void testScannerCanOpenRealKeystore() throws Exception {
        String keystoreName = "test-valid.jks";
        String password = "password123";
        Path keystorePath = tempDir.resolve(keystoreName);

        createTestKeystore(keystorePath.toString(), password, "meu-alias");

        ScannerService scanner = new ScannerService(Collections.singletonList(password));
        List<CertModel> results = scanner.scan(tempDir.toString());

        assertFalse(results.isEmpty(), "O scanner deveria ter encontrado pelo menos 1 certificado");
        
        CertModel cert = results.get(0);
        assertTrue(cert.isValid());
        assertEquals("meu-alias", cert.getAlias());
        assertNull(cert.getError());
        System.out.println("Teste de Integração Sucesso: Leu " + cert.getFilePath());
    }

    @Test
    void testScannerFailsOnWrongPassword() throws Exception {
        String keystoreName = "test-locked.jks";
        createTestKeystore(tempDir.resolve(keystoreName).toString(), "senhaDificil", "locked-alias");

        ScannerService scanner = new ScannerService(Collections.singletonList("senhaErrada"));
        List<CertModel> results = scanner.scan(tempDir.toString());

        assertFalse(results.isEmpty());
        CertModel result = results.get(0);
        
        assertFalse(result.isValid());
        assertTrue(result.getError().contains("BLOCKED"), "Deveria conter mensagem de bloqueado");
    }

    private void createTestKeystore(String path, String password, String alias) throws IOException, InterruptedException {
        List<String> command = Arrays.asList(
            "keytool",
            "-genkeypair",
            "-alias", alias,
            "-keyalg", "RSA",
            "-keysize", "2048",
            "-validity", "365",
            "-dname", "CN=Test Cert, O=Test Unit",
            "-keystore", path,
            "-storepass", password,
            "-keypass", password
        );

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO(); 
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Falha ao gerar keystore de teste. O 'keytool' está no PATH?");
        }
    }
}
