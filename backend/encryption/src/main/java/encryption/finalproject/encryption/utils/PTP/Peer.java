package encryption.finalproject.encryption.utils.PTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Peer {

    private static final int PORT = 7777;

    public static void main(String[] args) {
        Thread serverThread = new Thread(() -> startServer(PORT));
        serverThread.start();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter peer IP tp connect (or type 'exit' to quit):");
            String peerIP = scanner.nextLine();
            
            while(!peerIP.equalsIgnoreCase("exit")) {
                try {
                    Socket socket = new Socket(peerIP, PORT);
                    communicate(socket);
                } catch (IOException e) {
                    System.out.println("Unable to connect to peer: " + e.getMessage());
                }
                System.out.println("Enter another peer IP (or 'exit'): ");
                peerIP = scanner.nextLine();
            }
        }
        System.out.println("Peer shutting down");
    }

    private static void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port " + port + "...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Peer connected: " + clientSocket.getInetAddress());
                communicate(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    private static void communicate(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); Scanner scanner = new Scanner(System.in)) {
            Thread sender = new Thread(() -> {
                while (!socket.isClosed()) {
                    System.out.print("You: ");
                    String message = scanner.nextLine();
                    out.println(message);
                    if (message.equalsIgnoreCase("exit")) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.out.println("Error closing socket: " + e.getMessage());
                        }
                        break;
                    }
                }
            });

            // Receiving thread
            Thread receiver = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Peer: " + message);
                        if (message.equalsIgnoreCase("exit")) {
                            socket.close();
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });

            sender.start();
            receiver.start();

            sender.join();
            receiver.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Communication error: " + e.getMessage());
        }
    }
}
