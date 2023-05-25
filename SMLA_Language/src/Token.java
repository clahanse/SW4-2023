import java.util.ArrayList;
import java.util.List;

public class Token {
    private final String type;
    private final String value;
    private int lineNumber;
    private List<String> commands;
    private String variableValue;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(String type, String value, int lineNumber) {
        this.type = type;
        this.value = value;
        this.commands = new ArrayList<>();
        this.lineNumber = lineNumber;
    }

    public Token(String type, String value, String variableValue, String command, int lineNumber) {
        this.type = type;
        this.value = value;
        this.variableValue = variableValue;
        this.commands = new ArrayList<>();
        this.commands.add(command);
        this.lineNumber = lineNumber;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public List<String> getCommands() {
        return commands;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        if (variableValue == null) {
            return String.format("(%s, \"%s\")", type, value);
        } else {
            return String.format("(%s, \"%s\", \"%s\")", type, value, variableValue);
        }
    }
}
