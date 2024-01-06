import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public interface ChatClient extends Remote {
  void receiveMessage(String message) throws RemoteException;
}

class ChatClientImpl extends java.rmi.server.UnicastRemoteObject implements ChatClient {
  protected ChatClientImpl() throws RemoteException {
    super();
  }

  @Override
  public void receiveMessage(String message) throws RemoteException {
    System.out.println("Received message: " + message);
  }

  public static void main(String[] args) {
    try {
      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      ChatServer server = (ChatServer) registry.lookup("ChatServer");
      ChatClientImpl client = new ChatClientImpl();
      server.registerClient(client);

      try (Scanner scanner = new Scanner(System.in)) {
        while (true) {
          System.out.print("Enter message: ");
          String message = scanner.nextLine();
          server.sendMessage(message);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
