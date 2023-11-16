import java.io.*;
import java.net.Socket;

public class FileClientTCP {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 23456;

    public static void main(String[] args) {
        try {
            // Membaca file yang akan dikirim ke server
            String filePath = "D:/Client File/kirim.txt";
            File file = new File(filePath);
            Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server.");

            // Mengirim file ke server
            sendFile(file, clientSocket);

            // Menutup socket setelah selesai
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(File file, Socket clientSocket) {
        try {
            // Inisialisasi output stream untuk mengirim data ke server
            OutputStream outputStream = clientSocket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            // Mengirim informasi file (nama dan ukuran)
            objectOutputStream.writeUTF(file.getName());
            objectOutputStream.writeLong(file.length());
            objectOutputStream.flush();

            // Inisialisasi input stream untuk membaca data dari file
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Membaca dan mengirim data file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Menutup output stream dan file
            bufferedInputStream.close();
            clientSocket.shutdownOutput();

            System.out.println("File sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
