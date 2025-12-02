package certValidator.Utils;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import certValidator.Model.CertModel;

public class CertUtils {
    public static CertModel extractMetadata(X509Certificate cert, Path path, String alias) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(cert.getEncoded());
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02X", b));

        String issuer = cert.getIssuerX500Principal().getName();
        if (issuer.contains("CN=")) issuer = issuer.split("CN=")[1].split(",")[0];

        return new CertModel(path.toString(), alias, issuer, cert.getNotAfter(), hex.toString());
    }
}