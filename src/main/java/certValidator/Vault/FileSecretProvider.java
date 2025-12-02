package certValidator.Vault;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;
import java.util.stream.Collectors;

import javax.crypto.*;
import javax.crypto.spec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import certValidator.Interfaces.ISecretProvider;

public class FileSecretProvider implements ISecretProvider {
    private static final Logger logger = LoggerFactory.getLogger(FileSecretProvider.class);
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;

    private final String rawPath;
    private final String vaultPath;
    private final String masterKey;

    public FileSecretProvider(String rawPath, String vaultPath, String masterKey) {
        this.rawPath = rawPath;
        this.vaultPath = vaultPath;
        this.masterKey = masterKey;
    }

    @Override
    public void initialize() {
        Path input = Paths.get(rawPath);
        if (!Files.exists(input)) return;

        if (masterKey == null || masterKey.isEmpty()) {
            logger.error("ERROR: Passwords file found but MASTER_KEY is empty.");
            return;
        }

        try {
            logger.info("Encrypting secrets...");
            String data = Files.readString(input, StandardCharsets.UTF_8).trim();
            
            //Remove BOM (Byte Order Mark)
            data = data.replace("\uFEFF", "").trim();
            
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            random.nextBytes(iv);

            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, masterKey, salt, iv);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            try (FileOutputStream fos = new FileOutputStream(vaultPath)) {
                fos.write(salt);
                fos.write(iv);
                fos.write(cipherText);
            }
            logger.info("Vault created/updated.");
        } catch (Exception e) {
            logger.error("Error initializing vault: " + e.getMessage());
        }
    }

    @Override
    public List<String> getPasswords() {
        Path path = Paths.get(vaultPath);
        if (!Files.exists(path) || masterKey == null || masterKey.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            byte[] content = Files.readAllBytes(path);
            byte[] salt = Arrays.copyOfRange(content, 0, SALT_LENGTH);
            byte[] iv = Arrays.copyOfRange(content, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(content, SALT_LENGTH + IV_LENGTH, content.length);

            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, masterKey, salt, iv);
            String plain = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
            
            return Arrays.stream(plain.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error unlocking vault: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private Cipher initCipher(int mode, String pass, byte[] salt, byte[] iv) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(mode, secretKey, gcmSpec);
        return cipher;
    }
}