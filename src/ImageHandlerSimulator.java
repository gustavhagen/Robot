import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * This class sends images that are stored in a folder.
 * This class is only for simulation purposes.
 *
 * @author Gustav Sørdal Hagen
 */

public class ImageHandlerSimulator implements Runnable {

    // Creates some variables to use in the class.
    private Socket socket;
    private int totalImages;
    private ObjectOutputStream objectOutputStream;
    private int imageCounter = 0;
    private volatile boolean captureImage;
    private volatile boolean run = true;
    private static Date date = new Date();

    /**
     * Constructor for the ImageHandlerSimulator
     *
     * @param socket             The socket that is connected to the server.
     * @param totalImages        Total images that Simulator are going to send.
     * @param objectOutputStream The objectOutputStream that is connected to the socket.
     */
    public ImageHandlerSimulator(Socket socket, int totalImages, ObjectOutputStream objectOutputStream) {
        this.socket = socket;
        this.totalImages = totalImages;
        this.objectOutputStream = objectOutputStream;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Sends images that are stored in a folder.
     *
     * @throws IOException          If the ImageHandlerSimulator could not send an image.
     * @throws InterruptedException If the Thread.sleep()-method is interrupted-
     */
    @Override
    public void run() {
        while (run) {
            try {
                if (captureImage && imageCounter < totalImages) {
                    File image = new File("C:\\Users\\Gustav\\Desktop\\images\\image" + imageCounter + ".jpg");
                    if (image != null) {
                        captureImage = false;
                        byte[] byteArray = imageToByteArray(image);
                        ImageObject imageObject = new ImageObject("Image" + imageCounter, image.length(), byteArray, "" + date, "jpg");
                        objectOutputStream.writeObject(imageObject);
                    }
                    imageCounter++;
                }
                Thread.sleep(100);
            } catch (IOException e) {
                System.out.println("[UGV] Could not send image.");
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A method that returns the bytes of an image as a bytearray.
     *
     * @param image The image to get the bytearray from.
     * @return Returns a bytearray for the image
     * @throws IOException If image could not be written
     */
    private static final byte[] imageToByteArray(File image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("Could not write image.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the run-variable in the Run()-method to false. The loop stops.
     */
    public void stopThread() {
        run = false;
    }
}