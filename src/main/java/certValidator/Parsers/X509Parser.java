package certValidator.Parsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.cert.*;
import java.util.*;
import certValidator.Interfaces.ICertificateParser;
import certValidator.Model.CertModel;
import certValidator.Utils.CertUtils;

public class X509Parser implements ICertificateParser {

    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".cer") || name.endsWith(".crt") || name.endsWith(".pem");
    }

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