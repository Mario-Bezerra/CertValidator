package certValidator.Interfaces;

import java.nio.file.Path;
import java.util.List;
import certValidator.Model.CertModel;

public interface ICertificateParser {
	
    boolean supports(Path path);
    
    List<CertModel> parse(Path path, List<String> passwords);
}
