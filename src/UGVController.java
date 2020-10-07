import com.pi4j.io.gpio.*;

import java.net.Socket;

public class UGVController implements Runnable {
    private Socket socket;

    public UGVController(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
    }
}
