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

    private int currentState;

    public UGVControllerTest(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
    }

    public void run() {
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

                if (command.getCommand().equals("directions") && !auto) {
                    boolean[] wasd = command.getWasd();
                    System.out.println("Directions: w = " + wasd[0] + ", a = " + wasd[1] + ", s = " + wasd[2] + ", d = " + wasd[3]);
                    System.out.println("Speed " + command.getValue());
                }
                if (command.getCommand().equals("start") && !auto) {
                    System.out.println("Start automatic control");
                    auto = true;
                }
                if (command.getCommand().equals("stop") && auto) {
                    System.out.println("Stop automatic control");
                    auto = false;
                }

                int captureStates = command.getValue();
                boolean direction = false;
                switch ("") {
                    case "sne":         // IDLE
                        System.out.println("UGV in IDLE-mode. Not driving.");
                        changeStateTo(1);
                        break;
                    case "ani":         // LINE UP
                        System.out.println("UGV is lining up for object...");
                        changeStateTo(2);
                        break;
                    case "asd":         // CAPTURE
                        System.out.println("UGV is going to capture 3 images in 3 different heights...");
                        if (!direction) {
                            Thread.sleep(500);
                            captureStates(1);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                            Thread.sleep(1000);
                            captureStates(2);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                            Thread.sleep(1000);
                            captureStates(3);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                        }
                        if (direction) {
                            Thread.sleep(500);
                            captureStates(3);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                            Thread.sleep(1000);
                            captureStates(2);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                            Thread.sleep(1000);
                            captureStates(1);
                            Thread.sleep(1500);
                            captureStates(4);
                            captureImageAndWait();
                        }
                        System.out.println("Done capturing 3 images.");
                        changeStateTo(3);
                        break;
                    case "asdc":          // DRIVE
                        System.out.println("Continuing circle...");
                        System.out.println("UGV moves a short distance...");
                        direction = !direction;

                        break;
                    case "agava":         // STOP
                        System.out.println("UGV was stopped by operator.");
                        break;
                    default:              // DEFAULT
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
