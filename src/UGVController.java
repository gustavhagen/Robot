import com.pi4j.io.gpio.*;

import java.net.Socket;

public class UGVController implements Runnable {
    Socket socket;
    Drive drive;
    CameraElevator elevator;
    ImageHandler imageHandler;
    private static UltraSonicSensor ultrosonicFrontRight;
    private static UltraSonicSensor ultrasonicFrontLeft;
    private static UltraSonicSensor ultrasonicBack;
    private static UltraSonicSensor ultrasonicSide;


    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput trig1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static GpioPinDigitalInput echo1 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
    private static GpioPinDigitalOutput trig2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static GpioPinDigitalInput echo2 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
    private static GpioPinDigitalOutput trig3 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static GpioPinDigitalInput echo3 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
    private static GpioPinDigitalOutput trig4 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    private static GpioPinDigitalInput echo4 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);

    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVController(Socket socket) {
        this.socket = socket;
        ultrosonicFrontRight = new UltraSonicSensor(trig1, echo1);
        ultrasonicFrontLeft = new UltraSonicSensor(trig2, echo2);
        ultrasonicBack = new UltraSonicSensor(trig3, echo3);
        ultrasonicSide = new UltraSonicSensor(trig4, echo4);
    }


    public void run() {
        try {
            drive.stepperMotorAct(TEST_STEPS);

            elevator.moveUp(TEST_STEPS);
            captureImageAndWait();

            elevator.moveDown(TEST_STEPS);
            captureImageAndWait();

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
