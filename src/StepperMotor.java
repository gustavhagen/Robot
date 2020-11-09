import com.pi4j.io.gpio.*;

public class StepperMotor {
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;

    private int currentPosition = 0;

    public StepperMotor(GpioPinDigitalOutput pul, GpioPinDigitalOutput dir) {
        this.pul = pul;
        this.dir = dir;
    }

    public void stepperMotorAct(int steps, int speed) {
        if (currentPosition <= steps) {
            dir.high();
            pul.high();
            sleepMicro(50);
            pul.low();
            sleepMicro(speed);
            currentPosition++;
        }
        if (currentPosition >= steps) {
            dir.low();
            pul.high();
            sleepMicro(50);
            pul.low();
            sleepMicro(speed);
            currentPosition--;
        }
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }
}