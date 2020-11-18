import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the class that simulates the autonomous execution for the UGV.
 * The class receives a command "start" from the server and the simulation starts automatically.
 * This class also contains image capturing.
 *
 * @author Gustav Sørdal Hagen
 */

public class UGVSimulator implements Runnable {
    private final Socket socket;
    private ImageHandler imageHandler;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    // Volatile varibales used in the run()-method
    private volatile boolean autoMode = false;
    private volatile boolean[] wasd;
    private volatile boolean manualMode;

    // Total images the user want the UGV to take. Atomic for thread-safety.
    private final AtomicInteger totalImages = new AtomicInteger();

    // The max speed the UGV can drive is made as an Atomic due to thread-safety.
    private final AtomicInteger maxSpeed = new AtomicInteger();

    // Two threads that are running in the run()-method
    Thread imageThread;
    Thread autonomousThread;
    Thread manualDriveThread;
    Thread manualCameraThread;
    Thread manualTurnThread;

    /**
     * @param socket             The socket that is connected to the server.
     * @param objectOutputStream The stream that sends data from the simulator to the server.
     * @param objectInputStream  The stream that receives data from the server.
     * @throws IOException            If an I/O error occurred
     * @throws ClassNotFoundException If no class was found.
     */
    public UGVSimulator(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    public void run() {
        try {
            // Makes a command object that tells the server that this is an UGV.
            Command initCommand = new Command("UGV", 0, null, null);
            objectOutputStream.writeObject(initCommand);
            System.out.println(">>> Sent command to user telling this is an UGV!");


            while (true) {
                // UGV receives commands continuous from the server.
                Command command = (Command) objectInputStream.readObject();

                // Checks if the command from the server not equals null
                if (command.getCommand() != null) {
                    switch (command.getCommand()) {

                        case "start":                   // If command is "start", UGV is doing this state
                            // Checks if UGV is in manual mode, and total images is less or equals to 9, cna be edited.
                            if (!manualMode && command.getValue() >= 9) {
                                System.out.println("[UGV] Started UGV in automatic control...");
                                autoMode = true;
                                totalImages.set(command.getValue());        // Sets the total images to the value from the command

                                // Creates an imageHandler to take picture, and creates a Thread for it.
                                imageHandler = new ImageHandler(socket, command.getValue(), objectOutputStream);
                                imageThread = new Thread(imageHandler);
                                imageThread.start();
                                // Creates an Thread that is driving the UGV in autonomous mode.
                                autonomousThread = new Thread(this::autonomousDrive);
                                autonomousThread.start();
                                //Thread.sleep(60000);
                            }
                            break;

                        case "stop":                    // Stops the UGV in autonomous mode.
                            if (!manualMode) {
                                System.out.println("[UGV] Stopped UGV in automatic control...");
                                autoMode = false;
                                totalImages.set(0);
                                imageHandler.stopThread();      // Stops the imageThread.
//                                imageThread.interrupt();
//                                autonomousThread.interrupt();
                            }
                            break;

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
//                                    manualDriveThread.interrupt();
//                                    manualTurnThread.interrupt();
                                }
                                manualMode = false;
                            }
                            break;

                        case "ping":            // Ping from the server for checking connection.
                            //System.out.println("Ping from server...");
                            break;

                        default:                // Prints "Wrong command!" if the command is not equal to any of the cases.
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

    /**
     * Simulates the UGV when the UGV shall run in autonomous mode.
     * Contains a switch-case of different tasks the UGV are going to do.
     *
     * @throws InterruptedException If Thread.sleep() was interrupted
     */
    private void autonomousDrive() {
        try {
            // Creates some variables for this method
            int state = 0;
            int level = 0;
            int imageCounter = 0;
            int totalImages = this.totalImages.get();        // Uses the atomic variable totalImages.
            boolean direction = true;
            boolean lastDirection = true;

            // This while-loop runs when the UGV is in autonomous mode.
            while (autoMode) {
                switch (state) {

                    case 0:         //  Simulates that the UGV is lining up for the object to scan
                        System.out.println("[UGV] Lining up...");
                        Thread.sleep(5000);
                        System.out.println("[UGV] lined up!");
                        state = 3;      // Switches to the state where the UGV is capturing an image
                        break;

                    case 1:     // Simulates that the UGV is driving forward/around the object
                        System.out.println("[UGV] Moving forward...");
                        Thread.sleep(2000);
                        state = 3;
                        break;

                    case 2:     // Simulates that the camera is moving up/down
                        // Moves the camera up and capturing images
                        if (direction && level < 2) {
                            System.out.println("[UGV] Moving camera up...");
                            Thread.sleep(2500);
                            System.out.println("[UGV] Elevator positioned!");
                            level++;
                            state = 3;
                        }
                        // Moves the camera down and capturing images
                        if (!direction && level > 0) {
                            System.out.println("[UGV] Moving camera down...");
                            Thread.sleep(2500);
                            System.out.println("[UGV] Elevator positioned!");
                            level--;
                            state = 3;
                        }
                        // Changes the direction of the camera elevator
                        if (level != 1) {
                            direction = !direction;
                            System.out.println("[UGV] Changed direction on the camera elevator!");
                            state = 3;
                        }
                        break;

                    case 3:    // Capturing images
                        if (imageCounter < totalImages) {       // If the amount of captured images is below total images to capture.
                            System.out.println("[UGV] Capturing image...");
                            Thread.sleep(5000);
                            captureImageAndWait();          // Using this method to capture images.
                            System.out.println("[UGV] Image Captured!");
                            System.out.println(">>> Sent Image to server!");
                        }
                        imageCounter++;

                        // When down capturing images, switches to the last case.
                        if (imageCounter >= totalImages) {
                            state = 4;
                        }
                        // Going back to where the UGV is moving the camera up/down
                        else {
                            state = 2;
                        }
                        // If the camera has reached the bottom or the top.
                        if (direction != lastDirection) {
                            state = 1;
                        }
                        lastDirection = direction;
                        break;

                    // The UGV is done capturing images and is returning back to the position it started on.
                    case 4:
                        this.totalImages.set(0);
                        System.out.println("[UGV] Done capturing images...");
                        System.out.println("[UGV] Going back to start position!");
                        Thread.sleep(6000);
                        System.out.println("UGV DONE!!");
                        autoMode = false;
                        break;

                    // Wrong command from the server.
                    default:
                        System.out.println("Wrong state value");
                        break;

                }
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
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
                System.out.println("Driving with speed: " + speed);
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
        long refreshDelay = 1000000000;    // Nanoseconds

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
                System.out.println("Turning: " + turnPosition);
            }
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
        long refreshDelay = 1000000000;    // Nanoseconds

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
                    System.out.println("Camera height: " + height);
                }
            }
        }
        height = 0;
    }

    /**
     * Capturing an image using the imageHandler Class.
     */
    private void captureImageAndWait() {
        imageHandler.captureImage();
        // UGV waits as the image is being capture
        while (imageHandler.isCapturingImage()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
