import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Test {
    public static void main(String[] args) throws IOException {


        Socket socket = new Socket("10.22.192.92", 42069);
        System.out.println("Connected to server!");

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        int bufferSize = 10000000;
        byte[] buffer = new byte[bufferSize];

        // DataOutputStream dos...
        // dos.writeInt(imageSize);

        while (true) {
            int imageSize = dataInputStream.readInt(); // readInt er en blokkerende moetode, venter på at bilde kommer
            int bytesReceived = 0;
            // SJEKK OM bytesInThisPortion RETURNERER KORRKTE TALL
            do {
                int bytesInThisPortion = inputStream.readNBytes(buffer, bytesReceived, imageSize - bytesReceived);
                bytesReceived += bytesInThisPortion;
            } while (bytesReceived < imageSize);
            // processImage(buffer);
        }
    }
}
