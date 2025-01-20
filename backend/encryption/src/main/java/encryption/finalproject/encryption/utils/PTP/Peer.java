package encryption.finalproject.encryption.utils.PTP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import encryption.finalproject.encryption.utils.Encryption.OTPEncryption;

public class Peer {

    private static final int LISTENING_PORT = 7777;
    private static SecretKey sharedKey;

    public static void main(String[] args) throws Exception {
        try {
            startServer();
        } catch (Exception e) {
            System.out.println("Server is already running. Enter the server's IP address: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String serverIP = reader.readLine().trim();
            connectToServer(serverIP);
        }
    }

    private static void startServer() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(LISTENING_PORT)) {
            System.out.println("Server is listening on port " + LISTENING_PORT);
            Socket socket = serverSocket.accept();
            System.out.println("Peer connected: " + socket.getInetAddress());

            performDiffieHellman(socket);
            chat(socket);
        }
    }

    private static void connectToServer(String serverIP) throws Exception {
        try (Socket socket = new Socket(serverIP, LISTENING_PORT)) {
            System.out.println("Connected to server: " + serverIP);

            performDiffieHellman(socket);
            chat(socket);
        }
    }

    private static void performDiffieHellman(Socket socket) throws Exception {
        // Generate Diffie-Hellman key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Send public key to peer
        OutputStream os = socket.getOutputStream();
        os.write(keyPair.getPublic().getEncoded());
        os.flush();

        // Receive peer's public key
        byte[] peerPubKeyBytes = new byte[2048];
        int bytesRead = socket.getInputStream().read(peerPubKeyBytes);

        if (bytesRead <= 0) {
            throw new Exception("Failed to read peer's public key.");
        }

        // Convert peer's public key from bytes
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(peerPubKeyBytes);
        PublicKey peerPublicKey = keyFactory.generatePublic(x509Spec);

        // Perform the key agreement
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(peerPublicKey, true);

        byte[] sharedSecret = keyAgreement.generateSecret();
        if (sharedSecret == null || sharedSecret.length == 0) {
            throw new Exception("Shared secret generation failed.");
        }

        // Derive a shared AES key from the shared secret
        sharedKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");
        System.out.println("Shared Key: " + Base64.getEncoder().encodeToString(sharedKey.getEncoded()));
        System.out.println("Shared secret established.");
    }

    private static void chat(Socket socket) throws Exception {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("You can start chatting. Type 'exit' to quit.");
        while (true) {
            // Listen for incoming messages
            if (socketReader.ready()) {
                String encryptedMessage = socketReader.readLine();
                String decryptedMessage = decrypt(encryptedMessage);
                System.out.println("Peer: " + decryptedMessage);
            }

            // Send outgoing messages
            String message = consoleReader.readLine();
            if (message != null && message.equalsIgnoreCase("exit")) {
                System.out.println("Exiting chat...");
                break;
            }

            if (message != null && !message.trim().isEmpty()) {
                String encryptedMessage = encrypt(message);
                socketWriter.println(encryptedMessage);
            }
        }
    }

    public static String encrypt(String message) throws Exception {
        if (sharedKey == null) {
            throw new IllegalStateException("Shared key is not initialized. Perform key exchange first.");
        }
        byte[] plaintext = message.getBytes();
        byte[] ciphertext = OTPEncryption.encrypt(plaintext, sharedKey.getEncoded());
        return Base64.getEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String encryptedMessage) throws Exception {
        if (sharedKey == null) {
            throw new IllegalStateException("Shared key is not initialized. Perform key exchange first.");
        }
        try {
            byte[] ciphertext = Base64.getDecoder().decode(encryptedMessage);
            byte[] plaintext = OTPEncryption.decrypt(ciphertext, sharedKey.getEncoded());
            return new String(plaintext);
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid Base64 input", e);
        }
    }

    // Method to get the shared key
    public static String getSharedKey() {
        if (sharedKey != null) {
            return Base64.getEncoder().encodeToString(sharedKey.getEncoded());
        } else {
            return "Shared key not yet initialized.";
        }
    }
}
