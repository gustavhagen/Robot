import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ImageSender implements Runnable{

    private Socket socket;
    private OutputStream outputStream;
    BufferedImage image;

    public ImageSender(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = socket.getOutputStream();
    }

    @Override
    public void run(){
        try {
            // GUSTAV TESTER GIT PÅ STASJA
            int imageCounter = 0;
            while (!socket.isClosed() & imageCounter < 3) {
                image = ImageIO.read(new File("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\sau.jpg"));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", byteArrayOutputStream);
                System.out.println(byteArrayOutputStream.size());

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());
                outputStream.flush();
                System.out.println("Picture sent!");
                System.out.println("Flushed: " + System.currentTimeMillis());

//                Thread.sleep(5000);
//
//                ImageIO.write(image2, "jpg", byteArrayOutputStream);
//                System.out.println(byteArrayOutputStream.size());
//
//                size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//                outputStream.write(size);
//                outputStream.write(byteArrayOutputStream.toByteArray());
//                outputStream.flush();
//                System.out.println("Picture sent!");
//                System.out.println("Flushed: " + System.currentTimeMillis());
//
//                Thread.sleep(5000);
//
//
//                ImageIO.write(image3, "jpg", byteArrayOutputStream);
//                System.out.println(byteArrayOutputStream.size());
//
//                size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
//                outputStream.write(size);
//                outputStream.write(byteArrayOutputStream.toByteArray());
//                outputStream.flush();
//                System.out.println("Picture sent!");
//                System.out.println("Flushed: " + System.currentTimeMillis());
//
//                Thread.sleep(5000);
                imageCounter++;
            }
                System.out.println("Closing: " + System.currentTimeMillis());
                socket.close();
            } catch(IOException e){

            }

    }
}
