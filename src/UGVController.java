import com.pi4j.io.gpio.*;

import java.net.Socket;

public class UGVController implements Runnable {
    Socket socket;
    StepperMotor stepperCamera;
    StepperMotor stepperTurn;
    Servo cameraServo;
    Encoder encoder;
    DriveMotor driveMotor;
    ImageHandler imageHandler;
    UltraSonicSensor ultraSonicFrontRight;
    UltraSonicSensor ultrasonicFrontLeft;
    UltraSonicSensor ultrasonicBack;
    UltraSonicSensor ultrasonicSide;

    private static final GpioController gpioController = GpioFactory.getInstance();

    // Instance pins for Stepper Motors
    GpioPinDigitalOutput stepperCameraPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03); // Pin 15
    GpioPinDigitalOutput stepperCameraDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02); // Pin 13
    GpioPinDigitalOutput stepperTurnPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_06); // Pin 22
    GpioPinDigitalOutput stepperTurnDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00); // Pin 11

    // Instance pins for DC motor with encoder
    GpioPinDigitalOutput driveMotorPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04); // Pin 16
    GpioPinDigitalInput encoder1A = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_28); // Pin 38
    GpioPinDigitalInput encoder1B = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_29); // Pin 40

    // Instance pins for Servo
    GpioPinDigitalOutput servoPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05); // Pin 18

    // Instance pins for Ultrasonic sensors
    GpioPinDigitalOutput frontRightTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_21); // Pin 29
    GpioPinDigitalInput frontRightEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_DOWN); // Pin 31
    GpioPinDigitalOutput frontLeftTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_23); // Pin 33
    GpioPinDigitalInput frontLeftEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN); // Pin 35
    GpioPinDigitalOutput backTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25); // Pin 37
    GpioPinDigitalInput backEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_26, PinPullResistance.PULL_DOWN); // Pin 32
    GpioPinDigitalOutput sideTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_27); // Pin 36
    GpioPinDigitalInput sideEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);  // Pin 7


    private static final int TEST_STEPS = 4000;

    private enum UGVState {
        IDLE, CIRCLE, CAPTURE, SNIIII;
    }

    private UGVState state;

    public UGVController(Socket socket) {
        this.socket = socket;
        ultraSonicFrontRight = new UltraSonicSensor(frontRightTrig, frontRightEcho);
        ultrasonicFrontLeft = new UltraSonicSensor(frontLeftTrig, frontLeftEcho);
        ultrasonicBack = new UltraSonicSensor(backTrig, backEcho);
        ultrasonicSide = new UltraSonicSensor(sideTrig, sideEcho);
        stepperCamera = new StepperMotor(stepperCameraPul, stepperCameraDir);
        stepperTurn = new StepperMotor(stepperTurnPul, stepperTurnDir);
        encoder = new Encoder(encoder1A, encoder1B);
        driveMotor = new DriveMotor(driveMotorPin);
        cameraServo = new Servo(servoPin);
    }

    public void run() {
      //  try {
            //driveMotor.driveForward(1000, 1550);

            //stepperCamera.moveUp(2000);
            //captureImageAndWait();

            //stepperCamera.moveDown(1500);
            //captureImageAndWait();

            //driveMotor.driveForward(1000, 1550);
            //stepperTurn.turnLeft(250);
            //driveMotor.driveForward(1000, 1550);

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
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
