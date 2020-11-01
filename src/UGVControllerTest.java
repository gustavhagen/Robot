import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UGVControllerTest implements Runnable {
    Socket socket;
    ImageHandler imageHandler;


    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVControllerTest(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            Command command = new Command("UGV", 0);
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                Command command = (Command) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
