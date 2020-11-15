import com.pi4j.io.gpio.*;

/**
 * The Encoder-class is to get how many revolutions the DC motor is turning.
 *
 * @author Gustav Sørdal Hagen
 */
public class Encoder {
    // Creates the class-variables for the Encoder class
    private final GpioPinDigitalInput encoderA;
    private final GpioPinDigitalInput encoderB;
    private boolean A;
    private boolean B;
    private boolean lastA;
    private int counter = 0;

    /**
     * The constructor for the Encoder-class. Takes inn two parameters as pins for the encoder.
     * @param encoderA The EncoderA pin.
     * @param encoderB The EncoderB pin.
     */
    public Encoder(GpioPinDigitalInput encoderA, GpioPinDigitalInput encoderB) {
        this.encoderA = encoderA;
        this.encoderB = encoderB;
    }

    /**
     * This method returns the amount of revolutions the DC motor as turned.
     * @return The amount of revolutions the DC motor as turned.
     */
    public int encoderAct() {
        // A and B depends on if the pin for the encoder pins are high or low.
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
