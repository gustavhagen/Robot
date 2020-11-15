import com.pi4j.io.gpio.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * This is the class that runs the UGV in manual mode. The user (GUI) sends commands in form of
 * "w", "a", "s" or "d" to drive forward, backwards, left and right. The user also sends commands
 * to move the camera up and down.
 *
 * @author Gustav Sørdal Hagen
 */

public class UGVController implements Runnable {
    private Socket socket;
    private final StepperMotor stepperCamera;
    private final StepperMotor stepperTurn;
    private final DriveMotor driveMotor;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    // The max speed the UGV can drive is made as an Atomic due to thread-safety.
    private final AtomicInteger maxSpeed = new AtomicInteger();

    // Auto and manual booleans made volatile due to thread-safety
    private volatile boolean autoMode = false;
    private volatile boolean manualMode;

    // Volatile boolean array that contains the w, a, s, and d -keys for the driving of the UGV.
    private volatile boolean[] wasd;

    // Creates three threads which is going to do three different tasks at the same time.
    Thread manualDriveThread;
    Thread manualTurnThread;
    Thread manualCameraThread;

    // Java needs to instance a GpioController to start the IO-pins on the Raspberry Pi.
    private static final GpioController gpioController = GpioFactory.getInstance();

    // Instance pins for Stepper Motors as outputs
    GpioPinDigitalOutput stepperCameraPul;
    GpioPinDigitalOutput stepperCameraDir;
    GpioPinDigitalOutput stepperTurnPul;
    GpioPinDigitalOutput stepperTurnDir;

    // Instance pins for DC motor as outputs
    GpioPinDigitalOutput driveMotorPin;

    /**
     * The constructor for the UGVController class. Sets the pins for the components
     * used for the manual drive for the UGV.
     * @param socket The socket which is connected to the server
     * @param objectInputStream The stream that gets data from the server
     * @param objectOutputStream The stream that sends data to the server
     */
    public UGVController(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
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

        // Makes the objects with the given pins to the motors
        driveMotor = new DriveMotor(driveMotorPin);
        stepperTurn = new StepperMotor(stepperTurnPul, stepperTurnDir);
        stepperCamera = new StepperMotor(stepperCameraPul, stepperCameraDir);
    }

    /**
     * Runs the execution for the UGVController. Sends a command telling this is an UGV to the server.
     * Gets a command where the server tells that the UGV are going to drive in manual mode.
     *
     * @throws IOException If an I/O error occurred
     * @throws ClassNotFoundException If no class was found.
     */
    public void run() {
        try {
            Command initCommand = new Command("UGV", 0, null, null);
            objectOutputStream.writeObject(initCommand);    // Tells the server that this is an UGV
            System.out.println(">>> Sent command to user telling this is an UGV!");

            while (true) {
                Command command = (Command) objectInputStream.readObject();  // Gets a command from the server.

                if (command.getCommand() != null) {
                    switch (command.getCommand()) {
                        case "manual":                              // Doing this case if the command is "manual"
                            if (!autoMode) {
                                wasd = command.getWasd();           // Gets the wasd-button which is pressed.
                                maxSpeed.set(command.getValue());   // Sets the speed the GUI tells the UGV to drive in.
                                if (!manualMode) {
                                    System.out.println("Manual mode...");
                                    manualMode = true;

                                    // Creates three threads that are going to run at the same time.
                                    manualDriveThread = new Thread(this::manualDrive);
                                    manualTurnThread = new Thread(this::manualTurn);
                                    manualCameraThread = new Thread(this::manualCamera);

                                    // Starts the threads which was created.
                                    manualTurnThread.start();
                                    manualDriveThread.start();
                                    manualCameraThread.start();
                                }
                            }
                            break;

                        case "manualStop":                     // Stopping the manual drive for the UGV
                            if (!autoMode) {
                                if (manualMode) {
                                    manualDriveThread.interrupt();
                                    manualTurnThread.interrupt();
                                }
                                manualMode = false;
                            }
                            break;

                        case "ping":                            // Ping case for checking connection with server.
                            //System.out.println("Ping from server...");
                            break;

                        default:                                // If the command no one of the other cases.
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

    /**
     * This is the method that drives the DC motor in manual mode.
     * This method uses the setMotorSpeed()-method from the DriveMotor-class.
     */
    private void manualDrive() {
        // Speed to set for the DC Motor
        int speed = 0;

        // Creates four booleans which represents the global boolean array wasd.
        boolean forward;
        boolean backward;
        boolean left;
        boolean right;

        while (manualMode) {
            // Sets the four booleans to indexes for the global boolean array wasd.
            forward = wasd[0];
            left = wasd[1];
            backward = wasd[2];
            right = wasd[3];

            if (!right || !left) {  // As long as no one of the left/right keys are pressed
                // If only the forward-button, "w", is pressed and the speed is below the max speed
                // Positive speed means the motor is driving forwards.
                if (forward && !backward && speed < maxSpeed.get()) {
                    speed++;
                }
                // If only the backward-button, "s", is pressed and the speed is greater than negative max speed.
                // Negative speed means the motor is driving backwards.
                if (backward && !forward && speed > -maxSpeed.get()) {
                    speed--;
                }
                // Slows down if the motor is driving forwards and the forward-button is released.
                if ((!forward) && (speed > 0)) {
                    speed--;
                }
                // Slows down if the motor is driving backwards and the backwards-button is released.
                if ((!backward) && (speed < 0)) {
                    speed++;
                }
                // Decrease the speed if the the speed is above the speed limit.
                if (speed > maxSpeed.get()) {
                    speed--;
                }
                // Increase the speed if the speed is below the backward-speed limit
                if (speed < -maxSpeed.get()) {
                    speed++;
                }
                // Sets the speed to the DC Motor using the setMotorSpeed() from DriveMotor-class.
                System.out.println("W: " + wasd[0] + ", A: " + wasd[1] + ", S: " + wasd[2] + ", D: " + wasd[3]);
                driveMotor.setMotorSpeed(speed);
            }
        }
        speed = 0;
    }

    /**
     * Method for turning the turn stepper motor in manual mode.
     * Uses the stepperMotorAct()-method from the StepperMotor class
     */
    private void manualTurn() {
        // Creates two variables for the position for the turning.
        int turnPosition = 0;
        int maxTurnPosition = 500;

        // Creates two booleans which represents the global boolean array wasd.
        boolean left;
        boolean right;

        // Variables for setting the speed for the stepper motor.
        long speed = 50000; // Speed can be between 10 000 and 100 000. 100 000 is slowest and 10 000 is fastest.
        long newTime = 0;
        long refreshDelay = 1000000;    // Nanoseconds

        while (manualMode) {
            // Sets the two booleans to indexes for the global boolean array wasd.
            left = wasd[1];
            right = wasd[3];

            if (System.nanoTime() >= newTime) {
                // If only the right-button, "d", is pressed and turn position is below the max position.
                if (right && !left && turnPosition < maxTurnPosition) {
                    turnPosition++;
                }
                // If only the left-button, "s", is pressed and turn position is above the negative max position
                if (left && !right && turnPosition > -maxTurnPosition) {
                    turnPosition--;
                }
                newTime = System.nanoTime() + refreshDelay;
            }
            // Sets the position and the speed to the stepper motor.
            stepperTurn.stepperMotorAct(turnPosition, speed);
        }
        turnPosition = 0;
    }

    /**
     * Method for moving the camera stepper motor in manual mode.
     * Uses the stepperMotorAct()-method from the StepperMotor class.
     * The stepper motor for the camera moves if the user are pressing "w" or "s" if and only if
     * the buttons "a" AND "d" are pressed.
     */
    private void manualCamera() {
        // Variables for the height the camera is in.
        int height = 0;
        int maxHeight = 20000;      // Steps

        // Creates four booleans which represents the global boolean array wasd.
        boolean up;
        boolean down;
        boolean left;
        boolean right;

        // Variables for setting the speed for the stepper motor.
        long speed = 50000; // Speed can be between 10 and 100. 100 is slowest and 10 is fastest.
        long newTime = 0;
        long refreshDelay = 1000000;    // Nanoseconds

        while (manualMode) {
            // Sets the four booleans to indexes for the global boolean array wasd.
            up = wasd[0];
            left = wasd[1];
            down = wasd[2];
            right = wasd[3];

            // Moves camera up and down only ont the left and right button are pressed.
            if (left && right) {
                if (System.nanoTime() >= newTime) {
                    // If only the up-button, "w", is pressed and height is below the max height.
                    if (up && !down && height < maxHeight) {
                        height++;
                    }
                    // If only the down-button, "s", is pressed and height is below the max height.
                    if (down && !up && height > -maxHeight) {
                        height--;
                    }
                    newTime = System.nanoTime() + refreshDelay;
                }
            }
            // Sets the height and the speed to the stepperMotorAct()-method in the StepperMotor-class
            stepperCamera.stepperMotorAct(height, speed);
        }
        height = 0;
    }
}
