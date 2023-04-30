package main.lexer;

public class Token {

    private String type;
    private String value;
    private int lineNumber;
    private String command; 

        public Token(String type, String value, String command, int lineNumber) {
            this.type = type;
            this.value = value;
            this.command = command;
            this.lineNumber = lineNumber;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public String getCommand() {
            return command;
        }

        public int getLineNumber() {
            return lineNumber;
        }

}
