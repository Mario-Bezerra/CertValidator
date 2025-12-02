package certValidator.Vault;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoVault {
    private static final String TRANSFORMER_CIPHER_NAME 		  = "AES/GCM/NoPadding";
	private static final String ALGORITHM_NAME_SECRET_KEY_SPEC 	  = "AES";
	private static final String ALGORITHM_NAME_SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA256";
	private static final String COMMA_STRING_VALUE 				  = ",";
	
	private static final int KEY_LENGTH 	 = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int GCM_TAG_LENGTH  = 128;
    private static final int SALT_LENGTH 	 = 16;
    private static final int IV_LENGTH 		 = 12;
    
    final static Logger logger = LoggerFactory.getLogger(CryptoVault.class);

    public static void initializeVault(String rawPath, String vaultPath, String masterKey) {
        Path input = Paths.get(rawPath);
        if (!Files.exists(input)) return;

        if (masterKey.isEmpty()) {
        	logger.error("ERROR : 'passwords.txt' founded but MASTER_KEY is not defined.");
            return;
        }

        try {
        	logger.info("Cripto " + rawPath + " to " + vaultPath + "...");
            String data = Files.readString(input, StandardCharsets.UTF_8).trim();
            
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(salt);
            new SecureRandom().nextBytes(iv);

            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, masterKey, salt, iv);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            try (FileOutputStream fos = new FileOutputStream(vaultPath)) {
                fos.write(salt);
                fos.write(iv);
                fos.write(cipherText);
            }
            
            // Optional : Delete file after ingestion.
            // Files.delete(input); 
            logger.info("Vault created.");

        } catch (Exception e) {
        	logger.error("ERROR creating vault : " + e.getMessage());
        }
    }

    public static List<String> loadPasswords(String vaultPath, String masterKey) {
        Path path = Paths.get(vaultPath);
        if (!Files.exists(path) || masterKey.isEmpty()) return new ArrayList<>();

        try {
            byte[] content = Files.readAllBytes(path);
            byte[] salt = Arrays.copyOfRange(content, 0, SALT_LENGTH);
            byte[] iv = Arrays.copyOfRange(content, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(content, SALT_LENGTH + IV_LENGTH, content.length);

            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, masterKey, salt, iv);
            String plain = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
            
            return Arrays.stream(plain.split(COMMA_STRING_VALUE))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
        	logger.error("ERROR OPENING vault (wrong MasterKey?) : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static Cipher initCipher(int mode, String pass, byte[] salt, byte[] iv) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM_NAME_SECRET_KEY_FACTORY);
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM_NAME_SECRET_KEY_SPEC);

        Cipher cipher = Cipher.getInstance(TRANSFORMER_CIPHER_NAME);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(mode, secretKey, gcmSpec);
        return cipher;
    }
}
