package certValidator.vault;

import java.util.ArrayList;
import java.util.List;
import certValidator.interfaces.ISecretProvider;

/**
 * Secret provider that uses a direct list of passwords provided via
 * configuration.
 */
public class DirectSecretProvider implements ISecretProvider {

    private final List<String> directPasswords;

    /**
     * Constructs a new DirectSecretProvider.
     * 
     * @param directPasswords The list of passwords.
     */
    public DirectSecretProvider(List<String> directPasswords) {
        this.directPasswords = directPasswords != null ? directPasswords : new ArrayList<>();
    }

    /**
     * No initialization needed for direct passwords.
     */
    @Override
    public void initialize() {
        // No initialization needed for direct passwords
    }

    @Override
    public List<String> getPasswords() {
        return directPasswords;
    }
}
