package certValidator.config;

/**
 * Configuration class that provides application settings, often sourced from
 * environment variables.
 */
public class AppConfig {
    /**
     * Returns the configured scan path.
     * 
     * @return The configured scan path or a default value.
     */
    public String getScanPath() {
        return getEnv("SCAN_PATH", "./");
    }

    /**
     * Returns the configured report path.
     * 
     * @return The configured report path or a default value.
     */
    public String getReportPath() {
        return getEnv("REPORT_PATH", ".cert_reporter.html");
    }

    /**
     * Returns the master key.
     * 
     * @return The master key used for decryption.
     */
    public String getMasterKey() {
        return getEnv("MASTER_KEY", "");
    }

    /**
     * Returns the warning threshold in days.
     * 
     * @return The number of days before expiration to trigger a warning.
     */
    public int getWarningDays() {
        return Integer.parseInt(getEnv("WARNING_DAYS", "30"));
    }

    /**
     * Returns the comma-separated list of secret sources.
     * 
     * @return Comma-separated list (e.g., "file,env,prop"). Default is "file".
     */
    public String getSecretSources() {
        return getEnv("SECRET_SOURCES", "file");
    }

    /**
     * Returns the name of the environment variable for passwords.
     * 
     * @return The environment variable name.
     */
    public String getPasswordsEnvVar() {
        return getEnv("PASSWORDS_ENV", "CERT_PASSWORDS");
    }

    /**
     * Returns the path to the properties file.
     * 
     * @return The properties file path.
     */
    public String getPropertiesFilePath() {
        return getEnv("PROPERTIES_FILE", "");
    }

    /**
     * Returns the key in the properties file for passwords.
     * 
     * @return The properties key.
     */
    public String getPropertiesKey() {
        return getEnv("PROPERTIES_KEY", "cert.passwords");
    }

    // Email Configs
    /**
     * Returns the SMTP host.
     * 
     * @return The SMTP host for sending alerts.
     */
    public String getSmtpHost() {
        return getEnv("SMTP_HOST", "");
    }

    /**
     * Returns the SMTP port.
     * 
     * @return The SMTP port.
     */
    public String getSmtpPort() {
        return getEnv("SMTP_PORT", "587");
    }

    /**
     * Returns the SMTP user.
     * 
     * @return The SMTP user.
     */
    public String getEmailUser() {
        return getEnv("EMAIL_USER", "");
    }

    /**
     * Returns the SMTP password.
     * 
     * @return The SMTP password.
     */
    public String getEmailPass() {
        return getEnv("EMAIL_PASS", "");
    }

    /**
     * Returns the destination email address.
     * 
     * @return The destination email for alerts.
     */
    public String getEmailTo() {
        return getEnv("EMAIL_TO", "");
    }

    /**
     * Checks if email notifications are enabled.
     * 
     * @return true if email notifications are configured and enabled.
     */
    public boolean isEmailEnabled() {
        return !getSmtpHost().isEmpty() && !getEmailUser().isEmpty();
    }

    private String getEnv(String key, String def) {
        String val = System.getProperty(key);
        if (val == null || val.isEmpty()) {
            val = System.getenv(key);
        }
        return (val == null || val.isEmpty()) ? def : val;
    }
}
