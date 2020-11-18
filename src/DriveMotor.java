import com.pi4j.io.gpio.*;

/**
 * This class is for make the DC motor work. The motor works by pusle width-modulation.
 *
 * @author Gustav Sørdal Hagen
 */
public class DriveMotor {
    // The pin needed for the DC motor to work.
    private final GpioPinDigitalOutput pwm;

    /**
     * The Constructor for the DC motor class.
     *
     * @param pwm The GPIO-pin to connect on the Raspberry Pi
     */
    public DriveMotor(GpioPinDigitalOutput pwm) {
        this.pwm = pwm;
    }

    /**
     * Method that makes the DC motor to start. The pin assigned to the class
     * is set high and low rapidly, with a little delay. Optional pulse width for
     * this DC motor is 20 microseconds pr pulse.
     *
     * @param speed The speed to set to the motor.
     */
    public void setMotorSpeed(int speed) {
        int pulseWidth = 1450 + -speed * 5;       // -speed because speed can be negative.
        pwm.high();
        sleepMicro(pulseWidth);
        pwm.low();
        // A little delay after pin is set low for getting a pulse width.
        sleepMicro(20000 - pulseWidth);
    }

    /**
     * A method that works ass a delay. Using the system-time for the operating system
     * for the computer/Raspberry Pi.
     *
     * @param delay The delay to set in microseconds.
     */
    private static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);  // * 1000 to get the delay in microseconds
    }
}
