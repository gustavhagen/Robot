import com.pi4j.io.gpio.*;

public class Test {
//    private static final long TIMEOUT = 23200;
//    private static final double DISTANCE_CONSTANT = 170140e-9;
//    private static GpioPinDigitalOutput cameraPul = null;
//    private static GpioPinDigitalOutput cameraDir = null;
//    private static GpioPinDigitalOutput turnPul = null;
//    private static GpioPinDigitalOutput turnDir = null;
//    private static GpioPinDigitalOutput trig = null;
//    private static GpioPinDigitalInput echo = null;
//    private static GpioPinDigitalOutput pwm = null;
//    private static GpioPinDigitalOutput servoPin = null;
//    private static Servo servo;
//    private static StepperMotor stepperMotor1;
//    private static StepperMotor stepperMotor2;
//    private static DriveMotor driveMotor;
//    private static UltraSonicSensor frontRightSensor;
//    private static UltraSonicSensor frontLeftSensor;
//    private static UltraSonicSensor sideSensor;
//    private static UltraSonicSensor backSensor;


    public static void main(String[] args) throws InterruptedException {
        GpioController gpioController = GpioFactory.getInstance();
        //pwm = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        //trig = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        //echo = gpioController.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
        cameraPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
        cameraDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        turnPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW);
        turnDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW);
        servoPin = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);

        driveMotor = new DriveMotor(pwm);
        servo = new Servo(servoPin);
        stepperMotor1 = new StepperMotor(cameraPul, cameraDir);
        stepperMotor2 = new StepperMotor(turnPul, turnDir);


//        while (true) {
//            System.out.println("Distance: " + getDistance() + " cm");
//            sleepMicro(1000000); // 1 000 000 microseconds = 1 sec
//        }
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }
}
