package Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.util.Base64;

public class FileController {
    private final Logger logger = LoggerFactory.getLogger(FileController.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String STATIC_KEY = "1234567890123456"; // 16-byte key for AES-128 for testing

    String projectDir = System.getProperty("user.dir");
    String resourcesPath = projectDir + "/src/main/resources/api_key.bin";

    // Generate a static key (Replace this with secure key retrieval)
    private SecretKey getKey() {
        return new SecretKeySpec(STATIC_KEY.getBytes(), ALGORITHM);
    }

    // Encrypt the API key
    private String encrypt(String plainText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypt the API key
    private String decrypt(String encryptedText, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // Read the encrypted API key from the config file
    public String readApiKey() {
        try (FileInputStream fis = new FileInputStream(resourcesPath)) {
            byte[] data = fis.readAllBytes();
            String encryptedKey = new String(data);
            SecretKey key = getKey();
            logger.info("API Key read successfully.");
            return decrypt(encryptedKey, key);
        } catch (Exception ex) {
            logger.error("Error reading Config File.", ex);
            return null;
        }
    }

    // Write the encrypted API key to the config file
    public void writeApiKey(String apiKey) {
        try (FileOutputStream fos = new FileOutputStream(resourcesPath)) {
            SecretKey key = getKey();
            String encryptedKey = encrypt(apiKey, key);
            fos.write(encryptedKey.getBytes());
            logger.info("API Key written successfully.");
            JOptionPane.showMessageDialog(null, "API Key written successfully.");
        } catch (Exception ex) {
            logger.error("Error writing Config File.", ex);
        }
    }
}
