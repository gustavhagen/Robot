import com.pi4j.io.gpio.*;

public class StepperMotor {
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;

    private int currentPosition = 0;
    private long lastPulseTime = 0;

    public StepperMotor(GpioPinDigitalOutput pul, GpioPinDigitalOutput dir) {
        this.pul = pul;
        this.dir = dir;
    }

    public void stepperMotorAct(int steps, long speed) {
        if (currentPosition < steps) {
            dir.high();
            sleepNano(speed-(System.nanoTime() - lastPulseTime));
            pul.high();
            sleepNano(speed);
            pul.low();
            lastPulseTime = System.nanoTime();
            currentPosition++;
        }
        if (currentPosition > steps) {
            dir.low();
            sleepNano(speed-(System.nanoTime() - lastPulseTime));
            pul.high();
            sleepNano(speed);
            pul.low();
            lastPulseTime = System.nanoTime();
            currentPosition--;
        }
    }

    public static void sleepNano(long delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay) >= updatedTime);
    }
}
