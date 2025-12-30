package certValidator.model;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.Date;

/**
 * Model class representing certificate metadata.
 */
public class CertModel {
    /** File path where the certificate was found. */
    private String filePath;
    /** Alias of the certificate in the keystore. */
    private String alias;
    /** Issuer of the certificate. */
    private String issuer;
    /** SHA-256 checksum of the certificate. */
    private String checksum;
    /** Error message if parsing failed. */
    private String error;
    /** Expiration date of the certificate. */
    private Date notAfter;
    /** Issuance date of the certificate. */
    private Date notBefore;

    /**
     * Constructs a CertModel with a full set of metadata.
     *
     * @param filePath  The path to the certificate file.
     * @param alias     The certificate alias.
     * @param issuer    The certificate issuer.
     * @param notBefore The issuance date.
     * @param notAfter  The expiration date.
     * @param checksum  The certificate checksum.
     */
    public CertModel(String filePath, String alias, String issuer, Date notBefore, Date notAfter, String checksum) {
        this.filePath = filePath;
        this.alias = alias;
        this.issuer = issuer;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.checksum = checksum;
    }

    /**
     * Constructs a CertModel representing a parsing error.
     *
     * @param filePath The path to the certificate file.
     * @param error    The error message.
     */
    public CertModel(String filePath, String error) {
        this.filePath = filePath;
        this.alias = "ERRO";
        this.error = error;
    }

    /**
     * Calculates the number of days remaining until the certificate expires.
     *
     * @return The number of days remaining, or -999 if the expiration date is
     *         unknown.
     */
    public long getDaysRemaining() {
        if (notAfter == null)
            return -999;
        return ChronoUnit.DAYS.between(Instant.now(), notAfter.toInstant());
    }

    /**
     * Checks if the certificate is considered valid (no parsing error and not
     * expired).
     *
     * @return true if valid, false otherwise.
     */
    public boolean isValid() {
        return error == null && getDaysRemaining() >= 0;
    }

    /**
     * Gets the file path.
     * 
     * @return The file path of the certificate.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Gets the certificate alias.
     * 
     * @return The certificate alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Gets the certificate issuer.
     * 
     * @return The certificate issuer.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Gets the expiration date.
     * 
     * @return The expiration date.
     */
    public Date getNotAfter() {
        return notAfter;
    }

    /**
     * Gets the issuance date.
     * 
     * @return The issuance date.
     */
    public Date getNotBefore() {
        return notBefore;
    }

    /**
     * Gets the checksum.
     * 
     * @return The certificate checksum.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Gets the error message.
     * 
     * @return The error message, if any.
     */
    public String getError() {
        return error;
    }
}
