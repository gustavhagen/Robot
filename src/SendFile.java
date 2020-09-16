import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendFile {
    public static void main(String[] args) throws IOException, InterruptedException {

            Socket socket = new Socket("10.22.192.92", 42069);
            System.out.println("Connected to server!");

            OutputStream outputStream = socket.getOutputStream();

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            BufferedImage image = ImageIO.read(new File("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\esel.jpg"));
            ImageIO.write(image, "jpg", bufferedOutputStream);
            bufferedOutputStream.flush();
            System.out.println("Picture Sent!");

            bufferedOutputStream = new BufferedOutputStream(outputStream);
            image = ImageIO.read(new File("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\sau.jpg"));
            ImageIO.write(image, "jpg", bufferedOutputStream);
            bufferedOutputStream.flush();
            System.out.println("Picture Sent!");

            bufferedOutputStream = new BufferedOutputStream(outputStream);
            image = ImageIO.read(new File("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\elefant.jpg"));
            ImageIO.write(image, "jpg", bufferedOutputStream);
            bufferedOutputStream.flush();
            System.out.println("Picture Sent!");
            Thread.sleep(5000);

            bufferedOutputStream.close();
            socket.close();
    }
}
