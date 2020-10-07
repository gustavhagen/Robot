import java.io.Serializable;

public class Command implements Serializable
{
    private static final long serialVersionUID = 2780459376294108402L;
    String command;
    int value;

    public Command(String command, int value)
    {
        this.command = command;
        this.value = value;
    }

    public String getCommand()
    {
        return command;
    }

    public int getValue()
    {
        return value;
    }
}
