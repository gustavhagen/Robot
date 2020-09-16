import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    public static final int PORT = 52342;
    public static final int BUFFER_SIZE = 500102;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                saveFile(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveFile(Socket socket) throws Exception {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        FileOutputStream fileOutputStream = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        // 1. Read file name.
        Object object = objectInputStream.readObject();
        if (object instanceof String) {
            fileOutputStream = new FileOutputStream("C:\\Users\\Gustav\\Desktop\\images\\"+object.toString());  // Edit it for specific path
            System.out.println("C:\\Users\\Gustav\\Desktop\\images\\"+object.toString());
        } else {
            throwException("Something is wrong");
        }
        // 2. Read file to the end.
        Integer bytesRead = 0;
        do {
            object = objectInputStream.readObject();
            if (!(object instanceof Integer)) {
                throwException("Something is wrong");
            }
            bytesRead = (Integer) object;
            object = objectInputStream.readObject();
            if (!(object instanceof byte[])) {
                throwException("Something is wrong");
            }
            buffer = (byte[]) object;
            // 3. Write data to output file.
            fileOutputStream.write(buffer, 0, bytesRead);
        } while (bytesRead == BUFFER_SIZE);
        System.out.println("File transfer success");
        fileOutputStream.close();
        objectInputStream.close();
        objectOutputStream.close();
    }
    public static void throwException(String message) throws Exception {
        throw new Exception(message);
    }
    public static void main(String[] args) {
        new Server().start();
    }
}