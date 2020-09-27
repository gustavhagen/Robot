import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class Drive {

    public static void main(String[] args) {
        final GpioController gpioController = GpioFactory.getInstance();

        final GpioPinDigitalOutput[] pins = {
                gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW),
                gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW),
        };

        gpioController.setShutdownOptions(true, PinState.LOW, pins);
        GpioStepperMotorComponent stepperMotor = new GpioStepperMotorComponent(pins);
    }
}
