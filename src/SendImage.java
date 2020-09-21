import java.io.*;
import java.net.Socket;

public class SendImage {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("10.22.192.92", 42069);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = null;
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        int bufferSize = 2^16;

        while(true){
            byte[] buffer = new byte[bufferSize];
            File file = new File("C:\\Users\\Gustav\\Desktop\\images\\sau.jpg");
            int length = (int)file.length();

            System.out.println(length);

            dataOutputStream.writeInt(length);
            inputStream = new FileInputStream(file);

            int bytesSent = 0;
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
            System.out.println("Image was sent!");
            dataOutputStream.flush();
        }
    }
}
