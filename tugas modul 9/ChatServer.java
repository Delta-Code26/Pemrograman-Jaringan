import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public interface ChatServer extends Remote {
  void sendMessage(String message) throws RemoteException;

  void registerClient(ChatClient client) throws RemoteException;
}

class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
  private List<ChatClient> clients;

  public ChatServerImpl() throws RemoteException {
    clients = new ArrayList<>();
  }

  @Override
  public void registerClient(ChatClient client) throws RemoteException {
    clients.add(client);
  }

  @Override
  public void sendMessage(String message) throws RemoteException {
    System.out.println("Server received message: " + message);
    broadcastMessage(message);
  }

  private void broadcastMessage(String message) throws RemoteException {
    for (ChatClient client : clients) {
      client.receiveMessage(message);
    }
  }

  public static void main(String[] args) {
    try {
      ChatServerImpl server = new ChatServerImpl();
      Registry registry = LocateRegistry.createRegistry(1099);
      registry.rebind("ChatServer", server);
      System.out.println("ChatServer is running...");
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
}

interface ChatClient extends Remote {
  void receiveMessage(String message) throws RemoteException;
}

class ChatClientImpl extends UnicastRemoteObject implements ChatClient {
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

      // Your code to send messages from client to server goes here

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
