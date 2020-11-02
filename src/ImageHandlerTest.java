import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;


public class ImageHandlerTest implements Runnable {

    private Socket socket;
    private int totalImages;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    private VideoCapture camera;
    private int imageCounter = 0;
    private volatile boolean takeImage;
    private boolean[] wasd = new boolean[4];


    public ImageHandlerTest(Socket socket, int totalImages, ObjectOutputStream objectOutputStream) throws IOException {
        this.socket = socket;
        this.totalImages = totalImages;
        this.objectOutputStream = objectOutputStream;
    }

    @Override
    public void run() {
        try {
            Command command = new Command("Image", totalImages, null, null);
            objectOutputStream.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                if (takeImage) {
                    Mat imageMatrix = new Mat();
                    MatOfByte imageBytes = new MatOfByte();
                    System.out.println("Camera Connected!");

                    camera.read(imageMatrix);
                    System.out.println("Image captured!");
                    takeImage = false;

                    Imgcodecs.imencode(".jpg", imageMatrix, imageBytes);
                    int imageSize = (int) (imageBytes.total() * imageBytes.elemSize());

                    ImageObject imageObject = new ImageObject("Image" + imageCounter, imageSize, imageBytes.toArray(), "07.10.2020", "jpg");
                    objectOutputStream.writeObject(imageObject);
                    System.out.println("Size of image" + imageCounter + ": " + imageSize);
                    System.out.println("Image was sent!");
                    imageCounter++;
                }
            } catch (IOException e) {
                System.out.println("Could not send image.");
                e.printStackTrace();
            }
        }
    }

    public void captureImage() {        // May be synchronized
        takeImage = true;
    }

    public boolean isCapturingImage() {
        return takeImage;
    }

    private static final byte[] imageToByteArray(File image) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}