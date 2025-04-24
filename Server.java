import java.net.*;
import java.util.Vector;
import java.io.*;

public class Server implements Runnable {

    // Socket reference variable for each client
    private Socket socket;

    // Storing all connected clients using a Vector
    public static Vector<DataOutputStream> clients = new Vector<>();

    // Constructor to initialize the socket
    public Server(Socket s) {
        try {
            this.socket = s;
        } catch (Exception e) {
            System.out.println("Error initializing socket: " + e.getMessage());
        }
    }

    // The run method handles communication with a single client
    public void run() {
        try {
            // Create input and output streams for the client
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Add the client's output stream to the list of clients
            clients.add(dataOutputStream);
            System.out.println("New client added. Total clients: " + clients.size());

            // Continuously read messages from the client
            while (true) {
                String msgInput = dataInputStream.readUTF();
                System.out.println("Received: " + msgInput);

                // Broadcast the message to all connected clients
                broadcastMessage(msgInput);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            // Remove the client from the list when disconnected
            removeClient();
        }
    }

    // Method to broadcast a message to all connected clients
    private void broadcastMessage(String message) {
        for (int i = 0; i < clients.size(); i++) {
            try {
                DataOutputStream dos = clients.get(i);
                dos.writeUTF(message); // Send the message to each client
            } catch (IOException e) {
                System.out.println("Error broadcasting message: " + e.getMessage());
            }
        }
    }

    // Method to remove a client from the list when disconnected
    private void removeClient() {
        try {
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).equals(socket.getOutputStream())) {
                    clients.remove(i);
                    System.out.println("Client removed. Total clients: " + clients.size());
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error removing client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Create a ServerSocket and bind it to port 8080
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Server is running and listening on port 8080...");

            // Infinite loop to accept multiple client connections
            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                // Create a new Server object for the client
                Server server = new Server(socket);

                // Create a new thread for the client and start it
                Thread thread = new Thread(server);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}