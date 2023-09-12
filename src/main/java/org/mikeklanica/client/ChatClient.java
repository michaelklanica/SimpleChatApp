package org.mikeklanica.client;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Connect to the server running on localhost at port 12345
            Socket socket = new Socket("localhost", 12345);

            // Create input and output streams for communication with the server
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Read the user's name from the console
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your name: ");
            String name = consoleInput.readLine();

            // Send a message to the server indicating that this client has joined
            out.println(name + " has joined the chat.");

            // Start a separate thread to continuously listen for messages from the server
            Thread messageReceiver = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverIn.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageReceiver.start();

            // Allow the user to send messages to the server
            System.out.println("Start chatting (Type 'exit' to quit).");
            String message;
            while (true) {
                message = consoleInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(name + ": " + message);
            }

            // Close the socket and terminate the client
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
