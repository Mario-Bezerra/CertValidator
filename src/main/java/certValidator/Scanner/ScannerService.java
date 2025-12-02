package certValidator.Scanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.Model.CertModel;

public class ScannerService {
	final static Logger logger = LoggerFactory.getLogger(ScannerService.class);
	
    private final List<String> passwords;

    public ScannerService(List<String> passwords) {
        this.passwords = passwords;
    }

    public List<CertModel> scan(String rootDir) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(rootDir))) {
            return paths.filter(Files::isRegularFile)
                .map(this::processFile)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        }
    }

    private List<CertModel> processFile(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        List<CertModel> results = new ArrayList<>();
        
        try {
            if (name.endsWith(".jks") || 
            	name.endsWith(".p12") || 
            	name.endsWith(".pfx")) {
                results.addAll(processKeyStore(path));
            } 
            
            if (name.endsWith(".cer") || 
            	name.endsWith(".crt") || 
            	name.endsWith(".pem")) {
                results.addAll(processX509(path));
            }
        } catch (Exception e) {
            // Ignore all files that are not certificates.
        	logger.error("Exception when reading files.");
        }
        return results;
    }

    private List<CertModel> processKeyStore(Path path) {
        List<CertModel> list = new ArrayList<>();
        boolean opened = false;
        
        for (String pass : passwords) {
            try (InputStream is = new FileInputStream(path.toFile())) {
                KeyStore ks = KeyStore.getInstance(path.toString().endsWith(".jks") ? "JKS" : "PKCS12");
                ks.load(is, pass.toCharArray());
                
                Enumeration<String> aliases = ks.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate c = ks.getCertificate(alias);
                    if (c instanceof X509Certificate) {
                        list.add(extract((X509Certificate) c, path, alias));
                    }
                }
                opened = true;
                break;
            } catch (Exception ignored) {}
        }
        
        if (!opened) list.add(new CertModel(path.toString(), "BLOCKED - Passwords failed"));
        return list;
    }

    private List<CertModel> processX509(Path path) throws Exception {
        List<CertModel> list = new ArrayList<>();
        try (InputStream is = new FileInputStream(path.toFile())) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certs = fact.generateCertificates(is);
            int i = 1;
            for (Certificate c : certs) {
                if (c instanceof X509Certificate) {
                    list.add(extract((X509Certificate) c, path, "cert-" + i++));
                }
            }
        }
        return list;
    }

    private CertModel extract(X509Certificate cert, Path path, String alias) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(cert.getEncoded());
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02X", b));

        String issuer = cert.getIssuerX500Principal().getName();
        if (issuer.contains("CN=")) issuer = issuer.split("CN=")[1].split(",")[0];

        return new CertModel(path.toString(), alias, issuer, cert.getNotAfter(), hex.toString());
    }
}
