package certValidator.Parsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import certValidator.Interfaces.ICertificateParser;
import certValidator.Model.CertModel;
import certValidator.Utils.CertUtils;

public class JksParser implements ICertificateParser {

    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".jks") || name.endsWith(".p12") || name.endsWith(".pfx");
    }

    @Override
    public List<CertModel> parse(Path path, List<String> passwords) {
        List<CertModel> list = new ArrayList<>();
        boolean opened = false;

        for (String pass : passwords) {
            try (InputStream is = new FileInputStream(path.toFile())) {
                String type = path.toString().toLowerCase().endsWith(".jks") ? "JKS" : "PKCS12";
                KeyStore ks = KeyStore.getInstance(type);
                ks.load(is, pass.toCharArray());

                Enumeration<String> aliases = ks.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Certificate c = ks.getCertificate(alias);
                    if (c instanceof X509Certificate) {
                        list.add(CertUtils.extractMetadata((X509Certificate) c, path, alias));
                    }
                }
                opened = true;
                break;
            } catch (Exception ignored) {
                // Wrong password, trying next
            }
        }

        if (!opened) {
            list.add(new CertModel(path.toString(), "BLOCKED - Passwords failed"));
        }
        return list;
    }
}