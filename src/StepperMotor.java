import com.pi4j.io.gpio.*;

/**
 * Class for the stepper motor used in the project. The stepper motors need at least two pins.
 *
 * @author Gustav Sørdal Hagen
 */
public class StepperMotor {
    // The pins needed for the stepper motor to work.
    private GpioPinDigitalOutput pul = null;    // The pin to send the pulse
    private GpioPinDigitalOutput dir = null;    // The direction for the stepper motor

    // Variables used in the class
    private int currentPosition = 0;
    private long lastPulseTime = 0;

    /**
     * The constructor for the stepper motor class. Takes inn two parameters
     * as pins to connect to the Raspberry Pi
     * @param pul The pin to assign for the pulse to send
     * @param dir The pin that decides the direction for the motor
     */
    public StepperMotor(GpioPinDigitalOutput pul, GpioPinDigitalOutput dir) {
        this.pul = pul;
        this.dir = dir;
    }

    /**
     * The motor that makes the stepper motor turn. Uses the pins with
     * a delay. This method is using a delay with nanoseconds.
     * @param steps
     * @param speed
     */
    public void stepperMotorAct(int steps, long speed) {
        // Turning the motor with a direction, as long as current position is less than steps to turn.
        if (currentPosition < steps) {
            dir.high();     // Changing the direction for the motor.
            sleepNano(speed - (System.nanoTime() - lastPulseTime));
            pul.high();
            sleepNano(speed);
            pul.low();
            lastPulseTime = System.nanoTime();
            currentPosition++;
        }
        // Turning the stepper motor with the other direction.
        if (currentPosition > steps) {
            dir.low();
            sleepNano(speed - (System.nanoTime() - lastPulseTime));
            pul.high();
            sleepNano(speed);
            pul.low();
            lastPulseTime = System.nanoTime();
            currentPosition--;
        }
    }

    /**
     * Same method as in the DriveMotor class but here the method
     * waits for a delay in nanoseconds, not microseconds.
     * @param delay The delay to wait, in nanoseconds
     */
    public static void sleepNano(long delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay) >= updatedTime);
    }
}
