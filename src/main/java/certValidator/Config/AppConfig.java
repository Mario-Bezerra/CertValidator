package certValidator.Config;

public class AppConfig {
    public String getScanPath() { return getEnv("SCAN_PATH", "./"); }
    public String getReportPath() { return getEnv("REPORT_PATH", ".cert_reporter.html"); }
    public String getMasterKey() { return getEnv("MASTER_KEY", ""); }
    public int getWarningDays() { return Integer.parseInt(getEnv("WARNING_DAYS", "30")); }

    // Email Configs
    public String getSmtpHost() { return getEnv("SMTP_HOST", ""); }
    public String getSmtpPort() { return getEnv("SMTP_PORT", "587"); }
    public String getEmailUser() { return getEnv("EMAIL_USER", ""); }
    public String getEmailPass() { return getEnv("EMAIL_PASS", ""); }
    public String getEmailTo() { return getEnv("EMAIL_TO", ""); }
    
    public boolean isEmailEnabled() {
        return !getSmtpHost().isEmpty() && !getEmailUser().isEmpty();
    }

    private String getEnv(String key, String def) {
        String val = System.getenv(key);
        return (val == null || val.isEmpty()) ? def : val;
    }
}
