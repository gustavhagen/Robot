import com.pi4j.io.gpio.*;

public class UltraSonicSensor {
    private static final long TIMEOUT = 23200;
    private static final double DISTANCE_CONSTANT = 170140e-9;
    private static GpioPinDigitalOutput trig = null;
    private static GpioPinDigitalInput echo = null;

    public UltraSonicSensor(GpioPinDigitalOutput trig, GpioPinDigitalInput echo) {
        this.trig = trig;
        this.echo = echo;
    }

    public static void main(String[] args) {
        while (true) {
        System.out.println("Distance: " + getDistance() + " cm");
        sleepMicro(1000000); // 1 000 000 microseconds = 1 sec
    }
}

    public static double getDistance() {
        trig.high();
        System.out.println("Set trig HIGH on pin: " + trig.getPin());
        sleepMicro(10);
        trig.low();
        System.out.println("Set trig LOW on pin " + echo.getPin());

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
