import com.pi4j.io.gpio.*;

public class Test {
    private static final long TIMEOUT = 23200;
    private static final double DISTANCE_CONSTANT = 170140e-9;
    private static GpioPinDigitalOutput pul1 = null;
    private static GpioPinDigitalOutput dir1 = null;
    private static GpioPinDigitalOutput pul2 = null;
    private static GpioPinDigitalOutput dir2 = null;
    private static GpioPinDigitalOutput trig = null;
    private static GpioPinDigitalInput echo = null;
    private static GpioPinDigitalOutput pwm = null;
    private static Servo servo;
    private static StepperMotor stepperMotor1;
    private static StepperMotor stepperMotor2;
    private static DriveMotor driveMotor;
    private static UltraSonicSensor ultraSonicSensor;

    public Test(){
        driveMotor = new DriveMotor(pwm);
        stepperMotor1 = new StepperMotor(pul1, dir1);
        stepperMotor2 = new StepperMotor(pul2, dir2);
    }

    public static void main(String[] args) throws InterruptedException {
        GpioController gpioController = GpioFactory.getInstance();
        //pwm = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        //trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        //echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        pul1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        dir1 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        pul2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        dir2 = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);

        stepperMotor1.moveUp(100);
        stepperMotor2.moveUp(100);

        //driveMotor.motorAct(1231, 1000);

//        while (true) {
//            System.out.println("Distance: " + getDistance() + " cm");
//            sleepMicro(1000000); // 1 000 000 microseconds = 1 sec
//        }
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
