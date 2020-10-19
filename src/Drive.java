import com.pi4j.io.gpio.*;

public class Drive {
    private static GpioPinDigitalOutput stepperMotorPul = null;
    private static GpioPinDigitalOutput stepperMotorDir = null;
    private static GpioPinDigitalOutput ena = null;
    private static GpioPinDigitalOutput dcMotorPul = null;

    private int speed;
    private int distance;
    private int acceleration;

    public Drive(int speed, int distance, int acceleration) throws InterruptedException {
        this.speed = speed;
        this.distance = distance;
        this.acceleration = acceleration;

        GpioController gpioController = GpioFactory.getInstance();
        stepperMotorPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_17, PinState.LOW);
        stepperMotorDir = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_22, PinState.LOW);
        dcMotorPul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_14, PinState.LOW);
    }

    public void turnLeft(int steps) {
        stepperMotorAct(steps);
    }

    public void turnRight(int steps) {
        stepperMotorDir.high();
        stepperMotorAct(steps);
    }

    public void stepperMotorAct(int steps) {
        for (int i = 0; i < steps; i++) {
            stepperMotorPul.high();
            sleepMicro(50);
            stepperMotorPul.low();
            sleepMicro(50);
        }
    }

    public void setAcceleration(){
    }

    public void setDir(){
    }

    public void setSpeed(){
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }
}
