import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Client {


    public void send(String file_name){
        try {
            Socket socket = new Socket("localhost", 52342);
            File file = new File(file_name);
            System.out.println(file_name);
            ObjectInputStream ois = new ObjectInputStream(
                    socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(
                    socket.getOutputStream());
            oos.writeObject(file.getName());
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[Server.BUFFER_SIZE];
            Integer bytesRead = 0;
            while ((bytesRead = fis.read(buffer)) > 0) {
                oos.writeObject(bytesRead);
                oos.writeObject(Arrays.copyOf(buffer, buffer.length));
            }
            ois = null;
            oos = null;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Client c = new Client();
        c.send("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\sau.jpg");         // first image path
        c.send("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\esel.jpg");        //  second image path
        c.send("C:\\Users\\Gustav\\Documents\\NTNU\\HØST 2020\\Sanntids Datateknikk\\Prosjekt\\JavaCode\\images\\elefant.jpg");
    }
}