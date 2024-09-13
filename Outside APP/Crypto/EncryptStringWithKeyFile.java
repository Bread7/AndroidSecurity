import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class EncryptStringWithKeyFile {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a string to encrypt as an argument.");
            System.exit(1);
        }
        
        String dataToEncrypt = args[0]; // First argument as the string to encrypt
        
        try {
            // Read the encoded key from file
            byte[] keyBytes = Files.readAllBytes(Paths.get("aesKey.txt"));
            String encodedKey = new String(keyBytes);
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            
            // Rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            
            // Encrypt data
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] encryptedBytes = cipher.doFinal(dataToEncrypt.getBytes());
            
            // Output the encrypted data in Base64
            System.out.println(Base64.getEncoder().encodeToString(encryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
