package certValidator.mojo;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import certValidator.config.AppConfig;
import certValidator.interfaces.ISecretProvider;
import certValidator.vault.FileSecretProvider;

/**
 * Maven Mojo that only performs password encryption into a vault file.
 */
@Mojo(name = "encrypt")
public class EncryptMojo extends AbstractMojo {

    /**
     * Default constructor.
     */
    public EncryptMojo() {
    }

    /** Path to the text file containing raw passwords. */
    @Parameter(defaultValue = "passwords.txt", property = "passwordsFile")
    private String passwordsFile;

    /** Path to the file where the encrypted secrets will be saved. */
    @Parameter(defaultValue = "secrets.dat", property = "secretsFile")
    private String secretsFile;

    /**
     * Sets the passwords file path.
     * 
     * @param passwordsFile The path to the passwords file.
     */
    public void setPasswordsFile(String passwordsFile) {
        this.passwordsFile = passwordsFile;
    }

    /**
     * Sets the secrets file path.
     * 
     * @param secretsFile The path to the secrets file.
     */
    public void setSecretsFile(String secretsFile) {
        this.secretsFile = secretsFile;
    }

    /**
     * Executes the encryption process.
     *
     * @throws MojoExecutionException If an error occurs during encryption.
     */
    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Starting password encryption...");

        AppConfig config = new AppConfig();
        String masterKey = config.getMasterKey();

        if (masterKey == null || masterKey.isEmpty()) {
            throw new MojoExecutionException("MASTER_KEY is not defined. Cannot encrypt passwords.");
        }

        File raw = new File(passwordsFile);
        if (!raw.exists()) {
            throw new MojoExecutionException("Raw passwords file not found: " + passwordsFile);
        }

        try {
            ISecretProvider secretProvider = new FileSecretProvider(
                    passwordsFile,
                    secretsFile,
                    masterKey);

            secretProvider.initialize();
            getLog().info("Encryption completed successfully. Vault saved to: " + secretsFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Error during encryption process", e);
        }
    }
}
