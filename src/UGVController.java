import java.io.IOException;
import java.net.Socket;

public class UGVController implements Runnable {
    Socket socket;
    Drive drive;
    CameraElevator elevator;
    ImageHandler imageHandler;
    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVController(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            drive.motorAct(TEST_STEPS);

            elevator.moveUp(TEST_STEPS);
            captureImageAndWait();

            elevator.moveDown(TEST_STEPS);
            captureImageAndWait();

            drive.motorAct(TEST_STEPS);
            drive.turnLeft(TEST_STEPS);
            drive.motorAct(TEST_STEPS);

//            switch(state){
//                case IDLE -> {
//                }
//                case CIRCLE -> {
//                    System.out.println("REEEE");
//                    drive.motorAct(TEST_STEPS);
//                }
//                case CAPTURE -> {
//                    System.out.println("REE!!!EE");
//                    captureImageAndWait();
//                }
//                case SNIIII -> {
//                }
//            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
