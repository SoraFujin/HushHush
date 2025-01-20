package encryption.finalproject.encryption.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import encryption.finalproject.encryption.utils.PTP.Peer;

@RestController
@RequestMapping("/api/chat")
public class PTPEncryptionController {

    @PostMapping("/start-server")
    public String startServer() {
        try {
            Peer.main(new String[]{"server"});
            return "Server started and waiting for a connection...";
        } catch (Exception e) {
            return "Error starting server: " + e.getMessage();
        }
    }

    @PostMapping("/connect")
    public String connectToServer(@RequestParam String serverIP) {
        try {
            Peer.main(new String[]{serverIP});
            return "Connected to server: " + serverIP;
        } catch (Exception e) {
            return "Error connecting to server: " + e.getMessage();
        }
    }

    @PostMapping("/send-message")
    public String sendMessage(@RequestBody Map<String, String> payload) {
        try {
            String message = payload.get("message");
            String encryptedMessage = Peer.encrypt(message);
            return encryptedMessage;
        } catch (Exception e) {
            return "Error encrypting message: " + e.getMessage();
        }
    }

    @PostMapping("/receive-message")
    public String receiveMessage(@RequestBody Map<String, String> payload) {
        try {
            String encryptedMessage = payload.get("encryptedMessage");
            String decryptedMessage = Peer.decrypt(encryptedMessage);
            return decryptedMessage;
        } catch (Exception e) {
            return "Error decrypting message: " + e.getMessage();
        }
    }
   @GetMapping("/get-shared-key")
    public ResponseEntity<String> getSharedKey() {
        // Fetching shared key from environment variable
        String sharedKey = Peer.getSharedKey();
        if (sharedKey == null || sharedKey.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Shared key not found or is empty.");
        }
        return ResponseEntity.ok(sharedKey);
    }

       // Decrypt a message
    @PostMapping("/decrypt-message")
    public ResponseEntity<String> decryptMessage(@RequestBody Map<String, String> payload) {
        try {
            String encryptedMessage = payload.get("encryptedMessage");
            String sharedKey = payload.get("sharedKey");

            if (encryptedMessage == null || encryptedMessage.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Encrypted message is missing or empty.");
            }

            if (sharedKey == null || sharedKey.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body("Shared key is missing or empty.");
            }

            String decryptedMessage = Peer.decrypt(encryptedMessage);
            return ResponseEntity.ok(decryptedMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error decrypting message: " + e.getMessage());
        }
    }
}