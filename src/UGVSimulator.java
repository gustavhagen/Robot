import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the class that simulates the autonomous execution for the UGV.
 * The class receives a command "start" from the server and the simulation starts automatically.
 * This class also contains image capturing.
 * @author Gustav Sørdal Hagen
 */

public class UGVSimulator implements Runnable {
    Socket socket;
    ImageHandler imageHandler;
    ImageHandlerTest imageHandlerTest;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    // Volatile varibales used in the run()-method
    private volatile boolean autoMode = false;
    private volatile boolean[] wasd;
    private volatile boolean manualMode;

    // Total images the user want the UGV to take. Atomic for thread-safety.
    private AtomicInteger totalImages = new AtomicInteger();

    // Two threads that are running in the run()-method
    Thread imageThread;
    Thread autonomousThread;

    /**
     *
     * @param socket The socket that is connected to the server.
     * @param objectOutputStream The stream that sends data from the simulator to the server.
     * @param objectInputStream The stream that receives data from the server.
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

                        case "start":
                            if (!manualMode && command.getValue() >= 9) {
                                System.out.println("[UGV] Started UGV in automatic control...");
                                autoMode = true;
                                totalImages.set(command.getValue());

                                imageHandlerTest = new ImageHandlerTest(socket, command.getValue(), objectOutputStream);
                                imageThread = new Thread(imageHandlerTest);
                                imageThread.start();
                                autonomousThread = new Thread(this::autonomousDrive);
                                autonomousThread.start();
                                //Thread.sleep(60000);
                            }
                            break;

                        case "stop":
                            if (!manualMode) {
                                System.out.println("[UGV] Stopped UGV in automatic control...");
                                autoMode = false;
                                totalImages.set(0);
                                imageHandlerTest.stopThread();
//                                imageThread.interrupt();
//                                autonomousThread.interrupt();
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

    private void autonomousDrive() {
        try {
            int state = 0;
            int level = 0;
            int imageCounter = 0;
            int totalImages = this.totalImages.get();
            boolean direction = true;
            boolean lastDirection = true;

            while (autoMode) {
                switch (state) {
                    case 0:
                        System.out.println("[UGV] Lining up...");
                        Thread.sleep(5000);
                        System.out.println("[UGV] lined up!");
                        state = 3;
                        break;

                    case 1:
                        System.out.println("[UGV] Moving forward...");
                        Thread.sleep(2000);
                        state = 3;
                        break;

                    case 2:
                        if (direction && level < 2) {
                            System.out.println("[UGV] Moving camera up...");
                            Thread.sleep(2500);
                            System.out.println("[UGV] Elevator positioned!");
                            level++;
                            state = 3;
                        }
                        if (!direction && level > 0) {
                            System.out.println("[UGV] Moving camera down...");
                            Thread.sleep(2500);
                            System.out.println("[UGV] Elevator positioned!");
                            level--;
                            state = 3;
                        }
                        if (level != 1) {
                            direction = !direction;
                            System.out.println("[UGV] Changed direction on the camera elevator!");
                            state = 3;
                        }
                        break;

                    case 3:
                        if (imageCounter < totalImages) {
                            System.out.println("[UGV] Capturing image...");
                            Thread.sleep(5000);
                            captureImageAndWait();
                            System.out.println("[UGV] Image Captured!");
                            System.out.println(">>> Sent Image to server!");
                        }
                        imageCounter++;

                        if (imageCounter >= totalImages) {
                            state = 4;
                        } else {
                            state = 2;
                        }
                        if (direction != lastDirection) {
                            state = 1;
                        }
                        lastDirection = direction;
                        break;

                    case 4:
                        this.totalImages.set(0);
                        System.out.println("[UGV] Done capturing images...");
                        System.out.println("[UGV] Going back to start position!");
                        Thread.sleep(6000);
                        System.out.println("UGV DONE!!");
                        autoMode = false;
                        break;

                    default:
                        System.out.println("Wrong state value");
                        break;

                }
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private void captureImageAndWait() {
        imageHandlerTest.captureImage();
        while (imageHandlerTest.isCapturingImage()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
