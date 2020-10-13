import com.pi4j.io.gpio.*;

public class CameraElevator {

    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;
    private static GpioPinDigitalOutput ena = null;

    public CameraElevator() {
        pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        dir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
        ena = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
    }

    public static void motorAct(int steps) throws InterruptedException {
        for (int i = 0; i < steps; i++) {
            pul.high();
            sleepMicro(50);
            pul.low();
            sleepMicro(50);
        }
    }

    public void moveUp(int steps) throws InterruptedException {
        motorAct(steps);
    }

    public void moveDown(int steps) throws InterruptedException {
        dir.high();
        motorAct(steps);
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

    public void setElevatorHeight(int height) {
    }
}