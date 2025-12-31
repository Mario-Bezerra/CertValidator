package certValidator.utils;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;

import certValidator.model.CertModel;

/**
 * Utility class for certificate-related operations, such as metadata extraction
 * and hashing.
 */
public class CertUtils {

    /**
     * Default constructor for CertUtils.
     */
    public CertUtils() {
    }

    /**
     * Extracts relevant metadata from an X509Certificate and creates a CertModel.
     *
     * @param cert  The X509Certificate to process.
     * @param path  The path to the file containing the certificate.
     * @param alias The alias of the certificate in the keystore.
     * @return A CertModel containing the certificate's metadata.
     * @throws Exception If an error occurs during processing.
     */
    public static CertModel extractMetadata(X509Certificate cert, Path path, String alias) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(cert.getEncoded());
        StringBuilder hex = new StringBuilder();
        for (byte b : hash)
            hex.append(String.format("%02X", b));

        String issuer = cert.getIssuerX500Principal().getName();
        if (issuer.contains("CN="))
            issuer = issuer.split("CN=")[1].split(",")[0];

        return new CertModel(path.toString(), alias, issuer, cert.getNotBefore(), cert.getNotAfter(), hex.toString());
    }
}