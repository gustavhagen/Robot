import sun.java2d.loops.GraphicsPrimitive;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class UGVControllerTest implements Runnable {
    Socket socket;
    ImageHandler imageHandler;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private boolean auto = false;
    private volatile boolean manual = false;

    private volatile boolean turnLeft = false;
    private volatile boolean turnRight = false;
    private volatile boolean driveForward = false;
    private volatile boolean driveBackward = false;

    private int currentState;

    Thread turnThread;

    public UGVControllerTest(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    public void run() {
        turnThread = new Thread();
        turnThread.start();

        try {
            Command command = new Command("UGV", 0, null, null);
            objectOutputStream.writeObject(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Command command = (Command) objectInputStream.readObject();
                System.out.println(command.getCommand());

                if (command.getCommand() != null) {
                    boolean[] wasd = command.getWasd();
                    switch (command.getCommand()) {
                        case "manual":
                            System.out.println("Manual mode...");
                            printDriveDirection(command.getWasd());
                            break;
                        case "start":
                            System.out.println("Started UGV in automatic control...");
                            break;
                        case "stop":
                            System.out.println("Stop UGV in automatic control...");
                            break;
                    }
                }

//                if (command.getCommand().equals("manual") && !auto) {
//                    boolean[] wasd = command.getWasd();
//                    System.out.println("Directions: w = " + wasd[0] + ", a = " + wasd[1] + ", s = " + wasd[2] + ", d = " + wasd[3]);
//                    System.out.println("Speed " + command.getValue());
//                }
//                if (command.getCommand().equals("start") && !auto) {
//                    System.out.println("Start automatic control");
//                    auto = true;
//                }
//                if (command.getCommand().equals("stop") && auto) {
//                    System.out.println("Stop automatic control");
//                    auto = false;
//                }

//                int captureStates = command.getValue();
//                boolean direction = false;
//                switch (command.getCommand()) {
//                    case "sne":         // IDLE
//                        System.out.println("UGV in IDLE-mode. Not driving.");
//                        changeStateTo(1);
//                        break;
//                    case "ani":         // LINE UP
//                        System.out.println("UGV is lining up for object...");
//                        changeStateTo(2);
//                        break;
//                    case "asd":         // CAPTURE
//                        System.out.println("UGV is going to capture 3 images in 3 different heights...");
//                        if (!direction) {
//                            Thread.sleep(500);
//                            captureStates(1);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                            Thread.sleep(1000);
//                            captureStates(2);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                            Thread.sleep(1000);
//                            captureStates(3);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                        }
//                        if (direction) {
//                            Thread.sleep(500);
//                            captureStates(3);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                            Thread.sleep(1000);
//                            captureStates(2);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                            Thread.sleep(1000);
//                            captureStates(1);
//                            Thread.sleep(1500);
//                            captureStates(4);
//                            captureImageAndWait();
//                        }
//                        System.out.println("Done capturing 3 images.");
//                        changeStateTo(3);
//                        break;
//                    case "asdc":          // DRIVE
//                        System.out.println("Continuing circle...");
//                        System.out.println("UGV moves a short distance...");
//                        direction = !direction;
//
//                        break;
//                    case "agava":         // STOP
//                        System.out.println("UGV was stopped by operator.");
//                        break;
//                    default:              // DEFAULT
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } //catch (InterruptedException e) {
           // e.printStackTrace();
        //}
    }

    private void captureStates(int state) {
        switch (state) {
            case 1:
                System.out.println("Moving to lowest capturing point...");
                break;
            case 2:
                System.out.println("Moving to middle capturing point...");
                break;
            case 3:
                System.out.println("Moving to highest capturing point...");
                break;
            case 4:
                System.out.println("Capturing image...");
                break;
        }
    }

    private void printDriveDirection(boolean[] wasd) {
        int turnCounter = 0;
        if (wasd[0]) {
            if (wasd[3]) {
                System.out.println("Both forward and backward keys are pressed...");
            }
            driveForward = true;
            System.out.println("Driving forward...");
        }
        if (wasd[1]) {
            if (wasd[2]) {
                System.out.println("Both left and right keys are pressed...");
            }
            turnLeft = true;
            System.out.println("Turning left...");
        }
        if (wasd[2]) {
            if (wasd[1]) {
                System.out.println("Both left and right keys are pressed...");
            }
            driveBackward = true;
            System.out.println("Driving backward...");
        }
        if (wasd[3]) {
            if (wasd[0]) {
                System.out.println("Both forward and backward keys are pressed...");
            }
            turnRight = true;
            System.out.println("Turning right...");
        }

        while (wasd[1] || wasd[3]) {
            try {
                if (turnCounter >= 0 && turnCounter <= 100) {
                    turnLeft = true;
                    System.out.println(turnCounter);
                }
                turnCounter++;
                Thread.sleep(30);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private int changeStateTo(int newState) {
        currentState = newState;
        return currentState;
    }

    private void captureImageAndWait() {
        imageHandler.captureImage();
        while (imageHandler.isCapturingImage()) {
        }
    }
}
