import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class ImageHandler {

    private final static String HOST = "10.22.192.92";
    private final static String SONDRE_HOST = "83.243.218.40";
    private final static int PORT = 42069;
    private static Webcam webcam;

    public static void main(String[] args) throws IOException {

        System.out.println("Connecting to server...");
        Socket socket = new Socket(SONDRE_HOST, PORT);
        System.out.println("Connected to server on: " + SONDRE_HOST + ":" + PORT);


        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        int imageCounter = 0;
        while (!socket.isClosed() & (imageCounter < 10)) {
            webcam = Webcam.getDefault();
            webcam.setViewSize(new Dimension(1920,1080));
            webcam.open();
            ImageIO.write(webcam.getImage(), "jpg", new File("C:\\Users\\gusta\\OneDrive\\Skrivebord\\images\\iamge" + imageCounter + ".jpg"));
            System.out.println("Image was taken!");
            webcam.close();


            ImageObject imageObject = null;
            File image = new File("C:\\Users\\gusta\\OneDrive\\Skrivebord\\images\\iamge" + imageCounter + ".jpg");
            byte[] byteArray = imageToByteArray(image);
            imageObject = new ImageObject("Image" + imageCounter, image.length(), byteArray, "23.09.2020", "jpg");
            objectOutputStream.writeObject(imageObject);
            System.out.println("Image was sent!");
            // Thread.sleep(2000);
            imageCounter++;
        }
    }

    private static final byte[] imageToByteArray(File image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
