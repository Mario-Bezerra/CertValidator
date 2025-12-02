package certValidator;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.Config.AppConfig;
import certValidator.Interfaces.ICertificateParser;
import certValidator.Interfaces.INotifier;
import certValidator.Interfaces.IReporter;
import certValidator.Interfaces.ISecretProvider;
import certValidator.Model.CertModel;
import certValidator.Notifier.EmailNotifier;
import certValidator.Parsers.JksParser;
import certValidator.Parsers.X509Parser;
import certValidator.Reporter.HtmlReporter;
import certValidator.Scanner.ScannerService;
import certValidator.Vault.FileSecretProvider;

public class Main {
    final static Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("Validation of certificates initiated.");

        AppConfig config = new AppConfig();

        ISecretProvider secretProvider = new FileSecretProvider(
            "passwords.txt", 
            "secrets.dat", 
            config.getMasterKey()
        );
        
        secretProvider.initialize();
        List<String> passwords = secretProvider.getPasswords();

        if (passwords.isEmpty()) {
            logger.warn("No password loaded. Protected Keystores will fail.");
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