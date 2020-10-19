import com.pi4j.io.gpio.*;
import java.net.Socket;

public class UGVController implements Runnable {
    Socket socket;
    StepperMotor stepperCamera;
    StepperMotor stepperTurn;
    Servo cameraServo;
    DriveMotor driveMotor;
    ImageHandler imageHandler;
    UltraSonicSensor ultraSonicFrontRight;
    UltraSonicSensor ultrasonicFrontLeft;
    UltraSonicSensor ultrasonicBack;
    UltraSonicSensor ultrasonicSide;


    private static GpioController gpioController = GpioFactory.getInstance();

    // Instance pins for Stepper Motors
    GpioPinDigitalOutput stepperCameraPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00);
    GpioPinDigitalOutput stepperCameraDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
    GpioPinDigitalOutput stepperTurnPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02);
    GpioPinDigitalOutput stepperTurnDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03);

    // Instance pins for DC motor
    GpioPinDigitalOutput driveMotorPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03);

    // Instance pins for Servo
    GpioPinDigitalOutput servoPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04);

    // Instance pins for Ultrasonic sensors
    GpioPinDigitalOutput trig1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_22);
    GpioPinDigitalInput echo1 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_23, PinPullResistance.PULL_DOWN);
    GpioPinDigitalOutput trig2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_24);
    GpioPinDigitalInput echo2 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_25, PinPullResistance.PULL_DOWN);
    GpioPinDigitalOutput trig3 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_26);
    GpioPinDigitalInput echo3 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN);
    GpioPinDigitalOutput trig4 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_28);
    GpioPinDigitalInput echo4 = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);


    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVController(Socket socket) {
        this.socket = socket;
        ultraSonicFrontRight = new UltraSonicSensor(trig1, echo1);
        ultrasonicFrontLeft = new UltraSonicSensor(trig2, echo2);
        ultrasonicBack = new UltraSonicSensor(trig3, echo3);
        ultrasonicSide = new UltraSonicSensor(trig4, echo4);
        stepperCamera = new StepperMotor(stepperCameraPul, stepperCameraDir);
        stepperTurn = new StepperMotor(stepperTurnPul, stepperTurnDir);
        driveMotor = new DriveMotor(driveMotorPin);
        cameraServo = new Servo(servoPin);
    }

    public void run() {
        try {
            driveMotor.driveForward(TEST_STEPS, 1000);

            stepperCamera.moveUp(TEST_STEPS);
            captureImageAndWait();

            stepperCamera.moveDown(TEST_STEPS);
            captureImageAndWait();

            driveMotor.driveForward(TEST_STEPS, 1000);
            stepperTurn.turnLeft(TEST_STEPS);
            driveMotor.driveForward(TEST_STEPS, 1000);

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
