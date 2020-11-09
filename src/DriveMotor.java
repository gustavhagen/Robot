import com.pi4j.io.gpio.*;

public class DriveMotor {
    private GpioPinDigitalOutput pwm = null;

    public DriveMotor(GpioPinDigitalOutput pwm) {
        this.pwm = pwm;
    }

    public void setMotorSpeed(int speed) {
        int pulseWidth = 1500 + speed*5;
        pwm.high();
        sleepMicro(pulseWidth);
        pwm.low();
        sleepMicro(20000 - pulseWidth);
    }

    private static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }
}
