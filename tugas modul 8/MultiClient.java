import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class MultiClient {

  private static final String SERVER_IP = "localhost";
  private static final int SERVER_PORT = 2222;

  public static void main(String[] args) {
    try {
      Socket socket = new Socket(SERVER_IP, SERVER_PORT);
      System.out.println("Connected to the server");

      // Start a separate thread for receiving messages from the server
      Thread receiveThread = new Thread(new ClientReceiver(socket));
      receiveThread.start();

      try (// Send messages to the server from the main thread
          Scanner scanner = new Scanner(System.in)) {
        OutputStream output = socket.getOutputStream();

        while (true) {
          String message = scanner.nextLine();
          output.write(message.getBytes());
          output.flush();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class ClientReceiver implements Runnable {

  private Socket socket;
  private InputStream input;

  public ClientReceiver(Socket socket) {
    try {
      this.socket = socket;
      this.input = socket.getInputStream();
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
          break; // Server disconnected
        }

        String message = new String(buffer, 0, bytesRead);
        System.out.println("Received message from server: " + message);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        socket.close();
        System.exit(0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
