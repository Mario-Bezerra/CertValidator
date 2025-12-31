package certValidator.parsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.cert.*;
import java.util.*;

import certValidator.utils.CertUtils;
import certValidator.model.CertModel;
import certValidator.interfaces.ICertificateParser;

/**
 * Implementation of ICertificateParser for standard X.509 certificate files
 * (.cer, .crt, .pem).
 */
public class X509Parser implements ICertificateParser {

    /**
     * Default constructor for X509Parser.
     */
    public X509Parser() {
    }

    /**
     * @param path The path to the file.
     * @return true if the file extension is .cer, .crt, or .pem.
     */
    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".cer") || name.endsWith(".crt") || name.endsWith(".pem");
    }

    /**
     * Parses standard certificate files and extracts metadata.
     *
     * @param path      The path to the certificate file.
     * @param passwords Unused for this parser.
     * @return A list of CertModel instances for each found certificate.
     */
    @Override
    public List<CertModel> parse(Path path, List<String> passwords) {
        List<CertModel> list = new ArrayList<>();
        try (InputStream is = new FileInputStream(path.toFile())) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certs = fact.generateCertificates(is);
            int i = 1;
            for (Certificate c : certs) {
                if (c instanceof X509Certificate) {
                    list.add(CertUtils.extractMetadata((X509Certificate) c, path, "cert-" + i++));
                }
            }
        } catch (Exception e) {
            // Log error if needed or return partial
        }
        return list;
    }
}