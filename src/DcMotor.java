import com.pi4j.io.gpio.*;

public class DcMotor {
    private static GpioPinDigitalOutput pwm = null;

    public DcMotor(GpioPinDigitalOutput pwm) {
        this.pwm = pwm;
    }

    private void driveForward(int steps, int pulseWidth){
        motorAct(steps, pulseWidth);
    }

    private void motorAct(int steps, int pulseWidth){
        for(int i = 0; i < steps; i++) {
            pwm.high();
            sleepMicro(pulseWidth);
            pwm.low();
            sleepMicro(20000 - pulseWidth);
            if(steps > 500 && pulseWidth < 2000) {
                pulseWidth++;
            } else {
                pulseWidth = 1000;
            }
        }
    }

    private static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

}
