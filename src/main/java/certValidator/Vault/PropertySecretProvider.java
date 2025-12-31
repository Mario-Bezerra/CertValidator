package certValidator.vault;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import certValidator.interfaces.ISecretProvider;

/**
 * Secret provider that retrieves passwords from a .properties file.
 */
public class PropertySecretProvider implements ISecretProvider {

    private final String propertiesPath;
    private final String propertyKey;
    private List<String> passwords = new ArrayList<>();

    /**
     * Constructs a new PropertySecretProvider.
     * 
     * @param propertiesPath Path to the .properties file.
     * @param propertyKey    The key in the properties file containing
     *                       comma-separated passwords.
     */
    public PropertySecretProvider(String propertiesPath, String propertyKey) {
        this.propertiesPath = propertiesPath;
        this.propertyKey = propertyKey;
    }

    /**
     * Initializes the provider by reading the properties file.
     */
    @Override
    public void initialize() {
        if (propertiesPath == null || propertiesPath.isEmpty())
            return;

        Properties props = new Properties();
        try (InputStream is = new FileInputStream(propertiesPath)) {
            props.load(is);
            String value = props.getProperty(propertyKey);
            if (value != null && !value.isEmpty()) {
                this.passwords = Arrays.stream(value.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // Log warning or handle as needed
        }
    }

    @Override
    public List<String> getPasswords() {
        return passwords;
    }
}
