import java.io.Serializable;
import java.util.List;

public class Command implements Serializable {
    private static final long serialVersionUID = 2780459376294108402L;
    private final String command;
    private final int value;
    private final boolean[] wasd;
    private final List<Integer> UGVs;

    public Command(String command, int value, boolean[] wasd, List<Integer> UGVs) {
        this.command = command;
        this.value = value;
        this.wasd = wasd;
        this.UGVs = UGVs;
    }

    public String getCommand() {
        return command;
    }

    public int getValue() {
        return value;
    }

    public boolean[] getWasd() {
        return wasd;
    }

    public List<Integer> getUGVs() {
        return UGVs;
    }
}
