package certValidator.Interfaces;

import java.io.IOException;
import java.util.List;
import certValidator.Model.CertModel;

public interface IReporter {
    void generate(List<CertModel> data) throws IOException;
}
