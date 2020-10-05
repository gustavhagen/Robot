import com.pi4j.io.gpio.*;

public class RunUGV {
    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;
    private static GpioPinDigitalOutput ena = null;

    public static void main(String[] args) throws InterruptedException {
        pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        dir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
        ena = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
    }

    // HEI DETTE ER EN TEST
    public static void drive(int steps) throws InterruptedException {
        for (int i = 0; i < steps; i++) {
            pul.high();
            sleepMicro(50);
            pul.low();
            sleepMicro(50);
        }
        Thread.sleep(1000);
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

}
