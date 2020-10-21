import com.pi4j.io.gpio.*;

public class Encoder {
    private GpioPinDigitalInput encoderA;
    private GpioPinDigitalInput encoderB;
    private boolean A;
    private boolean B;
    private boolean lastA;
    private int counter = 0;

    public Encoder(GpioPinDigitalInput encoderA, GpioPinDigitalInput encoderB) {
        this.encoderA = encoderA;
        this.encoderB = encoderB;
    }

    public int enoderAct() {
        A = encoderA.isHigh();
        B = encoderB.isHigh();
        if (A != lastA) {
            if (B != A) {
                counter++;
            } else {
                counter--;
            }
        }
        lastA = encoderA.isHigh();
        return counter;
    }
}
