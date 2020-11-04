import com.pi4j.io.gpio.*;

public class Servo {
    private static GpioPinDigitalOutput servoPin = null;

    public Servo(GpioPinDigitalOutput servoPin) {
        this.servoPin = servoPin;
    }

    private void servoAct(int steps, int pulseWidth) {
        for (int i = 0; i < steps; i++) {
            servoPin.high();
            sleepMicro(pulseWidth);
            servoPin.low();
            sleepMicro(20000 - pulseWidth);
            if (steps > 500 && pulseWidth < 2000) {
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
