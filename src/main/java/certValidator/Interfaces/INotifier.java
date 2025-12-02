package certValidator.Interfaces;

import java.util.List;
import certValidator.Model.CertModel;

public interface INotifier {
    void sendAlert(List<CertModel> riskyCerts);
}
