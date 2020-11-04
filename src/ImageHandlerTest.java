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
    private ObjectOutputStream objectOutputStream;
    private VideoCapture camera;
    private int imageCounter = 0;
    private volatile boolean takeImage;
    private volatile boolean run = true;


    public ImageHandlerTest(Socket socket, int totalImages, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.totalImages = totalImages;
        this.objectOutputStream = objectOutputStream;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);
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
    }

    @Override
    public void run() {
        while (run) {
            try {
                if (takeImage && imageCounter < totalImages) {
                    Mat imageMatrix = new Mat();
                    MatOfByte imageBytes = new MatOfByte();

                    camera.read(imageMatrix);
                    takeImage = false;

                    Imgcodecs.imencode(".jpg", imageMatrix, imageBytes);
                    int imageSize = (int) (imageBytes.total() * imageBytes.elemSize());

                    ImageObject imageObject = new ImageObject("Image" + imageCounter, imageSize, imageBytes.toArray(), "07.10.2020", "jpg");
                    objectOutputStream.writeObject(imageObject);
                    imageCounter++;
                }
                Thread.sleep(100);
            } catch (IOException e) {
                System.out.println("Could not send image.");
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void captureImage() {        // May be synchronized
        takeImage = true;
    }

    public void stopThread(){
        run = false;
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