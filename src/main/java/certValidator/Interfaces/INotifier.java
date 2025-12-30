package certValidator.interfaces;

import java.util.List;

import certValidator.model.CertModel;

/**
 * Interface for notification systems that alert users about risky certificates.
 */
public interface INotifier {
    /**
     * Sends an alert for a list of certificates identified as risky or expiring
     * soon.
     *
     * @param riskyCerts The list of certificates to notify about.
     */
    void sendAlert(List<CertModel> riskyCerts);
}
