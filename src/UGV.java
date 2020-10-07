import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UGV {
    private final static String HOST = "10.22.192.92";
    private final static String SONDRE_HOST = "83.243.218.40";
    private final static int PORT = 42069;
    private final static int POOL_SIZE = 3;
    private final static int TOTAL_IMAGES = 100;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);


    public static void main(String[] args) {
        try {
            System.out.println("Connecting to server...");
            Socket imageSocket = new Socket(HOST, PORT);
            System.out.println("Connected ImageHandler to server!");
            Socket UGVSocket = new Socket(HOST, PORT);
            System.out.println("Connected UGV to server!");


            threadPool.execute(new ImageHandler(imageSocket, TOTAL_IMAGES));
            threadPool.execute(new UGVController(UGVSocket));


        } catch (UnknownHostException e) {
            System.out.println("Could not connect to server...");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An I/O error occurred...");
            e.printStackTrace();
        }
    }
}
