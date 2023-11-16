import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServerTCP {

    private static final int PORT = 23456;
    private static final String SAVE_DIR = "D:/Server File/"; // Sesuaikan dengan direktori penyimpanan yang sesuai di server

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        ExecutorService executorService = Executors.newFixedThreadPool(5); // Batasan jumlah klien yang dapat ditangani secara bersamaan

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())
            ) {
                try {
                    String fileName = ois.readUTF();
                    long fileSize = ois.readLong();

                    File file = new File(SAVE_DIR + fileName);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = ois.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                        }

                        System.out.println("File " + fileName + " received successfully from " + clientSocket.getInetAddress().getHostAddress());
                        dos.writeUTF("File " + fileName + " received successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        dos.writeUTF("Error receiving file.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
