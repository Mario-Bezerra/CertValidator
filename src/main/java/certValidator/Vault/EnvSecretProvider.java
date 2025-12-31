package certValidator.vault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import certValidator.interfaces.ISecretProvider;

/**
 * secret provider that retrieves passwords from an environment variable.
 */
public class EnvSecretProvider implements ISecretProvider {

    private final String envVarName;
    private final java.util.function.Function<String, String> envResolver;
    private List<String> passwords = new ArrayList<>();

    /**
     * Constructs a new EnvSecretProvider.
     * 
     * @param envVarName The name of the environment variable to read from.
     */
    public EnvSecretProvider(String envVarName) {
        this(envVarName, System::getenv);
    }

    /**
     * Package-private constructor for testing.
     */
    EnvSecretProvider(String envVarName, java.util.function.Function<String, String> envResolver) {
        this.envVarName = envVarName;
        this.envResolver = envResolver;
    }

    /**
     * Initializes the provider by reading the environment variable.
     */
    @Override
    public void initialize() {
        String value = envResolver.apply(envVarName);
        if (value != null && !value.isEmpty()) {
            this.passwords = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getPasswords() {
        return passwords;
    }
}
