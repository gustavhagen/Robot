import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class UGVController implements Runnable {
    private Socket socket;
    //private StepperMotor stepperCamera;
    //private StepperMotor stepperTurn;
    private Servo cameraServo;
    private Encoder encoder;
    private DriveMotor driveMotor;
    private ImageHandler imageHandler;
    private UltraSonicSensor ultraSonicFrontRight;
    private UltraSonicSensor ultrasonicFrontLeft;
    private UltraSonicSensor ultrasonicBack;
    private UltraSonicSensor ultrasonicSide;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private AtomicInteger maxSpeed = new AtomicInteger();

    private volatile boolean autoMode = false;

    private volatile boolean[] wasd;
    private volatile boolean manualMode;

    Thread manualDriveThread;
    Thread manualTurnThread;
    Thread manualCameraThread;

    private static final GpioController gpioController = GpioFactory.getInstance();

    // Instance pins for Stepper Motors
    GpioPinDigitalOutput stepperCameraPul;
    GpioPinDigitalOutput stepperCameraDir;
    GpioPinDigitalOutput stepperTurnPul;
    GpioPinDigitalOutput stepperTurnDir;

    // Instance pins for DC motor with encoder
    GpioPinDigitalOutput driveMotorPin;
    GpioPinDigitalInput encoder1A;
    GpioPinDigitalInput encoder1B;

    // Instance pins for Servo
    GpioPinDigitalOutput servoPin;

    // Instance pins for Ultrasonic sensors
    GpioPinDigitalOutput frontRightTrig;
    GpioPinDigitalInput frontRightEcho;
    GpioPinDigitalOutput frontLeftTrig;
    GpioPinDigitalInput frontLeftEcho;
    GpioPinDigitalOutput backTrig;
    GpioPinDigitalInput backEcho;
    GpioPinDigitalOutput sideTrig;
    GpioPinDigitalInput sideEcho;


    private static final int TEST_STEPS = 4000;

    public UGVController(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;


        // Instance pins for Stepper Motors
        try {
            stepperCameraPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03); // Pin 15
            Thread.sleep(10);
            stepperCameraDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02); // Pin 13
            Thread.sleep(10);
            stepperTurnPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_06); // Pin 22
            Thread.sleep(10);
            stepperTurnDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00); // Pin 11
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Instance pins for DC motor with encoder
        driveMotorPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04); // Pin 16
        encoder1A = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_28); // Pin 38
        encoder1B = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_29); // Pin 40

        // Instance pins for Servo
        servoPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05); // Pin 18

        // Instance pins for Ultrasonic sensors
        frontRightTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_21); // Pin 29
        frontRightEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_22, PinPullResistance.PULL_DOWN); // Pin 31
        frontLeftTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_23); // Pin 33
        frontLeftEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_24, PinPullResistance.PULL_DOWN); // Pin 35
        backTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_25); // Pin 37
        backEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_26, PinPullResistance.PULL_DOWN); // Pin 32
        sideTrig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_27); // Pin 36
        sideEcho = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);  // Pin 7

        ultraSonicFrontRight = new UltraSonicSensor(frontRightTrig, frontRightEcho);
        ultrasonicFrontLeft = new UltraSonicSensor(frontLeftTrig, frontLeftEcho);
        ultrasonicBack = new UltraSonicSensor(backTrig, backEcho);
        ultrasonicSide = new UltraSonicSensor(sideTrig, sideEcho);
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
                                    manualCameraThread = new Thread(this::manualCamera);
                                    manualTurnThread.start();
                                    manualDriveThread.start();
                                    manualCameraThread.start();
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
        boolean left;
        boolean right;

        while (manualMode) {
            forward = wasd[0];
            left = wasd[1];
            backward = wasd[2];
            right = wasd[3];

            if (!right || !left) {
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
            }
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
        StepperMotor stepperTurn = new StepperMotor(stepperTurnPul, stepperTurnDir);
        int turnPosition = 0;
        int maxTurnPosition = 500;
        //int counter = 0;
        boolean left;
        boolean right;

        long speed = 50000; // Speed can be between 10 and 100. 100 is slowest and 10 is fastest.
        long newTime = 0;
        long refreshDelay = 1000000;

        while (manualMode) {
            left = wasd[1];
            right = wasd[3];


            if (System.nanoTime() >= newTime) {
                if (right && !left && turnPosition < maxTurnPosition) {
                    turnPosition++;
                }
                if (left && !right && turnPosition > -maxTurnPosition) {
                    turnPosition--;
                } //if(!left && !right && turnPosition > 0){
                //turnPosition--;
                //}if(!left && !right && turnPosition < 0){
                //    turnPosition++;
                //}
                newTime = System.nanoTime() + refreshDelay;
                //System.out.println(turnPosition);
            }
            stepperTurn.stepperMotorAct(turnPosition, speed);

            //counter++;

            //if (counter > 10) {
            //    System.out.println("Turning: " + turnPosition);
            //    counter = 0;
            //}
        }
        turnPosition = 0;
    }

    private void manualCamera() {
        StepperMotor stepperCamera = new StepperMotor(stepperCameraPul, stepperCameraDir);
        int height = 0;
        int maxHeight = 20000;
        //int counter = 0;
        boolean up;
        boolean down;
        boolean left;
        boolean right;

        long speed = 50000; // Speed can be between 10 and 100. 100 is slowest and 10 is fastest.
        long newTime = 0;
        long refreshDelay = 1000000;

        while (manualMode) {
            up = wasd[0];
            left = wasd[1];
            down = wasd[2];
            right = wasd[3];

            if (left && right) {
                if (System.nanoTime() >= newTime) {
                    if (up && !down && height < maxHeight) {
                        height++;
                    }
                    if (down && !up && height > -maxHeight) {
                        height--;
                    } //if(!left && !right && turnPosition > 0){
                    //turnPosition--;
                    //}if(!left && !right && turnPosition < 0){
                    //    turnPosition++;
                    //}
                    newTime = System.nanoTime() + refreshDelay;
                }
            }
            stepperCamera.stepperMotorAct(height, speed);
        }
        height = 0;
    }


    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
