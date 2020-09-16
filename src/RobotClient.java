import java.io.IOException;

public class RobotClient {

    private static ImageSender imageSender;

    private static final String HOST = "10.22.192.92";
    private static final int PORT = 42069;

    public static void main(String[] args) throws IOException {
        imageSender = new ImageSender(HOST, PORT);
        imageSender.run();
    }
}
