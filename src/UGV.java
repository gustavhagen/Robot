import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This project is the main project in the course Real-Time Programming at NTNU in Ålesund.
 * <p>
 * This is the main class for the UGV in this project. The UGV connects to a server running on the given
 * hosts. There is set up 2 different hosts while testing the system. The port is also given.
 * After connecting to the server, the UGV-class runs either the UGVController class or the UGVSimulator.
 *
 * @author Gustav Sørdal Hagen
 */

public class UGV {
    private final static String HOST = "10.22.192.92";
    private final static String SONDRE_HOST = "83.243.240.94";
    private final static int PORT = 42069;

    /**
     * Runs the UGV
     *
     * @param args
     * @throws UnknownHostException If the UGV could not connect to a server
     * @throws IOException          If an I/O error occurred.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to server...");

            // Connects to the server with the given host-address and port.
            Socket socket = new Socket(SONDRE_HOST, PORT);

            // Creates two streams for communicating with the server.
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected UGV to server!");

            int threadPoolSize = 3;
            UGVSimulator ugvSimulator = new UGVSimulator(socket, objectOutputStream, objectInputStream, threadPoolSize);
            UGVController ugvController = new UGVController(socket, objectInputStream, objectOutputStream, threadPoolSize);
            System.out.println("Running UGV...");
            ugvSimulator.run();
            System.out.println("Program ended. UGV was done!");


        } catch (UnknownHostException e) {
            System.out.println("Could not connect to server...");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An I/O error occurred...");
            e.printStackTrace();
        }
    }
}
