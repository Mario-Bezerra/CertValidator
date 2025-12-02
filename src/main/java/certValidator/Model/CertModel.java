package certValidator.Model;

import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.util.Date;

public class CertModel {
    private String filePath, alias, issuer, checksum, error;
    private Date notAfter;

    public CertModel(String filePath, String alias, String issuer, Date notAfter, String checksum) {
        this.filePath = filePath;
        this.alias = alias;
        this.issuer = issuer;
        this.notAfter = notAfter;
        this.checksum = checksum;
    }

    public CertModel(String filePath, String error) {
        this.filePath = filePath;
        this.alias = "ERRO";
        this.error = error;
    }

    public long getDaysRemaining() {
        if (notAfter == null) return -999;
        return ChronoUnit.DAYS.between(Instant.now(), notAfter.toInstant());
    }

    public boolean isValid() {
        return error == null && getDaysRemaining() >= 0;
    }

    public String getFilePath() { return filePath; }
    public String getAlias() { return alias; }
    public String getIssuer() { return issuer; }
    public Date getNotAfter() { return notAfter; }
    public String getChecksum() { return checksum; }
    public String getError() { return error; }
}
