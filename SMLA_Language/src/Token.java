import java.util.ArrayList;
import java.util.List;

public class Token {
    private String type;
    private String value;
    private int lineNumber;
    private List<String> commands;
    private String variableValue;

    public Token(String type, String value,int lineNumber) {
        this.type = type;
        this.value = value;
        this.commands = new ArrayList<>();
        //  this.commands.add(command);
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
    public void setValue(String newValue){
        this.value=newValue;
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

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
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
