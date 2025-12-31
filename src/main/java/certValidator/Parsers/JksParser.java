package certValidator.parsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

import certValidator.utils.CertUtils;
import certValidator.interfaces.ICertificateParser;
import certValidator.model.CertModel;

/**
 * Implementation of ICertificateParser for JKS and PKCS12 keystore formats.
 */
public class JksParser implements ICertificateParser {

    /**
     * @param path The path to the file.
     * @return true if the file extension is .jks, .p12, or .pfx.
     */
    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".jks") || name.endsWith(".p12") || name.endsWith(".pfx");
    }

    /**
     * Parses a keystore file and extract all X509 certificates.
     *
     * @param path      The path to the keystore file.
     * @param passwords A list of potential passwords to try opening the keystore.
     * @return A list of CertModel instances for each found certificate.
     */
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
            } catch (Exception e) {
                e.printStackTrace();
                // Wrong password, trying next
            }
        }

        if (!opened) {
            list.add(new CertModel(path.toString(), "BLOCKED - Passwords failed"));
        }
        return list;
    }
}