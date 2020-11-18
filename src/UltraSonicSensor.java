import com.pi4j.io.gpio.*;

/**
 * Class for the Ultrasonic sensors used in the project.
 *
 * @author Gustav Sørdal Hagen
 */
public class UltraSonicSensor {
    // Variables and pins used for the Ultrasonic sensors
    private static final long TIMEOUT = 23200;   // If an pulse not is back after being sent in this time in microsec.
    private static final double DISTANCE_CONSTANT = 17014e-9;   // Constant for measure the distance to the object.

    // Pins to connect the Raspberry Pi
    private GpioPinDigitalOutput trig = null;
    private GpioPinDigitalInput echo = null;

    /**
     * The constructor for the Ultrasonic Sensor class.
     *
     * @param trig The pin for trigging a pulse.
     * @param echo The pin for the echo for the trigged pulse.
     */
    public UltraSonicSensor(GpioPinDigitalOutput trig, GpioPinDigitalInput echo) {
        this.trig = trig;
        this.echo = echo;
    }

    /**
     * Method that sets the trigger high and low, returns the distance to the object.
     *
     * @return The distance to the object in centimeters (cm)
     */
    public double getDistance() {
        trig.high();
        sleepMicro(10);
        trig.low();

        long timeOut = System.nanoTime() + TIMEOUT * 1000;

        while (echo.isLow()) {
            if (System.nanoTime() > timeOut) {
                return -1;
            }
        } //Wait until the ECHO pin gets HIGH

        long startTime = System.nanoTime();
        while (echo.isHigh()) {
        } // Wait until the ECHO pin gets LOW

        return (System.nanoTime() - startTime) * DISTANCE_CONSTANT;
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
