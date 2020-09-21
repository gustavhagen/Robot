import java.io.*;
import java.net.Socket;

public class ImageSender2 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Socket socket = new Socket("83.243.218.40", 42069);
        OutputStream outputStream = socket.getOutputStream();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\gusta\\OneDrive\\Skrivebord\\images"));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        Object object = objectInputStream.readObject();


        while(!socket.isClosed()){
            objectOutputStream.writeObject(object);
        }
    }
}
