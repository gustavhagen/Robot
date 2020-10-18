import com.pi4j.io.gpio.*;

public class Test {
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput trig = null;
    private static GpioPinDigitalInput echo = null;

    public static void main(String[] args) {
        GpioController gpioController = GpioFactory.getInstance();
        //pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);

        while (true) {
                trig.high();
                sleepMicro(10);
                trig.low();

                while (echo.isLow()) {
                } //Wait until the ECHO pin gets HIGH

                long startTime = System.nanoTime();
                while (echo.isHigh()) {
                } // Wait until the ECHO pin gets LOW
                long endTime = System.nanoTime();

                System.out.println("Distance: " + (((endTime - startTime) / 1000 / 2) / 29.1) + " cm");
                sleepMicro(1000000); // Sleep for 1 000 000 microseconds = 1 seconds
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
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

}
