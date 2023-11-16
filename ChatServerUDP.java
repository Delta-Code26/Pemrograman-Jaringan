import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ChatServerUDP {
    private static final int PORT = 23456;
    private static List<InetAddress> clientAddresses = new ArrayList<>();
    private static List<Integer> clientPorts = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if (!clientAddresses.contains(clientAddress) && !clientPorts.contains(clientPort)) {
                    clientAddresses.add(clientAddress);
                    clientPorts.add(clientPort);
                    System.out.println("New client connected: " + clientAddress.getHostAddress() + ":" + clientPort);
                }

                System.out.println("Received message from " + clientAddress.getHostAddress() + ":" + clientPort + ": " + message);

                // Broadcast the message to all clients
                broadcastMessage(message, clientAddress, clientPort, serverSocket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(String message, InetAddress senderAddress, int senderPort, DatagramSocket serverSocket) {
        try {
            for (int i = 0; i < clientAddresses.size(); i++) {
                if (!clientAddresses.get(i).equals(senderAddress) || clientPorts.get(i) != senderPort) {
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddresses.get(i), clientPorts.get(i));
                    serverSocket.send(sendPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
