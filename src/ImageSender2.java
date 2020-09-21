import java.io.*;
import java.net.Socket;

public class ImageSender2 {
    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("", 42069);
        OutputStream outputStream = socket.getOutputStream();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Gustav\\Desktop\\images\\sau.jpg"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);


        while(!socket.isClosed()){

        }


    }
}
