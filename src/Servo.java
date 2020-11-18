import com.pi4j.io.gpio.*;

/**
 * This class makes the servo for the camera to work.
 * The logic behind the servo is based on the DriveMotor class.
 *
 * @author Gustav Sørdal Hagen
 */
public class Servo {
    // Pin for the servo.
    private static GpioPinDigitalOutput servoPin;

    /**
     * The constructor for the Servo-class.
     *
     * @param servoPin The pin assigned to the Raspberry Pi.
     */
    public Servo(GpioPinDigitalOutput servoPin) {
        this.servoPin = servoPin;
    }

    /**
     * Makes the servo turn.
     *
     * @param steps      The amout of steps the servo are going to turn
     * @param pulseWidth The pulse width for the servo.
     */
    private void servoAct(int steps, int pulseWidth) {
        for (int i = 0; i < steps; i++) {
            servoPin.high();
            sleepMicro(pulseWidth);
            servoPin.low();
            sleepMicro(20000 - pulseWidth);
        }
    }

    /**
     * A method that works ass a delay. Using the system-time for the operating system
     * for the computer/Raspberry Pi.
     *
     * @param delay The delay to set in microseconds.
     */
    private static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);  // * 1000 to get the delay in microseconds
    }
}
