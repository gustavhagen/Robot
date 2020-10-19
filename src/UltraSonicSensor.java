import com.pi4j.io.gpio.*;

public class UltraSonicSensor {
    private static final long TIMEOUT = 23200;
    private static final double DISTANCE_CONSTANT = 17014e-9;
    private GpioPinDigitalOutput trig = null;
    private GpioPinDigitalInput echo = null;

    public UltraSonicSensor(GpioPinDigitalOutput trig, GpioPinDigitalInput echo) {
        this.trig = trig;
        this.echo = echo;
    }

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

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }
}
