package certValidator.interfaces;

import java.io.IOException;
import java.util.List;

import certValidator.model.CertModel;

/**
 * Interface for reporting systems that generate documentation of scanned
 * certificates.
 */
public interface IReporter {
    /**
     * Generates a report based on the provided certificate data.
     *
     * @param data The list of scanned certificates.
     * @throws IOException If an error occurs during report generation.
     */
    void generate(List<CertModel> data) throws IOException;
}
