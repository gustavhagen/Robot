import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class UGVController implements Runnable {
    private Socket socket;
    private StepperMotor stepperCamera;
    private StepperMotor stepperTurn;
    private DriveMotor driveMotor;
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


    public UGVController(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;

        // Instance pins for Stepper Motors
        stepperCameraPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03); // Pin 15
        stepperCameraDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02); // Pin 13
        stepperTurnPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_06); // Pin 22
        stepperTurnDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00); // Pin 11

        // Instance pins for DC motor with encoder
        driveMotorPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04); // Pin 16

        driveMotor = new DriveMotor(driveMotorPin);
        stepperTurn = new StepperMotor(stepperTurnPul, stepperTurnDir);
        stepperCamera = new StepperMotor(stepperCameraPul, stepperCameraDir);
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
        }
    }

    private void manualDrive() {
        int speed = 0;

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
        }
        speed = 0;
    }

    private void manualTurn() {
        int turnPosition = 0;
        int maxTurnPosition = 500;

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
                }
                newTime = System.nanoTime() + refreshDelay;
            }
            stepperTurn.stepperMotorAct(turnPosition, speed);
        }
        turnPosition = 0;
    }

    private void manualCamera() {
        int height = 0;
        int maxHeight = 20000;

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
                    }
                    newTime = System.nanoTime() + refreshDelay;
                }
            }
            stepperCamera.stepperMotorAct(height, speed);
        }
        height = 0;
    }
}
