import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;


public class ImageHandler {

    private final static String HOST = "10.22.192.92";
    private final static String SONDRE_HOST = "83.243.218.40";
    private final static int PORT = 42069;

    public static void main(String[] args) throws IOException {
        System.out.println("Connecting to server...");
        Socket socket = new Socket(SONDRE_HOST, PORT);
        System.out.println("Connected to server on: " + SONDRE_HOST + ":" + PORT);

        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);
        System.out.println("Found Camera!");
        camera.set(3, 1920);    // Width of image
        camera.set(4, 1080);    // Height of image
//        camera.set(5, 1);       // Framerate
//        camera.set(20, 0);      // Sharpness
//        camera.set(39, 0);      // Auto-focus
//        camera.set(22, 100);    // Gamma
        System.out.println("Resolution Set!");

        if (!camera.isOpened()) {
            System.out.println("Camera not opened!");
        }

        Mat imageMatrix = new Mat();
        MatOfByte imageBytes = new MatOfByte();
        System.out.println("Camera Connected!");

        ImageObject imageObject = null;
        int imageSize = 0;
        int imageCounter = 0;
        while (imageCounter < 10) {
            camera.read(imageMatrix);

            Imgcodecs.imencode(".jpg", imageMatrix, imageBytes);
            imageSize = (int) (imageBytes.total() * imageBytes.elemSize());

            imageObject = new ImageObject("Image" + imageCounter, imageSize, imageBytes.toArray(), "01.10.2020", "jpg");
            objectOutputStream.writeObject(imageObject);
            System.out.println("Size of image" + imageCounter + ": " + imageSize);
            System.out.println("Image was sent!");
            imageCounter++;
            imageObject = null;
        }
        System.out.println("Done!");
    }

    private static final byte[] imageToByteArray(File image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
