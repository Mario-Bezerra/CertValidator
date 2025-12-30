package certValidator.interfaces;

import java.nio.file.Path;
import java.util.List;

import certValidator.model.CertModel;

/**
 * Interface for certificate parsers that can extract metadata from different
 * file formats.
 */
public interface ICertificateParser {

    /**
     * Checks if the parser supports the given file path.
     *
     * @param path The path to the file to check.
     * @return true if the parser supports the file, false otherwise.
     */
    boolean supports(Path path);

    /**
     * Parses the certificate file and extracts metadata.
     *
     * @param path      The path to the certificate file.
     * @param passwords A list of potential passwords for password-protected
     *                  formats.
     * @return A list of CertModel instances containing certificate metadata.
     */
    List<CertModel> parse(Path path, List<String> passwords);
}
