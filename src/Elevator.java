import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.*;

public class Elevator {

    private static final int SLEEP_TIME = 2000;
    private static final int STEPPER_MOTOR_STEPS = 5000;
    private static final int STEPPER_MOTOR_REVOLUTIONS = 2;
    private static final int STEPPER_MOTOR_FORWARD_STEPS = 5000;
    private static final int STEPPER_MOTOR_FORWARD_MILLISECONDS = 2000;

    public Elevator() {
    }

    public static void main(String[] args) throws InterruptedException {
        final GpioController gpioController = GpioFactory.getInstance();

        final GpioPinDigitalOutput[] pins = {
                gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW),
                gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW),
        };

        gpioController.setShutdownOptions(true, PinState.LOW, pins);
        GpioStepperMotorComponent stepperMotor = new GpioStepperMotorComponent(pins);


        stepperMotor.setStepsPerRevolution(STEPPER_MOTOR_STEPS);

        System.out.println("--STEPPER MOTOR-- FORWARD 5000 steps...");
        stepperMotor.step(STEPPER_MOTOR_FORWARD_STEPS);
        System.out.println("--STEPPER MOTOR-- SLEEP 2 sec...");
        Thread.sleep(SLEEP_TIME);

        System.out.println("--STEPPER MOTOR-- FORWARD 2 revolutions...");
        stepperMotor.rotate(STEPPER_MOTOR_REVOLUTIONS);
        System.out.println("--STEPPER MOTOR-- SLEEP 2 sec...");
        Thread.sleep(SLEEP_TIME);

        System.out.println("--STEPPER MOTOR-- FORWARD 2 sec...");
        stepperMotor.forward(STEPPER_MOTOR_FORWARD_MILLISECONDS);
        System.out.println("--STEPPER MOTOR-- SLEEP 2 sec...");
        Thread.sleep(SLEEP_TIME);

        System.out.println("--STEPPER MOTOR-- STOPPED...");
        stepperMotor.stop();


    }

    public void setElevatorHeight(int height) {
    }
}
