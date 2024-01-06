import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiServer {

  private static final int PORT = 2222;
  static List<ClientHandler> clients = new ArrayList<>();
  private static ExecutorService executorService = Executors.newFixedThreadPool(10);

  public static void main(String[] args) {
    try {
      try (ServerSocket serverSocket = new ServerSocket(PORT)) {
        System.out.println("Server listening on port " + PORT);

        while (true) {
          Socket clientSocket = serverSocket.accept();
          System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostAddress());

          // Create a new ClientHandler thread for each client
          ClientHandler clientHandler = new ClientHandler(clientSocket);
          clients.add(clientHandler);
          executorService.execute(clientHandler);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Broadcast message to all connected clients
  public static synchronized void broadcastMessage(String message, ClientHandler sender) {
    for (ClientHandler client : clients) {
      if (client != sender) {
        client.sendMessage(message);
      }
    }
  }
}

class ClientHandler implements Runnable {

  private Socket clientSocket;
  private InputStream input;
  private OutputStream output;

  public ClientHandler(Socket clientSocket) {
    try {
      this.clientSocket = clientSocket;
      this.input = clientSocket.getInputStream();
      this.output = clientSocket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    try {
      while (true) {
        byte[] buffer = new byte[1024];
        int bytesRead = input.read(buffer);

        if (bytesRead == -1) {
          break; // Client disconnected
        }

        String message = new String(buffer, 0, bytesRead);
        System.out.println("Received message from " + clientSocket.getInetAddress().getHostAddress() + ": " + message);

        // Broadcast the message to all clients
        MultiServer.broadcastMessage(message, this);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      // Remove the client from the list when disconnected
      MultiServer.clients.remove(this);
      System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());

      try {
        clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // Send a message to the client
  public void sendMessage(String message) {
    try {
      output.write(message.getBytes());
      output.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
