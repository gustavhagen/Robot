import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.net.Socket;

public class UGVController implements Runnable {
    Socket socket;
    Drive drive;
    CameraElevator elevator;
    ImageHandler imageHandler;
    private static UltraSonicSensor ultraSonicSensor;
    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static GpioPinDigitalInput echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);

    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVController(Socket socket) {
        this.socket = socket;
        ultraSonicSensor = new UltraSonicSensor(trig, echo);
    }


    @Override
    public void run() {
        try {
            ultraSonicSensor.getDistance();
            System.out.println("Distance: " + ultraSonicSensor.getDistance() + " cm");

            drive.stepperMotorAct(TEST_STEPS);

            elevator.moveUp(TEST_STEPS);
            //captureImageAndWait();

            elevator.moveDown(TEST_STEPS);
            //captureImageAndWait();

            drive.stepperMotorAct(TEST_STEPS);
            drive.turnLeft(TEST_STEPS);
            drive.stepperMotorAct(TEST_STEPS);

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
