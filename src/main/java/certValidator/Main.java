package certValidator;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.Config.AppConfig;
import certValidator.Model.CertModel;
import certValidator.Notifier.EmailNotifier;
import certValidator.Reporter.HtmlReporter;
import certValidator.Scanner.ScannerService;
import certValidator.Vault.CryptoVault;

public class Main {
	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args) {
    	logger.info("Validation of certificates initiated.");

        AppConfig config = new AppConfig();

        CryptoVault.initializeVault("passwords.txt", "secrets.dat", config.getMasterKey());

        List<String> keystorePasswords = CryptoVault.loadPasswords("secrets.dat", config.getMasterKey());
        if (keystorePasswords.isEmpty()) {
        	logger.warn("No password loaded. Protected Keystores will fail.");
        }

        try {
            ScannerService scanner = new ScannerService(keystorePasswords);
            List<CertModel> certificates = scanner.scan(config.getScanPath());

            List<CertModel> riskyCerts = certificates.stream()
                    .filter(c -> !c.isValid() || c.getDaysRemaining() <= config.getWarningDays())
                    .collect(Collectors.toList());

            HtmlReporter.generate(certificates, config.getReportPath());
            
            logger.info("Report saved on : " + config.getReportPath());

            if (!riskyCerts.isEmpty() && config.isEmailEnabled()) {
            	logger.warn(riskyCerts.size() + " critical certificates. Triggering e-mail...");
                EmailNotifier.sendAlert(config, riskyCerts);
            } else {
            	logger.info("No e-mail sended (No risks or missing configuration)");
            }

        } catch (Exception e) {
        	logger.error("FATAL ERROR : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
