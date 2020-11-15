import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * This class is taking care of the capturing of images and sends them to the server.
 *
 * @author Gustav Sørdal Hagen
 */
public class ImageHandler implements Runnable {

    // Creates some variables to use in the class.
    private Socket socket;
    private final int totalImages;
    private final ObjectOutputStream objectOutputStream;
    private final VideoCapture camera;
    private int imageCounter = 0;
    private volatile boolean captureImage;
    private volatile boolean run = true;
    private static final Date date = new Date();

    /**
     * The constructor for the ImageHandler class.
     * @param socket The socket that is connected to the server.
     * @param totalImages Total images to capture
     * @param objectOutputStream The outputstream that is connected to the socket.
     */
    public ImageHandler(Socket socket, int totalImages, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.totalImages = totalImages;
        this.objectOutputStream = objectOutputStream;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);
        System.out.println("[UGV] Found Camera!");
        camera.set(3, 1920);    // Width of image
        camera.set(4, 1080);    // Height of image
        System.out.println("[Camera] Resolution Set!");
        if (!camera.isOpened()) {
            System.out.println("[UGV] Camera not opened!");
        }
    }

    /**
     * Runs the capturing of an image. This method runs all the time when the
     * imageThread in the UGVSimulator-class is enabled.
     * @throws IOException If the ImageHandler could not send an image.
     * @throws InterruptedException If the Thread.sleep()-method is interrupted-
     */
    @Override
    public void run() {
        // While-loop that always runs as the run-variable is not set to false
        while (run) {
            try {
                // captureImage is set to true when CaptureImage()-method is called.
                // Watches if the amount of images is below total images to capture.
                if (captureImage && imageCounter < totalImages) {
                    // Creates a imageMatrix and a matrix of bytes.
                    Mat imageMatrix = new Mat();
                    MatOfByte imageBytes = new MatOfByte();

                    // Captured a image with the attached camera. Needs the imageMatrix.
                    camera.read(imageMatrix);
                    captureImage = false;

                    //Encodes an image into a memory buffer with the imageMatrix and imageBytes.
                    Imgcodecs.imencode(".jpg", imageMatrix, imageBytes);
                    // The size of the image to send.
                    int imageSize = (int) (imageBytes.total() * imageBytes.elemSize());

                    // Creates an imageObject to send to the server.
                    ImageObject imageObject = new ImageObject("Image" + imageCounter, imageSize,
                                                                imageBytes.toArray(), "" + date, "jpg");

                    // Writes the imageObject to the outputstream that sends the image to the server
                    objectOutputStream.writeObject(imageObject);
                    imageCounter++;
                }
                Thread.sleep(100);
            } catch (IOException e) {
                System.out.println("[UGV] Could not send image.");
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the captureImage-variable to true for making the run()-method capture and send an image.
     */
    public void captureImage() {        // May be synchronized
        captureImage = true;
    }

    /**
     * Sets the run-variable in the Run()-method to false. The loop stops.
     */
    public void stopThread(){
        run = false;
    }

    /**
     * Returns true/false for the captureImage.
     * Used in the UGVSimulator when the UGV are going to wait while an image is being captured.
     * @return true/false for the captureImage.
     */
    public boolean isCapturingImage() {
        return captureImage;
    }

//    private static final byte[] imageToByteArray(File image) throws IOException {
//        BufferedImage bufferedImage = ImageIO.read(image);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
//        return byteArrayOutputStream.toByteArray();
//    }
}