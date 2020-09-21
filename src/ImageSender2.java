import java.io.*;
import java.net.Socket;

public class ImageSender2 {

    private final static String HOST = "83.243.218.40";
    private final static int PORT = 42069;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        System.out.println("Connecting to server...");
        Socket socket = new Socket(HOST, PORT);
        System.out.println("Connected to server on: " + HOST + ":" + PORT);
        OutputStream outputStream = socket.getOutputStream();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Gustav\\Desktop\\images\\sau.jpg"));
        //File file = new File("C:\\Users\\Gustav\\Desktop\\images\\sau.jpg");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        Object object = objectInputStream.readObject();

        int imageCounter = 0;
        while(!socket.isClosed() & (imageCounter < 4)){
            objectOutputStream.writeObject(object);
            //int imageSize = (int)file.length();
            //System.out.println(imageSize);
            imageCounter++;
        }
    }
}
