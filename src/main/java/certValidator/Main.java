package certValidator;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import certValidator.vault.EnvSecretProvider;
import certValidator.vault.FileSecretProvider;
import certValidator.vault.PropertySecretProvider;

/**
 * Entry point for the CertValidator application when run as a standalone jar.
 */
public class Main {
    /** Logger instance for the Main class. */
    final static Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Main method that orchestrates the certificate scanning process.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        logger.info("Validation of certificates initiated.");

        AppConfig config = new AppConfig();

        List<ISecretProvider> providers = new java.util.ArrayList<>();
        String sources = config.getSecretSources();

        for (String source : sources.split(",")) {
            switch (source.trim().toLowerCase()) {
                case "file":
                    providers.add(new FileSecretProvider("passwords.txt", "secrets.dat", config.getMasterKey()));
                    break;
                case "env":
                    providers.add(new EnvSecretProvider(config.getPasswordsEnvVar()));
                    break;
                case "prop":
                case "property":
                    providers
                            .add(new PropertySecretProvider(config.getPropertiesFilePath(), config.getPropertiesKey()));
                    break;
            }
        }

        List<String> passwords = new java.util.ArrayList<>();
        for (ISecretProvider provider : providers) {
            provider.initialize();
            passwords.addAll(provider.getPasswords());
        }

        // Remove duplicates passwords
        passwords = passwords.stream().distinct().collect(Collectors.toList());

        if (passwords.isEmpty()) {
            logger.warn("No passwords loaded from " + sources + ". Protected Keystores might fail.");
        } else {
            logger.info("Loaded " + passwords.size() + " password(s) from " + providers.size() + " source(s).");
        }

        List<ICertificateParser> parsers = new java.util.ArrayList<>();
        parsers.add(new JksParser());
        parsers.add(new X509Parser());

        ScannerService scanner = new ScannerService(passwords, parsers);
        IReporter reporter = new HtmlReporter(config.getReportPath());
        INotifier notifier = new EmailNotifier(config);

        try {
            List<CertModel> certificates = scanner.scan(config.getScanPath());

            List<CertModel> riskyCerts = certificates.stream()
                    .filter(c -> !c.isValid() || c.getDaysRemaining() <= config.getWarningDays())
                    .collect(Collectors.toList());

            reporter.generate(certificates);
            logger.info("Report saved on : " + config.getReportPath());

            if (!riskyCerts.isEmpty()) {
                logger.warn(riskyCerts.size() + " critical certificates. Triggering notification...");
                notifier.sendAlert(riskyCerts);
            } else {
                logger.info("No notification sent (No risks)");
            }

        } catch (Exception e) {
            logger.error("FATAL ERROR : " + e.getMessage());
            e.printStackTrace();
        }
    }
}