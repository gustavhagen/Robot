import com.pi4j.io.gpio.*;
import org.omg.CORBA.TIMEOUT;

public class Test {
    private static final long TIMEOUT = 23200;
    private static final double DISTANCE_CONSTANT = 17;
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput trig = null;
    private static GpioPinDigitalInput echo = null;

    public static void main(String[] args) {
        GpioController gpioController = GpioFactory.getInstance();
        //pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);

        while (true) {
            getDistance();
            System.out.println("Distance: " + getDistance());
        }


    }
    // TEST FOR DC-MOTOR
//        int pulseWidth = 1000;
//        int steps = 10000;
//        System.out.println("Starting!");
//        for(int i = 0; i < steps; i++) {
//            pul.high();
//            sleepMicro(pulseWidth);
//            pul.low();
//            sleepMicro(20000 - pulseWidth);
//            if(steps > 500 && pulseWidth < 2000) {
//                pulseWidth++;
//            } else {
//                pulseWidth = 1000;
//            }
//        }
//        System.out.println("Done!");

    private static double getDistance() {
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
