import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

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

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private AtomicInteger maxSpeed = new AtomicInteger();

    private volatile boolean autoMode = false;

    private volatile boolean[] wasd;
    private volatile boolean manualMode;

    Thread manualDriveThread;
    Thread manualTurnThread;

    private static final GpioController gpioController = GpioFactory.getInstance();

    GpioPinAnalogInput input = gpioController.provisionAnalogInputPin(RaspiPin.GPIO_08);


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

    public UGVController(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;

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
        try {
            Command initCommand = new Command("UGV", 0, null, null);
            objectOutputStream.writeObject(initCommand);

            while (true) {
                Command command = (Command) objectInputStream.readObject();

                if (command.getCommand() != null) {
                    switch (command.getCommand()) {

                        case "manual":
                            if (!autoMode) {
                                wasd = command.getWasd();
                                maxSpeed.set(command.getValue());
                                if (!manualMode) {
                                    System.out.println("Manual mode...");
                                    manualMode = true;
                                    manualDriveThread = new Thread(this::manualDrive);
                                    manualTurnThread = new Thread(this::manualTurn);
                                    manualTurnThread.start();
                                    manualDriveThread.start();
                                }
                            }
                            break;

                        case "manualStop":
                            if (!autoMode) {
                                if (manualMode) {
//                                    manualDriveThread.interrupt();
//                                    manualTurnThread.interrupt();
                                }
                                manualMode = false;
                            }
                            break;

                        case "ping":
                            //System.out.println("Ping from server...");
                            break;

                        default:
                            System.out.println("Wrong command!");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } //catch (InterruptedException e) {
        // e.printStackTrace();
        //}
    }


    private void manualDrive() {
        int speed = 0;
        //int counter = 0;

        boolean forward;
        boolean backward;

        while (manualMode) {
            forward = wasd[0];
            backward = wasd[2];


            if (forward && !backward && speed < maxSpeed.get()) {
                speed++;
            }
            if (backward && !forward && speed > -maxSpeed.get()) {
                speed--;
            }
            if ((!forward) && (speed > 0)) {
                speed--;
            }
            if ((!backward) && (speed < 0)) {
                speed++;
            }
            if (speed > maxSpeed.get()) {
                speed--;
            }
            if (speed < -maxSpeed.get()) {
                speed++;
            }
            driveMotor.setMotorSpeed(speed);
            //counter++;
            //System.out.println("w: " + wasd[0] + ", a: " + wasd[1] + ", s: " + wasd[2] + ", d: " + wasd[3]);
            //if (counter > 50) {
            //    System.out.println("Moving: " + speed);
            //    counter = 0;
            //}
        }
        speed = 0;
    }

    private void manualTurn() {
        int turnPosition = 0;
        int maxTurnPosition = 500;
        //int counter = 0;
        boolean left;
        boolean right;

        int speed = 100; // Speed can be between 10 and 100. 100 is slowest and 10 is fastest.

        while (manualMode) {
            left = wasd[1];
            right = wasd[3];

            if (right && !left && turnPosition < maxTurnPosition) {
                turnPosition++;
                stepperTurn.stepperMotorAct(turnPosition, speed);
            }
            if (left && !right && turnPosition > -maxTurnPosition) {
                turnPosition--;
                stepperTurn.stepperMotorAct(turnPosition, speed);
            } if(!left && !right && turnPosition > 0){
                turnPosition--;
            }if(!left && !right && turnPosition < 0){
                turnPosition++;
            }
            //counter++;

            //if (counter > 10) {
            //    System.out.println("Turning: " + turnPosition);
            //    counter = 0;
            //}
        }
        turnPosition = 0;
    }

    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
