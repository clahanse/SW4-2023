import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.*;

public class SMLAParser {

    private int currentTokenIndex;
    private Token currentToken;
    private List<Token> tokens; // token list to get input data
    static List<Node> nodeList = new ArrayList<>(); // node list to save output data
    static List<Token> tokenList = new ArrayList<>(); // token list to save output data

    // TYPE OF THE TOKENS
    public enum TokenType {
        simulation("SETUP SIMULATION"),
        typesimulation("WHERE TYPE IS"),
        vacant("WHERE VACANT IS"),
        group("WHERE GROUPS WITH"),
        similarity("WHERE SIMILARITY IS"),
        moving("WHERE MOVING IS"),
        run("RUN SIMULATION FOR"),
        color("color", "RED", "GREEN", "YELLOW", "CYAN", "PINK", "BLUE"),
        typemoving("'RANDOM or SCHELLING'", "RANDOM", "SCHELLING"),
        calculation("CALCULATION"),
        identifier("a, b or c", "A", "B", "C"),
        letterordigit("ALPHABETS and NUMBER"),
        phrase("WITH, GROUPS, TICKS  or NO"),
        addoperator("operator + or -", "+", "-"),
        multoperator("operator * or /", "*", "/"),
        n_float("'float'"),
        n_integer("ineter"),
        digit("'1, 2...9'"),
        letter("'a...z or A...Z'"),
        equal("="),
        end_command("'end of line'"),
        EOF("'end of List'");

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
        this.currentToken = tokens.get(0);
        int lineNumber = 1;
        for (Token token : tokens) {
            if (!token.getType().equals("EOL")) {
                token.setLineNumber(lineNumber);
                lineNumber++;
            }
        }
    }

    // PARSER PROGRAM
    public void parseProgram() throws ParseException {
        parseStatements();
    }

    // PARSER STATEMENT
    private void parseStatements() throws ParseException {
        while (currentTokenIndex < tokens.size() && !currentToken.getType().equals("EOF")) {
            switch (currentToken.getType()) {
                case "simulation":
                    parseSetupSimulation();
                    break;
                case "typesimulation":
                    parseSetTypeSimulation();
                    break;
                case "vacant":
                    parseSetVacant();
                    break;
                case "group":
                    parseSetGroup();
                    break;
                case "similarity":
                    parseSetSimilarity();
                    break;
                case "moving":
                    parseSetMoving();
                    break;
                case "run":
                    parseRunSimulation();
                    break;
                case "calculation":
                    parseSimpleCalculation();
                    break;
                default: {
                    throw new IllegalArgumentException("Invalid statement type at line "
                            + currentToken.getLineNumber() + ": " + currentToken.getValue());
                }
            }
        }
    }
    // I. GROUP OF PARSER FUNCTIONS
    // 1. PARSER "SETUP SIMULATION (NAME) WITH NUMBER GROUPS "
    private void parseSetupSimulation() throws ParseException {
        if (getTokensCommand(tokens, currentTokenIndex) != 5) {
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.simulation);
        matchKeywordValue(TokenType.simulation);
        matchType(TokenType.letterordigit);
        matchStringValue(TokenType.letterordigit);
        matchType(TokenType.phrase);
        matchPhraseValue(TokenType.phrase);
        matchType(TokenType.n_integer);
        matchIntegerValue(TokenType.n_integer);
        matchType(TokenType.phrase);
        matchPhraseValue(TokenType.phrase);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Setup Simulation' parsed successfully");
    }

    // 2. PARSER "WHERE TYPE IS SCHELLING"
    private void parseSetTypeSimulation() throws ParseException {
        if (getTokensCommand(tokens, currentTokenIndex) != 2) {
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.typesimulation);
        matchKeywordValue(TokenType.typesimulation);
        matchType(TokenType.typemoving);
        matchStringValue(TokenType.typemoving);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Type simulation' parsed successfully");
    }

    // 3. PARSER "WHERE VACANT IS NUMBER"
    private void parseSetVacant() throws ParseException {
        if (getTokensCommand(tokens, currentTokenIndex) != 2) {
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.vacant);
        matchKeywordValue(TokenType.vacant);
        matchType(TokenType.n_integer);
        matchIntegerValue(TokenType.n_integer);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Vacant' parsed successfully");
    }

    // 4. PARSER "WHERE GROUPS WITH (name1: color1: ratio1, name2: color2: ratio2..."
    private void parseSetGroup() throws ParseException {
        matchType(TokenType.group);
        matchKeywordValue(TokenType.group);
        while (!currentToken.getType().equals("end_command")) {
            matchType(TokenType.letterordigit);
            matchStringValue(TokenType.letterordigit);
            matchType(TokenType.color);
            matchOtherValue(TokenType.color);
            matchType(TokenType.n_integer);
            matchIntegerValue(TokenType.n_integer);
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Groups' parsed successfully");
    }

    // 5. PARSER "WHERE SIMILARITY IS NUMBER"
    private void parseSetSimilarity() throws ParseException {

        if (getTokensCommand(tokens, currentTokenIndex) != 2) {
            System.out.println("\nTokensSimilarity: " +getTokensCommand(tokens, currentTokenIndex));
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.similarity);
        matchKeywordValue(TokenType.similarity);
        matchType(TokenType.n_integer);
        matchIntegerValue(TokenType.n_integer);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Similarity' parsed successfully");
    }

    // 6. PARSER "WHERE MOVING IS RANDOM"
    private void parseSetMoving() throws ParseException {
        if (getTokensCommand(tokens, currentTokenIndex) != 2) {
            System.out.println("\nTokensMoving: "+ getTokensCommand(tokens, currentTokenIndex));
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.moving);
        matchKeywordValue(TokenType.moving);
        matchType(TokenType.typemoving);
        matchOtherValue(TokenType.typemoving);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Moving' parsed successfully");
    }

    // 7. PARSER "RUN SIMULATION FOR NUMBER | NO TICKS"
    private void parseRunSimulation() throws ParseException {
        if (getTokensCommand(tokens, currentTokenIndex) != 3) {
            throw new ParseException("Invalid number of tokens in command", 0);
        }
        matchType(TokenType.run);
        matchKeywordValue(TokenType.run);
        matchType(TokenType.n_integer);
        matchIntegerValue(TokenType.n_integer);
        matchType(TokenType.phrase);
        matchPhraseValue(TokenType.phrase);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Run simulation' parsed successfully");
    }

    // 8. PARSER "CALCULATION a = 4 + 5 * 6 /..."
    private void parseSimpleCalculation() throws ParseException {
        matchType(TokenType.calculation);
        matchKeywordValue(TokenType.calculation);
        matchType(TokenType.identifier);
        matchOtherValue(TokenType.identifier);
        matchType(TokenType.equal);
        matchKeywordValue(TokenType.equal);
        parseAdditiveExpression();
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Calculation' parsed successfully");
    }

    // Parser the first term in the expression (called by parseSimpleCalculation())
    private void parseAdditiveExpression() throws ParseException {
        String type = currentToken.getType();
        if (type.equals("n_integer")) {
            parseNumOper();
        } else {
            handleSyntaxError(currentToken.getValue(), "'+','-','*','/'",currentToken.getLineNumber());
        }
    }

    // Parser the number and the operator (called by parseAdditiveExpression())
    private void parseNumOper() throws ParseException {
        while (!currentToken.getType().equals("end_command")) {
            matchType(TokenType.n_integer);
            matchIntegerValue(TokenType.n_integer);

            while (!currentToken.getType().equals("end_command")) {
                if (currentToken.getType().equals("addoperator")) {
                    matchType(TokenType.addoperator);
                    matchOtherValue(TokenType.addoperator);
                    if (currentToken.getType().equals("end_command")) {
                        handleSyntaxError(currentToken.getValue(), "a number",currentToken.getLineNumber());
                    }
                    break;
                } else if (currentToken.getType().equals("multoperator")) {
                    matchType(TokenType.multoperator);
                    matchOtherValue(TokenType.multoperator);
                    if (currentToken.getType().equals("end_command")) {
                        handleSyntaxError(currentToken.getValue(), "a number",currentToken.getLineNumber());
                    }
                    break;
                } else {
                    handleSyntaxError(currentToken.getValue(), "'+','-','*','/'",currentToken.getLineNumber());
                }
            }
        }
    }
    // II. GROUP OF MATCHING FUNCTIONS
    // 1. MATCH TYPE OF THE INPUT TOKEN AND EXPECTED TYPE
    private void matchType(TokenType expectedType) throws ParseException {
        if (currentTokenIndex == 0 && expectedType != TokenType.valueOf(currentToken.getType())) {
            handleSyntaxError(currentToken.getType(), String.valueOf(expectedType),currentToken.getLineNumber());
        } else if (expectedType != TokenType.valueOf(currentToken.getType())) {
            handleSyntaxError(currentToken.getType(), String.valueOf(expectedType),currentToken.getLineNumber());
        }
    }

    // 2. MATCH THE VALUE OF KEYWORDS "SETUP SIMULATION...", "WHERE TYPE..",...
    private void matchKeywordValue(TokenType expectedType) throws ParseException {
        // check the value are color, identify, type of moving, operator
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        String currentValue = currentToken.getValue().trim();
        if (!currentValue.equals(expectedValue)) {
            handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 3. MATCH THE VALUE OF TYPES "color", "typemoving", "identifier", "addoperator", "multoperator"
    private void matchOtherValue(TokenType expectedType) throws ParseException {
        // check the value are color, idenfifier, type of moving, operator
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        String[] ar_ValueInEnum = TokenType.valueOf(currentToken.getType()).getValues();
        if (currentToken.getType().equals("color") || currentToken.getType().equals("identifier") ||
                currentToken.getType().equals("typemoving") || currentToken.getType().equals("addoperator") ||
                currentToken.getType().equals("multoperator")) {
            if (!findElementInArray(ar_ValueInEnum, currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }

        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 4. MATCH THE VALUE OF STRING
    private void matchStringValue(TokenType expectedType) throws ParseException {
        // check the value is NAME
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        if (currentToken.getType().equals("letterordigit")) {
            if (!isAlphanumeric(currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 4. MATCH THE VALUE OF PHRASES "WITH", "GROUPS", "NO", "TICKS"
    private void matchPhraseValue(TokenType expectedType) throws ParseException {
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        int previousIndex1 = currentTokenIndex - 1;
        String type_pre_index1 = tokens.get(previousIndex1).getType();
        // check WITH in command SETUP SIMULATION (NAME) WITH NUMBER GROUPS
        if (type_pre_index1.equals("simulation")) {
            if (!currentToken.getValue().trim().equals("WITH")) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }
        }
        // check GROUPS in command SETUP SIMULATION (NAME) WITH NUMBER GROUPS
        if (type_pre_index1.equals("n_integer") && tokens.get(previousIndex1 - 1).getType().equals("simulation")) {
            if (!currentToken.getValue().trim().equals("GROUPS")) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }
        }
        // check NUMBER in command "RUN SIMULATION FOR NUMBER TICKS"
        if (type_pre_index1.equals("run")) {
            if (currentToken.getType().equals("n_integer")) {
                if (!isInteger(currentToken.getValue().trim())) {
                    handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
                }
            }
        }
        // check TICKS in command "RUN SIMULATION FOR NUMBER TICKS"
        if (currentToken.getType().equals("phrase") && tokens.get(previousIndex1 - 1).getType().equals("run")) {
            if (!currentToken.getValue().trim().equals("TICKS")) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 5. MATCH INTEGER
    private void matchIntegerValue(TokenType expectedType) throws ParseException {
        // check the value is an integer
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        if (currentToken.getType().equals("n_integer")) {
            if (!isInteger(currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue,currentToken.getLineNumber());
            }
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }
    // III. OTHER FUNCTIONS
    // Move to next token
    private int advanceToNextToken(List<Token> tokens, int currentTokenIndex) {
        // Consume the current token and advance to the next token
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);

        } else {
            // reached the end of the token stream
            currentToken = new Token("EOF", "","", currentToken.getLineNumber());
        }
        return currentTokenIndex;
    }

    // Get value of enum from index
    public static String TokenValueFromIndex(int index) {
        TokenType[] tokenTypes = TokenType.values();
        if (index >= 0 && index < tokenTypes.length) {
            return tokenTypes[index].getValues()[0];
        }
        return null;
    }

    // Get value of enum from type
    public static String getTokenValue(String type) {
        TokenType[] tokenTypes = TokenType.values();
        for (int i = 0; i < tokenTypes.length; i++) {
            if (tokenTypes[i].name().equals(type)) {
                String value = TokenValueFromIndex(i);
                return value;
            }
        }
        return null;
    }

    // Get number of tokens in the command
    private int getTokensCommand(List<Token> tokens, int startIndex) {
        int endIndex = startIndex + 1;
        while (endIndex < tokens.size()) {
            Token token = tokens.get(endIndex);
            String a = token.getType().trim();
            String b = String.valueOf((TokenType.end_command)).trim();
            if (a.equals(b)) {
                break;
            }
            endIndex++;
        }
        int numTokens = endIndex - startIndex;
        return numTokens;
    }

    // Find element in array
    public static boolean findElementInArray(String[] arr, String target) {
        for (String str : arr) {
            if (str.equals(target)) {
                return true;
            }
        }
        return false;
    }

    // Check a string as integer
    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check string consists of only alphabets and numbers
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }
    public void checkEOF() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            // reached the end of the token stream
            currentToken = new Token("EOF", "", "",currentToken.getLineNumber());
        }
    }

    // add node and token to the list
    private void addNodeAndToken(Token currentToken) throws ParseException {
        // create a new node for the current token and add it to the list of nodes
        Node node = new Node(currentToken.getType(), currentToken.getValue());
        nodeList.add(node);

        // add the current token to the list of tokens
        tokenList.add(currentToken);
    }
    public void handleSyntaxError(String unexpectedToken, String expectedToken, int lineNumber) throws ParseException {
        String errorMessage = "\nSyntax error on line %d: unexpected token '%s', expected '%s'";
        throw new ParseException(String.format(errorMessage, lineNumber, unexpectedToken, expectedToken), lineNumber);
    }

    // CLASS TOKEN
    public static class Token {
        private String typ;
        private String val;
        private int lineNumber;
        private List<String> commands;

        public Token(String type, String value, String command, int lineNumber) {
            this.typ = type;
            this.val = value;
            this.commands = new ArrayList<>();
            this.commands.add(command);
            this.lineNumber = lineNumber;
        }

        public String getValue() {
            return val;
        }

        public String getType() {
            return typ;
        }

        public void addVal(String newVal) {
            val = newVal;
        }

        public void addTyp(String newTyp) {
            typ = newTyp;
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
        @Override
        public String toString() {
            return String.format("(%s, \"%s\")", typ, val);
        }
    }
    public class Node {
        private TokenType type;
        private String value;
        private List<Node> children;

        public Node(String type, String value) {
            this.type = TokenType.valueOf(type);
            this.value = value;
            this.children = new ArrayList<>();
        }

        public TokenType getType() {
            return type;
        }

        public void setType(TokenType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(type).append(" ").append(value);
            for (Node child : children) {
                sb.append("\n  ").append(child.toString().replaceAll("\n", "\n  "));
            }
            return sb.toString();
        }

    }

    // CHECK PROGRAM
    /*public static void main(String[] args) {
        List<SMLAParser.Token> tokens = new ArrayList<>();
        // WHERE GROUPS WITH (name: color: ratio:...)
        tokens.add(new SMLAParser().new Token("group", "WHERE GROUPS WITH","", 10));
        // A
        tokens.add(new SMLAParser().new Token("letterordigit", "SCHELLING", "",11));
        tokens.add(new SMLAParser().new Token("color", "green","", 12));
        tokens.add(new SMLAParser().new Token("n_integer", "3","",13));
        // B
        tokens.add(new SMLAParser().new Token("letterordigit", "GROUPS","", 14));
        tokens.add(new SMLAParser().new Token("color", "green","", 15));
        tokens.add(new SMLAParser().new Token("n_integer", "30","", 16));
        tokens.add(new SMLAParser().new Token("end_command", "","", 0));

        // WHERE MOVING IS TYPE
        tokens.add(new SMLAParser().new Token("moving", "WHERE MOVING IS", "",19));
        tokens.add(new SMLAParser().new Token("typemoving", "RANDOM","", 0));
        tokens.add(new SMLAParser().new Token("end_command", "","", 0));

        // WHERE SIMILARITY IS NUMBER
        tokens.add(new SMLAParser().new Token("similarity", "WHERE SIMILARITY IS", "", 17));
        tokens.add(new SMLAParser().new Token("n_integer", "9", "", 18));
        tokens.add(new SMLAParser().new Token("end_command", "", "", 0));


        // RUN SIMULATION FOR
        tokens.add(new SMLAParser().new Token("run", "RUN SIMULATION FOR","",22 ));
        tokens.add(new SMLAParser().new Token("n_integer", "1","", 22));
        tokens.add(new SMLAParser().new Token("phrase", "TICKS","", 23));
        tokens.add(new SMLAParser().new Token("end_command", "","", 0));

        // WHERE TYPE IS SCHELLING
        tokens.add(new SMLAParser().new Token("typesimulation", "WHERE TYPE IS","", 7));
        tokens.add(new SMLAParser().new Token("letterordigit", "SCHELLING","", 7));
        tokens.add(new SMLAParser().new Token("end_command", "","", 0));
        // CALCULATION
        tokens.add(new SMLAParser().new Token("calculation", "CALCULATION","", 24));
        tokens.add(new SMLAParser().new Token("identifier", "a","", 25));
        tokens.add(new SMLAParser().new Token("equal", "=","", 26));
        tokens.add(new SMLAParser().new Token("n_integer", "30","", 28));
        tokens.add(new SMLAParser().new Token("addoperator", "+","", 29));
        tokens.add(new SMLAParser().new Token("n_integer", "3","", 30));
        tokens.add(new SMLAParser().new Token("multoperator", "*","", 31));
        tokens.add(new SMLAParser().new Token("n_integer", "2","", 32));
        tokens.add(new SMLAParser().new Token("addoperator", "-","", 33));
        tokens.add(new SMLAParser().new Token("n_integer", "3","", 34));
        tokens.add(new SMLAParser().new Token("end_command", "","", 36));

        // SETUP SIMULATION WITH NUMBER GROUPS
        tokens.add(new SMLAParser().new Token("simulation", "SETUP SIMULATION","", 1));
        tokens.add(new SMLAParser().new Token("letterordigit", "SCHELLING1","", 2));
        tokens.add(new SMLAParser().new Token("phrase", "WITH","", 3));
        tokens.add(new SMLAParser().new Token("n_integer", "3","", 4));
        tokens.add(new SMLAParser().new Token("phrase", "GROUPS","", 5));
        tokens.add(new SMLAParser().new Token("end_command", "","", 0));
        // WHERE VACANT IS NUMBER
        tokens.add(new SMLAParser().new Token("vacant", "WHERE VACANT IS","", 8));
        tokens.add(new SMLAParser().new Token("n_integer", "1", "",9));
        tokens.add(new SMLAParser().new Token("end_command", "", "",0));


        // EXCESS TOKENS
        //   tokens.add(new SMLAParser().new Token("multoperator", "GROUPS","", 37));
        // tokens.add(new SMLAParser().new Token("end_command", "GROUPS","", 38));

        try {
            SMLAParser parser = new SMLAParser(tokens);
            parser.parseProgram();
            System.out.println("\n Parser success");
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        // Save the node and token lists to a file
        File file = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\output.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("Node List:");
            for (Node node : nodeList) {
                writer.println(node.toString());
            }

            writer.println("\nToken List:");
            for (Token token : tokenList) {
                writer.println(token.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

}
