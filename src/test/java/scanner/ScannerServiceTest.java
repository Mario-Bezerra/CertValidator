package scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import certValidator.Interfaces.ICertificateParser;
import certValidator.Model.CertModel;
import certValidator.Parsers.JksParser;
import certValidator.Parsers.X509Parser;
import certValidator.Scanner.ScannerService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScannerServiceTest {

    @TempDir
    Path tempDir;
    
    private List<ICertificateParser> getParsers() {
        return Arrays.asList(new JksParser(), new X509Parser());
    }

    @Test
    void testScannerCanOpenRealKeystore() throws Exception {
        String keystoreName = "test-valid.jks";
        String password = "password123";
        Path keystorePath = tempDir.resolve(keystoreName);

        createTestKeystore(keystorePath.toString(), password, "meu-alias");

        ScannerService scanner = new ScannerService(
                Collections.singletonList(password), 
                getParsers() 
            );
        List<CertModel> results = scanner.scan(tempDir.toString());

        assertFalse(results.isEmpty(), "O scanner deveria ter encontrado pelo menos 1 certificado");
        
        CertModel cert = results.get(0);
        assertTrue(cert.isValid());
        assertEquals("meu-alias", cert.getAlias());
        assertNull(cert.getError());
    }

    @Test
    void testScannerFailsOnWrongPassword() throws Exception {
        String keystoreName = "test-locked.jks";
        createTestKeystore(tempDir.resolve(keystoreName).toString(), "senhaDificil", "locked-alias");

        ScannerService scanner = new ScannerService(
                Collections.singletonList("senhaErrada"), 
                getParsers()
            );
        List<CertModel> results = scanner.scan(tempDir.toString());

        assertFalse(results.isEmpty());
        CertModel result = results.get(0);
        
        assertFalse(result.isValid());
        assertTrue(result.getError().contains("BLOCKED"));
    }
    
    @Test
    void testScannerHandlesCorruptFile() throws Exception {
        Path corruptPath = tempDir.resolve("corrupt.jks");
        // Cria um arquivo com conteúdo inválido
        Files.write(corruptPath, new byte[]{0, 1, 2, 3});

        ScannerService scanner = new ScannerService(
            Collections.singletonList("pass"),
            getParsers()
        );
        List<CertModel> results = scanner.scan(tempDir.toString());

        assertFalse(results.isEmpty(), "O scanner deve retornar um registro de erro para arquivos corrompidos");
        
        CertModel result = results.get(0);
        assertFalse(result.isValid(), "O resultado deve ser marcado como inválido");
        assertNotNull(result.getError(), "Deve haver uma mensagem de erro");
        assertTrue(result.getError().contains("BLOCKED"), "A mensagem deve indicar que o arquivo foi bloqueado/falhou");
    }

    @Test
    void testScannerProcessesCrtFiles() throws Exception {
        String keystoreName = "temp.jks";
        Path keystorePath = tempDir.resolve(keystoreName);
        createTestKeystore(keystorePath.toString(), "changeit", "export-alias");

        Path cerPath = tempDir.resolve("exported.cer");
        
        List<String> command = Arrays.asList(
            "keytool", "-export",
            "-alias", "export-alias",
            "-keystore", keystorePath.toString(),
            "-storepass", "changeit",
            "-file", cerPath.toString()
        );
        ProcessBuilder pb = new ProcessBuilder(command);
        
        assertEquals(0, pb.start().waitFor(), "Falha ao exportar certificado para teste");

        ScannerService scanner = new ScannerService(
            Collections.emptyList(),
            getParsers()
        );
        
        List<CertModel> results = scanner.scan(tempDir.toString());

        boolean foundCer = results.stream()
            .anyMatch(c -> c.getFilePath().endsWith("exported.cer") && c.getAlias().startsWith("cert-"));
            
        assertTrue(foundCer, "Deveria ter processado o arquivo .cer");
    }

    private void createTestKeystore(String path, String password, String alias) throws IOException, InterruptedException {
    	List<String> command = Arrays.asList(
                "keytool", "-genkeypair", "-alias", alias, "-keyalg", "RSA", "-keysize", "2048", 
                "-validity", "365", "-dname", "CN=Test Cert, O=Test Unit", 
                "-keystore", path, "-storepass", password, "-keypass", password
            );

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Falha ao gerar keystore de teste.");
        }
    }
}