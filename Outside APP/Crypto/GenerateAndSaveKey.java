import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class GenerateAndSaveKey {
    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Initialize the key generator
            SecretKey secretKey = keyGen.generateKey(); // Generate the AES key
            
            // Encode the key as Base64 (to ensure it's safely written as text)
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            // Write the encoded key to a file
            try (FileOutputStream fos = new FileOutputStream("aesKey.txt")) {
                fos.write(encodedKey.getBytes());
                System.out.println("AES Key saved to aesKey.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
