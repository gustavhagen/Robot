import com.pi4j.io.gpio.*;

public class UGVController implements Runnable {
    private static GpioController gpioController = GpioFactory.getInstance();
    private static GpioPinDigitalOutput pul = null;
    private static GpioPinDigitalOutput dir = null;
    private static GpioPinDigitalOutput ena = null;

    public UGVController(){
    }

    @Override
    public void run() {
    }
}
