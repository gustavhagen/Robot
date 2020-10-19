import com.pi4j.io.gpio.*;

public class Test {
    private static final long TIMEOUT = 23200;
    private static final double DISTANCE_CONSTANT = 170140e-9;
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput trig = null;
    private static GpioPinDigitalInput echo = null;
    Servo servo;
    StepperMotor stepperMotor;
    DriveMotor driveMotor;
    UltraSonicSensor ultraSonicSensor;



    public static void main(String[] args) {
        GpioController gpioController = GpioFactory.getInstance();
        trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);



        while (true) {
            System.out.println("Distance: " + getDistance() + " cm");
            sleepMicro(1000000); // 1 000 000 microseconds = 1 sec
        }


    }

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
