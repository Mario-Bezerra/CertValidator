package certValidator.Notifier;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import certValidator.Config.AppConfig;
import certValidator.Interfaces.INotifier;
import certValidator.Model.CertModel;

public class EmailNotifier implements INotifier {
    
	final static Logger logger = LoggerFactory.getLogger(EmailNotifier.class);
    private final AppConfig config;

    public EmailNotifier(AppConfig config) {
        this.config = config;
    }
	
    @Override
    public void sendAlert(List<CertModel> riskyCerts) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", config.getSmtpHost());
        props.put("mail.smtp.port", config.getSmtpPort());

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getEmailUser(), config.getEmailPass());
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getEmailUser()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(config.getEmailTo()));
            message.setSubject("WARN: Certificate expired or invalidated");

            StringBuilder body = new StringBuilder("Pay attention to the following certificates:\n\n");
            for (CertModel c : riskyCerts) {
                body.append(String.format("* [%s] %s (%d days) - %s\n", 
                    (c.isValid() ? "EXPIRE SOON" : "EXPIRED/ERROR"),
                    c.getAlias(), c.getDaysRemaining(), c.getFilePath()));
            }
            
            message.setText(body.toString());
            Transport.send(message);
        } catch (Exception e) {
        	logger.error("Failed sending e-mail" + e.getMessage());
        }
    }
}
