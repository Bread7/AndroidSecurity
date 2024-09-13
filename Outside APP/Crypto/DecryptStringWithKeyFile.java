import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class DecryptStringWithKeyFile {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a Base64-encoded string to decrypt as an argument.");
            System.exit(1);
        }
        
        String base64EncryptedData = args[0]; // First argument as the string to decrypt
        
        try {
            // Read the encoded key from file
            byte[] keyBytes = Files.readAllBytes(Paths.get("aesKey.txt"));
            String encodedKey = new String(keyBytes);
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
            
            // Rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            
            // Decrypt data
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64EncryptedData));
            
            // Output the decrypted data
            System.out.println(new String(decryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
