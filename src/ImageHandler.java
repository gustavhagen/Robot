
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

import org.opencv.highgui.HighGui;

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
        camera.set(3, 1920);
        camera.set(4, 1080);
        System.out.println("Resolution Set!");
        if (!camera.isOpened()) {
            System.out.println("Camera not opened!");
        }

        Mat matrix = new Mat();
        MatOfByte frame = new MatOfByte();
        System.out.println("Camera Connected!");

        int imageSize = 0;
        int imageCounter = 0;
        while (!socket.isClosed() & (imageCounter < 10)) {
            camera.read(matrix);
            ImageObject imageObject = null;
            imageSize = (int) (frame.total() * frame.elemSize());

            Imgcodecs.imencode(".jpg", matrix ,frame);

            imageObject = new ImageObject("Image" + imageCounter, imageSize, frame.toArray(), "01.10.2020", "jpg");
            objectOutputStream.writeObject(imageObject);
            System.out.println("Image was sent!");
            imageCounter++;
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
