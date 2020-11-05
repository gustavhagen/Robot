import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class UGVControllerTest implements Runnable {
    Socket socket;
    ImageHandler imageHandler;
    ImageHandlerTest imageHandlerTest;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private volatile boolean autoMode = false;

    private volatile boolean[] wasd;
    private volatile boolean manualMode;

    private AtomicInteger maxSpeed = new AtomicInteger();
    private AtomicInteger totalImages = new AtomicInteger();

    Thread manualDriveThread;
    Thread manualTurnThread;
    Thread imageThread;
    Thread autonomousThread;

    public UGVControllerTest(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
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

                        case "start":
                            if (!manualMode && command.getValue() >= 9) {
                                System.out.println("Started UGV in automatic control...");
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
                                System.out.println("Stop UGV in automatic control...");
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

    private void manualDrive() {
        int speed = 0;
        int counter = 0;

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
            counter++;
            System.out.println("w: " + wasd[0] +", a: "+ wasd[1] + ", s: " + wasd[2] + ",d: " + wasd[3]);
            if (counter > 50) {
                System.out.println("Moving: " + speed);
                counter = 0;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        speed = 0;
    }

    private void manualTurn() {
        int turnPosition = 0;
        int counter = 0;
        boolean left;
        boolean right;

        while (manualMode) {
            left = wasd[1];
            right = wasd[3];

            if (right && !left && turnPosition < 10) {
                turnPosition++;
            }
            if (left && !right && turnPosition > -10) {
                turnPosition--;
            }

            counter++;

            if (counter > 10) {
                System.out.println("Turning: " + turnPosition);
                counter = 0;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        turnPosition = 0;
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
                        System.out.println("Lining up...");
                        Thread.sleep(5000);
                        System.out.println("UGV lined up!");
                        state = 3;
                        break;

                    case 1:
                        System.out.println("Move forward...");
                        Thread.sleep(2000);
                        state = 3;
                        break;

                    case 2:
                        if (direction && level < 2) {
                            System.out.println("Moving elevator up...");
                            Thread.sleep(2500);
                            System.out.println("Elevator positioned!");
                            level++;
                            state = 3;
                        }
                        if (!direction && level > 0) {
                            System.out.println("Moving elevator down...");
                            Thread.sleep(2500);
                            System.out.println("Elevator positioned!");
                            level--;
                            state = 3;
                        }
                        if (level != 1) {
                            direction = !direction;
                            System.out.println("Change direction");
                            state = 3;
                        }
                        break;

                    case 3:
                        if (imageCounter < totalImages) {
                            System.out.println("Capturing image...");
                            Thread.sleep(5000);
                            captureImageAndWait();
                            System.out.println("Image Captured");
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
                        System.out.println("UGV Done capturing images..");
                        System.out.println("Going back to start position.");
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
