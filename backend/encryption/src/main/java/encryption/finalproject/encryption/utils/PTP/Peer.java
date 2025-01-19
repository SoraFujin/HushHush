package encryption.finalproject.encryption.utils.PTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Peer {
    private static final int LISTENING_PORT = 7777;
    private static SecretKey sharedKey;
    private static BigInteger p;
    private static BigInteger g;
    private static BigInteger privateKey;
    private static BigInteger publicKey;
    private static final DiffieHellman dh = new DiffieHellman();

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Are you a server (y/n)?");
        String role = reader.readLine().trim().toLowerCase();

        if (role.equals("y")) {
            startServer();
        } else {
            System.out.print("Enter the server's IP address: ");
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

        // Send public key to the other peer
        OutputStream os = socket.getOutputStream();
        os.write(keyPair.getPublic().getEncoded());
        os.flush();

        // Receive the other peer's public key
        InputStream is = socket.getInputStream();
        byte[] peerPubKeyBytes = new byte[2048];
        int bytesRead = is.read(peerPubKeyBytes);

        // Generate the shared secret
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(peerPubKeyBytes);
        PublicKey peerPublicKey = keyFactory.generatePublic(x509Spec);

        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(peerPublicKey, true);

        // Derive the shared secret
        byte[] sharedSecret = keyAgreement.generateSecret();
        sharedKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");

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

    private static String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sharedKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sharedKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }
}
