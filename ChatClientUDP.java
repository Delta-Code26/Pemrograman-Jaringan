import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatClientUDP {
    private static final int PORT = 23456;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");

            // Send the client's name to the server
            byte[] sendData = name.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORT);
            clientSocket.send(sendPacket);

            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            receiveThread.start();

            System.out.println("Type your messages. Type 'exit' to quit.");

            while (true) {
                String sendMessage = scanner.nextLine();

                if (sendMessage.equalsIgnoreCase("exit")) {
                    System.out.println("Closing the chat.");
                    clientSocket.close();
                    System.exit(0);
                }

                sendMessage = name + ": " + sendMessage;

                sendData = sendMessage.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, PORT);
                clientSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
