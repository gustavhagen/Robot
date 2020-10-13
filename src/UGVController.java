import java.net.Socket;

public class UGVController implements Runnable {
    private Socket socket;
    private Drive drive;
    private CameraElevator elevator;
    private ImageHandler imageHandler;

    public UGVController(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
