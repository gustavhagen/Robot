import com.pi4j.io.gpio.*;

public class Test{
    private static GpioPinDigitalOutput pul = null;


    public static void main(String[] args){
        GpioController gpioController = GpioFactory.getInstance();
        pul = gpioController.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);

        int pulseWidth = 1000;
        int steps = 10000;
        System.out.println("Starting!");
        for(int i = 0; i < steps; i++) {
            pul.high();
            sleepMicro(pulseWidth);
            pul.low();
            sleepMicro(20000 - pulseWidth);
            if(steps > 500 && pulseWidth < 2000) {
                pulseWidth++;
            } else {
                pulseWidth = 1000;
            }
        }
        System.out.println("Done!");
    }

    public static void sleepMicro(int delay) {
        long initialTime = System.nanoTime();
        long updatedTime = 0;
        do {
            updatedTime = System.nanoTime();
        } while ((initialTime + delay * 1000) >= updatedTime);
    }

}
