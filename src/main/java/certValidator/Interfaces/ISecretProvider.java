package certValidator.interfaces;

import java.util.List;

/**
 * Interface for secret providers that supply passwords for keystores.
 */
public interface ISecretProvider {
    /**
     * Initializes the secret provider (e.g., loads secrets from disk or vault).
     */
    void initialize();

    /**
     * Returns the list of passwords provided by this provider.
     *
     * @return A list of password strings.
     */
    List<String> getPasswords();
}
