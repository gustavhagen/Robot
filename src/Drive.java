import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class Drive {

    public static void main(String[] args) {
        final GpioController gipo = GpioFactory.getInstance();

        final GpioPinDigitalOutput[] pins = {
                gipo.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW),
                gipo.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW),
        };

        gipo.setShutdownOptions(true, PinState.LOW, pins);
        GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);



    }
}
