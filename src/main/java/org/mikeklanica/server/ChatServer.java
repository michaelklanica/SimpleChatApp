package org.mikeklanica.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args){
        System.out.println("Chat Server is running...");
        ServerSocket serverSocket = null;

        try{
            //Create the ServerSocket and bind it to port 12345
            serverSocket = new ServerSocket(12345, 0, InetAddress.getByName("0.0.0.0"));

            while (true) {
                // Accept incoming client connections and start a new thread to handle each client
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        public void run() {
            try {
                // Create input and output streams for the client
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Add the client's output stream to the set of client writers
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }
                String message;
                while((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    // Broadcast the received message to all connected clients
                    broadcast(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Remove client's output stream from the set of client writers
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
        private void broadcast(String message) {
            // Send message to all connected clients
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }



}
