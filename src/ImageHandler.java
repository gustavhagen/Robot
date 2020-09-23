import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class ImageHandler{

    private final static String HOST = "10.22.192.92";
    private final static int PORT = 42069;

    public static void main(String[] args) throws IOException, InterruptedException {
            System.out.println("Connecting to server...");
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connected to server on: " + HOST + ":" + PORT);

            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            ImageObject imageObject = null;

            int imageCount = 0;
            while(!socket.isClosed() & (imageCount < 50)){
                File image = new File("C:\\Users\\Gustav\\Desktop\\images\\sau.jpg");
                byte[] byteArray = imageToByteArray(image);
                imageObject = new ImageObject("Image"+imageCount, image.length(), byteArray, "23.09.2020", "jpg");
                objectOutputStream.writeObject(imageObject);
                System.out.println("Image was sent!");
                // Thread.sleep(2000);
                imageCount++;
            }
    }

    private static final byte[] imageToByteArray(File image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
