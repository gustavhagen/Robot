import com.pi4j.io.gpio.*;

public class RunUGV extends Drive {
    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;
    private static GpioPinDigitalOutput ena = null;

    public static void main(String[] args) throws InterruptedException {
        pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        dir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
        ena = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);

        motorAct(4000);
    }
}
