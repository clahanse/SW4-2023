import java.text.ParseException;
import java.util.*;

public class SMLAParser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;
    private int groupNumber;

    // TYPE OF TOKENS
    public enum TokenType {
        simulation("SETUP SIMULATION"),
        typesimulation("WHERE TYPE IS"),
        vacant("WHERE VACANT IS"),
        letterordigit("[a-zA-Z0-9]+"),
        letter_letterordigit("[a-zA-Z][a-zA-Z0-9]+"),
        n_integer("[0-9]+"),
        unknown,
        EOF;

        private final String[] values;

        TokenType(String... values) {
            this.values = values;
        }

        public String[] getValues() {
            return values;
        }
    }

    // CONSTRUCTOR
    public SMLAParser() {
    }

    // CONSTRUCTOR WITH PARAMETER
    public SMLAParser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        int lineNumber = 1;
        for (Token token : tokens) {
            if (!token.getType().equals("EOL")) {
                currentToken = token;
                currentToken.setLineNumber(lineNumber);
                break;
            }
            lineNumber++;
        }
    }
     // FUNCTION PARSERS PROGRAM
    public void parseProgram() throws ParseException {
        parseStatement();
    }

    // FUNCTION PARSES COMMANDS
    private void parseStatement() throws ParseException {

        switch (currentToken.getType()) {
            case "simulation":
                parseSetupSimulation();
            case "typesimulation":
                parseSetTypeSimulation();
            case "vacant":
                parseSetVacant();
             //   break;
            default:
               if (currentToken.getType()=="EOF"){
                   System.out.println("All tokens in the list were scanned");
               }else { throw new IllegalArgumentException("Invalid statement type at line "
                       + currentToken.getLineNumber() + ": " + currentToken.getType());
                   }
        }
    }

    // FUNCTION PARSES "SETUP SIMULATION (Schelling1) WITH 3 GROUPS"
    private void parseSetupSimulation() throws ParseException {
        match(TokenType.simulation);
        match(TokenType.letter_letterordigit);
        match(TokenType.letterordigit);
        match(TokenType.n_integer);
        match(TokenType.letterordigit);
        System.out.println("\n'Setup Simulation' parsed successfully");

    }

    // FUNCTION PARSES "WHERE TYPE IS SCHELLING"
    private void parseSetTypeSimulation() throws ParseException {
        match(TokenType.typesimulation);
        match(TokenType.letter_letterordigit);
        System.out.println("'Set Type Simulation' parsed successfully");
    }

    // FUNCTION PARSES "WHERE VACANT IS NUMBER"
    private void parseSetVacant() throws ParseException {
        match(TokenType.vacant);
        match(TokenType.n_integer);
        System.out.println("'Setup Vacant' parsed successfully");
    }

    // FUNCTION COMPARES THE TYPE OF INPUT TOKEN AND EXPECTED TYPE
    private void match(TokenType expectedType) throws ParseException {
        if (currentTokenIndex == 0 && expectedType != TokenType.valueOf(currentToken.getType())) {
            throw new ParseException("\nSyntax error on line   " + currentToken.getLineNumber() + ": unexpected token " +
                    currentToken.getType() + ", expected " + expectedType, currentToken.getLineNumber());
        }
        else if (expectedType == TokenType.valueOf(currentToken.getType())) {
            // Consume the current token and advance to the next token
            currentTokenIndex++;
            if (currentTokenIndex < tokens.size()) {
                currentToken = tokens.get(currentTokenIndex);
            } else {
                // have reached the end of the token stream
                currentToken = new Token("EOF", "", currentToken.getLineNumber());
            }
        } else {
            throw new ParseException("\nSyntax error on line " + currentToken.getLineNumber() + ": unexpected token " +
                    currentToken.getType() + ", expected " + expectedType, currentToken.getLineNumber());
        }
    }

    // CLASS TOKEN
    public class Token {

        private String type;
        private String value;
        private int lineNumber;

        private List<String> commands;

        public Token(String type, String value, int lineNumber) {
            this.type = type;
            this.value = value;
            this.commands = new ArrayList<>();
            this.lineNumber = lineNumber;
            //this.commands.add(command);
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

        public void addValue(String newValue) {
            value = newValue;
        }

        public void addType(String newType) {
            type = newType;
        }

        public List<String> getCommands() {
            return commands;
        }

        // getter and setter methods for lineNumber
        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

    // FUNCTION CHECKS PROGRAM
    public static void main(String[] args) {
        List<SMLAParser.Token> tokens = new ArrayList<>();
        // "SETUP SIMULATION (Schelling1) WITH 3 GROUPS"
        tokens.add(new SMLAParser().new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new SMLAParser().new Token("letter_letterordigit", "(SCHELLING1)", 2));
        tokens.add(new SMLAParser().new Token("letterordigit", "WITH", 3));
        tokens.add(new SMLAParser().new Token("n_integer", "3", 4));
        tokens.add(new SMLAParser().new Token("letterordigit", "GROUPS", 5));
        // "WHERE TYPE IS SCHELLING"
        tokens.add(new SMLAParser().new Token("typesimulation", "WHERE TYPE IS", 6));
        tokens.add(new SMLAParser().new Token("letter_letterordigit", "SCHELLING", 7));
        // "WHERE VACANT IS NUMBER"
        tokens.add(new SMLAParser().new Token("vacant", "WITH", 8));
        tokens.add(new SMLAParser().new Token("n_integer", "3", 9));
        // EXCESS TOKENS
        // tokens.add(new SMLAParser().new Token("letterordigit", "ABCD", 10));
        // tokens.add(new SMLAParser().new Token("letterordigit", "WHY", 11));
        try {
            SMLAParser parser = new SMLAParser(tokens);
            parser.parseProgram();
            System.out.println("\n Parser success");
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
