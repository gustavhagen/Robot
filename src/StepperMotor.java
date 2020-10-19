import com.pi4j.io.gpio.*;

public class StepperMotor {
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;

    public StepperMotor(GpioPinDigitalOutput pul, GpioPinDigitalOutput dir) {
        this.pul = pul;
        this.dir = dir;
    }

    public void stepperMotorAct(int steps) {
        for (int i = 0; i < steps; i++) {
            pul.high();
            sleepMicro(50);
            pul.low();
            sleepMicro(50);
        }
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

    public void setElevatorHeight(int height) {
    }

    public void moveUp(int steps) throws InterruptedException {
        stepperMotorAct(steps);
    }

    public void moveDown(int steps) throws InterruptedException {
        dir.high();
        stepperMotorAct(steps);
    }
}