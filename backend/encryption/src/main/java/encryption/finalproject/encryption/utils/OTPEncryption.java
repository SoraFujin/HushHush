package encryption.finalproject.encryption.utils;

import java.util.Arrays;

public class OTPEncryption {

    // Method to adjust the shared key length to match the plaintext length
    private static byte[] adjustKeyLength(byte[] sharedKey, int targetLength) {
        byte[] adjustedKey = new byte[targetLength];

        if (sharedKey.length == targetLength) {
            return sharedKey; // No adjustment needed
        } else if (sharedKey.length < targetLength) {
            // Expand key: Repeat the shared key to fill the target length
            for (int i = 0; i < targetLength; i++) {
                adjustedKey[i] = sharedKey[i % sharedKey.length];
            }
        } else {
            // Reduce key: Truncate the shared key to fit the target length
            adjustedKey = Arrays.copyOf(sharedKey, targetLength);
        }

        return adjustedKey;
    }

    // Method to encrypt plaintext using the shared key
    public static byte[] encrypt(byte[] plaintext, byte[] sharedKey) {
        // Validate input
        if (plaintext == null || sharedKey == null) {
            throw new IllegalArgumentException("Plaintext and shared key must not be null.");
        }

        // Adjust the shared key length to match the plaintext length
        byte[] adjustedKey = adjustKeyLength(sharedKey, plaintext.length);

        // XOR plaintext with the adjusted key
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ adjustedKey[i]);
        }

        return ciphertext;
    }

    // Method to decrypt ciphertext using the shared key
    public static byte[] decrypt(byte[] ciphertext, byte[] sharedKey) {
        // Validate input
        if (ciphertext == null || sharedKey == null) {
            throw new IllegalArgumentException("Ciphertext and shared key must not be null.");
        }

        // Decryption is the same as encryption (XOR again)
        return encrypt(ciphertext, sharedKey);
    }

    // Example usage for testing (mock shared key for now)
    public static void main(String[] args) {
        try {
            // Mock shared key (replace with your friend's Diffie-Hellman implementation)
            byte[] sharedKey = "mockShamvhgvhgvhgvghvghvhgvhgvhgDH".getBytes();

            // Example plaintext
            String message = "Hello, secure world!";
            byte[] plaintext = message.getBytes();

            // Encrypt the plaintext
            byte[] ciphertext = encrypt(plaintext, sharedKey);
            System.out.println("Ciphertext (Base64): " + java.util.Base64.getEncoder().encodeToString(ciphertext));

            // Decrypt the ciphertext
            byte[] decryptedMessage = decrypt(ciphertext, sharedKey);
            System.out.println("Decrypted Message: " + new String(decryptedMessage));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
