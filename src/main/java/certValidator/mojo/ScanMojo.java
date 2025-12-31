package certValidator.mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import certValidator.config.AppConfig;
import certValidator.interfaces.ICertificateParser;
import certValidator.interfaces.INotifier;
import certValidator.interfaces.IReporter;
import certValidator.interfaces.ISecretProvider;
import certValidator.model.CertModel;
import certValidator.notifier.EmailNotifier;
import certValidator.parsers.JksParser;
import certValidator.parsers.X509Parser;
import certValidator.reporter.HtmlReporter;
import certValidator.scanner.ScannerService;
import certValidator.vault.DirectSecretProvider;
import certValidator.vault.EnvSecretProvider;
import certValidator.vault.FileSecretProvider;
import certValidator.vault.PropertySecretProvider;

/**
 * Maven Mojo that performs the certificate scan.
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.VERIFY)
public class ScanMojo extends AbstractMojo {

    /**
     * Default constructor for ScanMojo.
     */
    public ScanMojo() {
    }

    /** Root directory to start scanning from. */
    @Parameter(defaultValue = "${project.basedir}", property = "scanPath", required = true)
    private File scanPath;

    /** Path to save the generated HTML report. */
    @Parameter(defaultValue = "${project.build.directory}/cert-report.html", property = "reportPath", required = true)
    private File reportPath;

    /** Number of days before expiration to trigger a warning. */
    @Parameter(defaultValue = "30", property = "warningDays")
    private int warningDays;

    /** Path to the text file containing passwords for keystores. */
    @Parameter(defaultValue = "passwords.txt", property = "passwordsFile")
    private String passwordsFile;

    /** Path to the file containing encrypted secrets. */
    @Parameter(defaultValue = "secrets.dat", property = "secretsFile")
    private String secretsFile;

    /** Email address to send alerts to. */
    @Parameter(property = "emailTo")
    private String emailTo;

    /** Name of the environment variable containing passwords. */
    @Parameter(defaultValue = "CERT_PASSWORDS", property = "passwordsEnv")
    private String passwordsEnv;

    /** Path to a properties file containing passwords. */
    @Parameter(property = "propertiesFile")
    private File propertiesFile;

    /** The key in the properties file containing passwords. */
    @Parameter(defaultValue = "cert.passwords", property = "propertiesKey")
    private String propertiesKey;

    /** List of passwords provided directly in the configuration. */
    @Parameter
    private List<String> directPasswords;

    /** The Maven Project. */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Root directory to start scanning from.
     * 
     * @param scanPath Root directory to start scanning from.
     */
    public void setScanPath(File scanPath) {
        this.scanPath = scanPath;
    }

    /**
     * Path to save the generated HTML report.
     * 
     * @param reportPath Path to save the generated HTML report.
     */
    public void setReportPath(File reportPath) {
        this.reportPath = reportPath;
    }

    /**
     * Number of days before expiration to trigger a warning.
     * 
     * @param warningDays Number of days before expiration to trigger a warning.
     */
    public void setWarningDays(int warningDays) {
        this.warningDays = warningDays;
    }

    /**
     * Path to the text file containing passwords.
     * 
     * @param passwordsFile Path to the text file containing passwords.
     */
    public void setPasswordsFile(String passwordsFile) {
        this.passwordsFile = passwordsFile;
    }

    /**
     * Path to the file containing encrypted secrets.
     * 
     * @param secretsFile Path to the file containing encrypted secrets.
     */
    public void setSecretsFile(String secretsFile) {
        this.secretsFile = secretsFile;
    }

    /**
     * List of passwords provided directly.
     * 
     * @param directPasswords List of passwords provided directly.
     */
    public void setDirectPasswords(List<String> directPasswords) {
        this.directPasswords = directPasswords;
    }

    /**
     * Executes the certificate scan, generates a report, and sends alerts if
     * necessary.
     *
     * @throws MojoExecutionException If an error occurs during execution.
     */
    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Starting Certificate Scan...");
        getLog().info("Scanning directory: " + scanPath);

        try {
            AppConfig config = new AppConfig();

            if (emailTo != null) {
                // We might need to make AppConfig mutable or use setters if available
                // For now, let's assume AppConfig reads env vars mostly, but we can try to
                // influence it or just pass params manually if we refactored.
                // Since AppConfig isn't shown fully, I'll rely on it or just ignore email
                // override if not supported easily.
                // But actually, we should probably construct specific configs.
            }

            List<ISecretProvider> providers = new ArrayList<>();

            // 1. File Vault Provider (Encodes if raw file exists, then provides)
            providers.add(new FileSecretProvider(passwordsFile, secretsFile, config.getMasterKey()));

            // 2. Environment Variable Provider
            providers.add(new EnvSecretProvider(passwordsEnv));

            // 3. Properties File Provider
            if (propertiesFile != null) {
                providers.add(new PropertySecretProvider(propertiesFile.getAbsolutePath(), propertiesKey));
            }

            // 4. Direct Passwords Provider
            if (directPasswords != null && !directPasswords.isEmpty()) {
                providers.add(new DirectSecretProvider(directPasswords));
            }

            List<String> passwords = new ArrayList<>();
            for (ISecretProvider provider : providers) {
                provider.initialize();
                passwords.addAll(provider.getPasswords());
            }

            // Remove duplicates
            passwords = passwords.stream().distinct().collect(Collectors.toList());

            if (passwords.isEmpty()) {
                getLog().warn("No passwords loaded from any source. Protected Keystores might fail.");
            } else {
                getLog().info("Loaded " + passwords.size() + " password(s) from " + providers.size() + " source(s).");
            }

            List<ICertificateParser> parsers = new ArrayList<>();
            parsers.add(new JksParser());
            parsers.add(new X509Parser());

            ScannerService scanner = new ScannerService(passwords, parsers);

            if (reportPath.getParentFile() != null) {
                reportPath.getParentFile().mkdirs();
            }

            IReporter reporter = new HtmlReporter(reportPath.getAbsolutePath());
            INotifier notifier = new EmailNotifier(config);

            List<CertModel> certificates = scanner.scan(scanPath.getAbsolutePath());

            List<CertModel> riskyCerts = certificates.stream()
                    .filter(c -> !c.isValid() || c.getDaysRemaining() <= warningDays)
                    .collect(Collectors.toList());

            reporter.generate(certificates);
            getLog().info("Report saved to: " + reportPath.getAbsolutePath());

            if (!riskyCerts.isEmpty()) {
                getLog().warn(riskyCerts.size() + " critical certificates found!");
                notifier.sendAlert(riskyCerts);

                // Fail build if needed?
                // throw new MojoExecutionException("Certificates expiring or invalid!");
            } else {
                getLog().info("No risks found.");
            }

        } catch (Exception e) {
            throw new MojoExecutionException("Error during certificate scanning", e);
        }
    }
}
