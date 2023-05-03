import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class SMLAParser {

    private static int currentTokenIndex;
    private static Token currentToken;
    String nameSimulation;
    private List<Token> tokens; // list to receive input data
    static List<Token> tokenList = new ArrayList<>(); // token list to save output data

    // TYPE OF THE TOKENS
    public enum TokenType {
        simulation("SETUP SIMULATION"),
        pref("WHERE PREF IS"),
        vacant("WHERE VACANT IS"),
        using("USING"),
        run("RUN SIMULATION"),
        color("color", "RED", "GREEN", "YELLOW", "CYAN", "PINK", "BLUE"),
        typeMoving("'RANDOM or SCHELLING'", "RANDOM", "SCHELLING"),
        report("REPORT SIMULATION"),
        alphanumeric("ALPHABETS and NUMBER"),
        variable("NUMBER OR STRING"),
        phrase("WITH, GROUPS, TICKS  or NO"),
        addOperator("operator + or -", "+", "-"),
        multiOperator("operator * or /", "*", "/"),
        n_float("'float'"),
        n_integer("ineter"),
        letter("'a...z or A...Z'"),
        assignmentOperator("="),
        end_command("'end of line'"),
        EOF("'end of List'");

        public boolean equals(String value) {
            return Arrays.asList(this.getValues()).contains(value);
        }
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
        currentTokenIndex = 0;
        currentToken = tokens.get(0);
    }

    // PARSER PROGRAM
    public void parseProgram() throws Exception {
        parseStatements();
    }

    // PARSER STATEMENT
    private void parseStatements() throws Exception {
        while (currentTokenIndex < tokens.size() && !currentToken.getType().equals("EOF")) {
            switch (currentToken.getType()) {
                case "variable" -> parseVariable();
                case "simulation" -> parseSetupSimulation();
                case "pref" -> parsePref();
                case "vacant" -> parseSetVacant();
                case "using" -> parseSetMoving();
                case "run" -> parseRunSimulation();
                case "report" -> parseReportSimulation();
                default -> {
                    throw new IllegalArgumentException("\nInvalid statement type at line: "
                            + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue());
                }
            }
        }
    }

    // 1. PARSER VARIABLE
    private void parseVariable() throws ParseException {
        matchType(TokenType.variable);
        matchVariableValue(TokenType.variable);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Variable' parsed successfully");
    }

    // 2. PARSER "SETUP SIMULATION '(NAME)' WITH 'GROUPS' AS (Group1, Group2, Group3)"
    private void parseSetupSimulation() throws Exception {
        matchType(TokenType.simulation); // match 'SETUP SIMULATION'
        matchKeywordValue(TokenType.simulation);
        matchType(TokenType.alphanumeric); // match 'NAME'
        matchStringValue(TokenType.alphanumeric);
        matchType(TokenType.phrase); // match 'WITH'
        matchPhraseValue(TokenType.phrase);
        String currentValue = tokens.get(currentTokenIndex).getValue(); // match 'GROUPS'
        if (isInteger(currentValue)) { // GROUPS is integer
            matchType(TokenType.n_integer);
            matchIntegerValue(TokenType.n_integer);
            totalGroups = Integer.parseInt(currentValue);
        } else { // GROUPS is variable
            assignVariable(tokens, currentToken); // assign value to variable
        }
        if (getTokensCommand(tokens, currentTokenIndex) != 1 + totalGroups) { // check number of groups
            int line = currentToken.getLineNumber();
            throw new ParseException("\nInvalid number of groups or missing 'AS', line " + line, line);
        }
        matchType(TokenType.phrase); // match 'AS'
        matchPhraseValue(TokenType.phrase);
        while (!currentToken.getType().equals("end_command")) { // match (Group1, Group2...)
            assignVariable(tokens, currentToken);
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Setup Simulation' parsed successfully");
    }

    // 3. PARSER "WHERE PREF IS (pref1, pref2, pref3)"
    private void parsePref() throws Exception {
        int i = 0;
        matchType(TokenType.pref); // match 'WHERE PREF IS'
        matchKeywordValue(TokenType.pref);
        while (!currentToken.getType().equals("end_command")) { // match 'pref1, pref2...'
            prefGroup(tokens, currentToken);
            i++;
        }
        if (i != totalGroups) { // check number of groups
            throw new ParseException("\nInvalid number of groups, line " +
                    currentToken.getLineNumber(), currentToken.getLineNumber());
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Type simulation' parsed successfully");
    }

    // 4. PARSER "WHERE VACANT IS Vacant"
    private void parseSetVacant() throws Exception {
        checkTokens(tokens, currentTokenIndex, 2); // check number of tokens in command
        matchType(TokenType.vacant); // match 'WHERE VACANT IS'
        matchKeywordValue(TokenType.vacant);
        String currentValue = tokens.get(currentTokenIndex).getValue();
        if (isInteger(currentValue)) { // Vacant is integer
            matchType(TokenType.n_integer);
            matchIntegerValue(TokenType.n_integer);
        } else { // Vacant is variable
            assignVariable(tokens, currentToken);
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Vacant' parsed successfully");
    }

    // 5. PARSER "USING RANDOM MOVE"
    private void parseSetMoving() throws ParseException {
        checkTokens(tokens, currentTokenIndex, 3); // check number of tokens in command
        matchType(TokenType.using); // match 'USING'
        matchKeywordValue(TokenType.using);
        matchType(TokenType.typeMoving); // match 'RANDOM'
        matchOtherValue(TokenType.typeMoving);
        matchType(TokenType.phrase); // match 'MOVE'
        matchPhraseValue(TokenType.phrase);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Moving' parsed successfully");
    }

    // 6. PARSER "RUN SIMULATION 'NAME' FOR NUMBER TICKS"
    private void parseRunSimulation() throws Exception {
        checkTokens(tokens, currentTokenIndex, 5); // check number of tokens in command
        matchType(TokenType.run); // match 'RUN SIMULATION'
        matchKeywordValue(TokenType.run);
        matchType(TokenType.alphanumeric); // match 'NAME'
        matchNameSimulation(tokens, currentToken);
        matchType(TokenType.phrase); // match 'FOR'
        matchPhraseValue(TokenType.phrase);
        matchType(TokenType.n_integer); // match 'NUMBER'
        matchIntegerValue(TokenType.n_integer);
        matchType(TokenType.phrase); // match 'TICKS'
        matchPhraseValue(TokenType.phrase);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Run simulation' parsed successfully");
    }

    // 7. PARSER "REPORT SIMULATION 'NAME1', 'NAME2'..."
    private void parseReportSimulation() throws Exception {
        matchType(TokenType.report); // match 'REPORT SIMULATION'
        matchKeywordValue(TokenType.report);
        while (!currentToken.getType().equals("end_command")) { // match name
            matchType(TokenType.alphanumeric);
            matchNameSimulation(tokens, currentToken);
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Report simulation' parsed successfully");
    }
    // II. GROUP OF MATCHING FUNCTIONS
    // 1. MATCH TYPE OF THE INPUT TOKEN AND EXPECTED TYPE
    private void matchType(TokenType expectedType) throws ParseException {
        if (expectedType != TokenType.valueOf(currentToken.getType())) { // type don't match
            handleSyntaxError(currentToken.getType(), String.valueOf(expectedType), currentToken.getLineNumber());
        }
    }

    // 2. MATCH THE VALUE OF KEYWORDS "SETUP SIMULATION...", "WHERE PREF..",...
    private void matchKeywordValue(TokenType expectedType) throws ParseException {
        // check the value are color, identify, type of moving, operator
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        String currentValue = currentToken.getValue().trim();
        if (!currentValue.equals(expectedValue)) {
            handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
        }
        // add node to the list of nodes and token to the list of tokens
        currentToken.getCommands().add(nameSimulation);
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 3. MATCH THE VALUE OF TYPES "color", "typeMoving", "identifier", "addOperator", "multiOperator"
    private void matchOtherValue(TokenType expectedType) throws ParseException {
        // check the value are color, type of moving, operator
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        String[] ar_ValueInEnum = TokenType.valueOf(currentToken.getType()).getValues();
        List<String> allowedTypes = Arrays.asList("color", "identifier", "pref", "addOperator", "multiOperator");
        if (allowedTypes.contains(currentToken.getType())) {
            if (!findElementInArray(ar_ValueInEnum, currentToken.getValue().trim())) { // don't exist in enum
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
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
        if (currentToken.getType().equals("alphanumeric")) {
            if (!isAlphanumeric(currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        if (tokens.get(currentTokenIndex - 1).getValue().equals("SETUP")) { // set simulation name
            nameSimulation = currentToken.getValue();
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }


    // 5. MATCH THE VALUE OF PHRASES "WITH", "FOR", "TICKS"...
    private void matchPhraseValue(TokenType expectedType) throws ParseException {
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        int previousIndex1 = currentTokenIndex - 1;
        String type_pre_index1 = tokens.get(previousIndex1).getType();
        String value_pre_index1 = tokens.get(previousIndex1).getValue();
        int previousIndex2 = currentTokenIndex - 2;
        String type_pre_index2 = tokens.get(previousIndex2).getType();
        String value_pre_index2 = tokens.get(previousIndex2).getValue();
        // check WITH in command SETUP SIMULATION (NAME) 'WITH' NUMBER AS (Group1, Group2...)
        if (type_pre_index2.equals("simulation")) {
            if (!currentToken.getValue().trim().equals("WITH")) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // check AS in command SETUP SIMULATION (NAME) WITH NUMBER 'AS' (Group1, Group2...)
        if (value_pre_index2.equals("WITH")) {
            if (!currentToken.getValue().trim().equals("AS")) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // check FOR in command "RUN SIMULATION NAME 'FOR' NUMBER TICKS"
        if (type_pre_index2.equals("run")) {
            if (!currentToken.getValue().trim().equals("FOR")) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // check TICK in command "RUN SIMULATION NAME FOR NUMBER 'TICKS'"
        if (currentToken.getType().equals("phrase") && value_pre_index2.equals("FOR")) {
            if (!currentToken.getValue().trim().equals("TICKS")) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // check MOVE in command "USING RANDOM 'MOVE'"
        if (currentToken.getType().equals("phrase") && value_pre_index2.equals("USING")) {
            if (!currentToken.getValue().trim().equals("MOVE")) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // add node to the list of nodes and token to the list of tokens
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 6. MATCH INTEGER
    private void matchIntegerValue(TokenType expectedType) throws ParseException {
        // check the value is an integer
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        if (currentToken.getType().equals("n_integer")) {
            if (!isInteger(currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        }
        // add node to the list of nodes and token to the list of tokens
        currentToken.getCommands().add(nameSimulation);
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // 7. MATCH VALUE OF VARIABLE
    private void matchVariableValue(TokenType expectedType) throws ParseException {
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        if (isInteger(currentToken.getVariableValue()) || isAlphanumeric(currentToken.getVariableValue())) {
            // add node to the list of nodes and token to the list of tokens
            addNodeAndToken(currentToken);
            // advance to the next token
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        } else {
            handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
        }
    }
    // III. OTHER FUNCTIONS
    // Move to next token
    private static int advanceToNextToken(List<Token> tokens, int currentTokenIndex) {
        // Consume the current token and advance to the next token
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            // reached the end of the token stream
            currentToken = new Token("EOF", "","","",0);
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

    // Set pref for group
    private void prefGroup(List<Token> tokens, Token currentToken) throws Exception {
        String currentValue = currentToken.getValue();
        int newValue = 0;
        // calculation (integer), (integer +|- variable), (integer +|- integer)
        if (isInteger(currentValue)) { // check token value is integer, fx. 7
            if (Integer.parseInt(currentValue) == 0) {
                throw new Exception("\nPref isn't zero, line " + currentToken.getLineNumber());
            }
            if (!isOperator(tokens.get(currentTokenIndex + 1).getValue())) { // check next token isn't '+' or '-'
                currentToken.getCommands().add(nameSimulation);
                addNodeAndToken(currentToken);  // add token into the list (5)
            } else {  // if exist '+' or '-' after current token, fx. 7 +
                newValue = Integer.parseInt(currentValue);
                String nextValue = (tokens.get(currentTokenIndex + 2).getValue());
                if (!isInteger(tokens.get(currentTokenIndex + 2).getValue())) { // after operator is variable, fx. 7 + x
                    performMathOperation1(tokens, currentTokenIndex, nextValue, newValue);
                    currentTokenIndex += 2;
                } else { // after the integer is an integer,
                    performMathOperation3(currentTokenIndex, newValue, Integer.parseInt(nextValue));
                    currentTokenIndex += 2;
                }
            }
            // calculation (variable), (variable +|- variable), (variable +|- integer)
        } else if (isAlphanumeric(currentValue)) { // check token is variable, fx. y
            boolean found = false;
            int lineNum = currentToken.getLineNumber();
            for (Token t : tokens) {
                if (t.getValue().equals(currentValue) && t.getType().equals("variable")) { // search variable in the list
                    newValue = Integer.parseInt(t.getVariableValue()); // get variable value from list
                    if (!isOperator(tokens.get(currentTokenIndex + 1).getValue())) { // check operator after variable
                        addNodeAndToken(new Token("n_integer", t.getVariableValue(), nameSimulation,"", lineNum));
                    } else { // exist operator after variable '+' or '-', fx. y +
                        String nextValue = (tokens.get(currentTokenIndex + 2).getValue());
                        if (!isInteger(tokens.get(currentTokenIndex + 2).getValue())) { // exist variable after operator,
                            performMathOperation1(tokens, currentTokenIndex, nextValue, newValue);
                            currentTokenIndex += 2;
                        } else {
                            performMathOperation3(currentTokenIndex, newValue, Integer.parseInt(nextValue));
                            currentTokenIndex += 2;
                        }
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new Exception("\nVariable " + currentValue + " not found in list, line "
                        + currentToken.getLineNumber());
            }
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // Match operator in pref
    private void performMathOperation1(List<Token> tokens, int currentTokenIndex,
                                       String nextValue, int newValue) throws Exception {
        for (Token t1 : tokens) {
            if (t1.getValue().equals(nextValue) && t1.getType().equals("variable")) {
                performMathOperation2(t1, currentTokenIndex, newValue);
            }
        }
    }

    private void performMathOperation2(Token t1, int currentTokenIndex, int newValue) throws Exception {
        if (tokens.get(currentTokenIndex + 1).getValue().equals("+")) {
            newValue += Integer.parseInt(t1.getVariableValue()); // y + z
        } else {
            newValue -= Integer.parseInt(t1.getVariableValue()); // y - z
            if (newValue <= 0) {
                throw new Exception("\nInvalid Pref: zero or negative, line " + currentToken.getLineNumber());
            }
        }
        int lineNum=tokens.get(currentTokenIndex).getLineNumber();
        addNodeAndToken(new Token("n_integer", String.valueOf(newValue), nameSimulation,"", lineNum));
    }

    private void performMathOperation3(int currentTokenIndex, int newValue, int nextValue) throws Exception {
        if (tokens.get(currentTokenIndex + 1).getValue().equals("+")) {
            newValue += Integer.parseInt(String.valueOf(nextValue)); // y + z
        } else {
            newValue -= Integer.parseInt(String.valueOf(nextValue)); // y - z
            if (newValue <= 0) {
                throw new Exception("\nInvalid Pref: zero or negative, line " + currentToken.getLineNumber());
            }
        }
        int lineNum=tokens.get(currentTokenIndex).getLineNumber();
        addNodeAndToken(new Token("n_integer", String.valueOf(newValue), nameSimulation,"", lineNum));
    }

    // Check string is operator
    private boolean isOperator(String s) {
        if (s.equals("+") || s.equals("-")) {
            return true;
        } else {
            return false;
        }
    }

    // Assign value to variable
    static int totalGroups; // number of groups

    public void assignVariable(List<Token> tokens, Token currentToken) throws Exception {
        int lineNum=currentToken.getLineNumber();
        String typ = "";
        boolean isNewVariable = true;
        for (Token t : tokens) {
            if (t.getValue().equals(currentToken.getValue()) && t.getType().equals("variable")) {
                if (isInteger(t.getVariableValue())) {
                    typ = "n_integer";
                } else {
                    typ = "alphanumeric";
                }
                addNodeAndToken(new Token(typ, String.valueOf(t.getVariableValue()),"","",lineNum));
                int previousIndex1 = currentTokenIndex - 1;
                String value_pre_index1 = tokens.get(previousIndex1).getValue();
                if (value_pre_index1.equals("WITH") || value_pre_index1.equals("IS")) {
                    totalGroups = Integer.parseInt(t.getVariableValue());
                }
                currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
                isNewVariable = false;
                break;
            }
        }
        if (isNewVariable) {
            System.out.println("New name: "+currentToken.getValue());
            currentToken.getCommands().add(nameSimulation);
            addNodeAndToken(currentToken);
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        }
    }

    // Match simulation name in report
    public void matchNameSimulation(List<Token> tokens, Token currentToken) throws Exception {
        if (!tokens.contains(currentToken)) {
            throw new Exception("\nToken " + currentToken + " not found in list, line " +
                    currentToken.getLineNumber());
        }
        int i = 0;
        String currentValue = currentToken.getValue();
        for (Token t : tokens) {
            if (t.getValue().equals(currentValue)) {
                i++;
            }
        }
        if (i < 2) {
            throw new Exception("\nInvalid name of simulation, line " + currentToken.getLineNumber());
        } else {
            // add node to the list of nodes and token to the list of tokens
            addNodeAndToken(currentToken);
            // advance to the next token
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        }
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

    // check number of tokens in command
    public void checkTokens(List<Token> tokens, int currentTokenIndex, int expectedTokens) throws ParseException {
        int actualTokens = getTokensCommand(tokens, currentTokenIndex);
        if (actualTokens != expectedTokens) {
            int line = tokens.get(currentTokenIndex).getLineNumber();
            throw new ParseException("\nInvalid number of tokens in command, line " + line, line);
        }
    }

    // Add node and token to the list
    private void addNodeAndToken(Token currentToken) throws ParseException {
        // add the current token to the list of tokens
        tokenList.add(currentToken);
    }

    // Message error
    public void handleSyntaxError(String unexpectedToken, String expectedToken, int lineNumber) throws ParseException {
        String errorMessage = "\nSyntax error on line %d: unexpected token '%s', expected '%s'";
        throw new ParseException(String.format(errorMessage, lineNumber, unexpectedToken, expectedToken), lineNumber);
    }
    public static List<Token> getTokenList() {
        return tokenList;
    }


    // CHECK PROGRAM
    public static void main(String[] args) throws Exception {
        List<Token> tokens = new ArrayList<>();
        // Output from Scanner
        // Variable pref1 = 10, vacant = 50
        tokens.add(new Token("variable","PREF1", "10","", 1));
        tokens.add(new Token("end_command","end of command", "","", 1));
        tokens.add(new Token("variable","VACANT", "50","", 2));
        tokens.add(new Token("end_command","end of command", "","", 2));

        // Run simulation Example1 FOR 50 TICKS
        tokens.add(new Token("run","RUN SIMULATION", "","", 3));
        tokens.add(new Token("alphanumeric","EXAMPLE1", "","", 3));
        tokens.add(new Token("phrase","FOR", "","", 3));
        tokens.add(new Token("n_integer","50", "","", 3));
        tokens.add(new Token("phrase","TICKS", "","", 3));
        tokens.add(new Token("end_command","end of command", "","", 3));

        // Report simulation Example1
        tokens.add(new Token("report","REPORT SIMULATION", "","", 4));
        tokens.add(new Token("alphanumeric","EXAMPLE1", "","", 4));
        tokens.add(new Token("end_command","end of command", "","", 4));

        // Using random move
        tokens.add(new Token("using","USING", "","", 5));
        tokens.add(new Token("typeMoving","RANDOM", "","", 5));
        tokens.add(new Token("phrase","MOVE", "","", 5));
        tokens.add(new Token("end_command","end of command", "","", 5));

        // Setup simulation (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
        tokens.add(new Token("simulation","SETUP SIMULATION", "","", 6));
        tokens.add(new Token("alphanumeric","EXAMPLE1", "","", 6));
        tokens.add(new Token("phrase","WITH", "","", 6));
        tokens.add(new Token("n_integer","4", "","", 6));
        tokens.add(new Token("phrase","AS", "","", 6));
        tokens.add(new Token("alphanumeric","GROUP1", "","", 6));
        tokens.add(new Token("alphanumeric","GROUP2", "","", 6));
        tokens.add(new Token("alphanumeric","GROUP3", "","", 6));
        tokens.add(new Token("alphanumeric","ABC", "","", 6));
        tokens.add(new Token("end_command","end of command", "","", 6));

        // Where pref is (10, 20, 30, 40)
        tokens.add(new Token("pref","WHERE PREF IS", "","", 7));
        tokens.add(new Token("n_integer","10", "","", 7));
        tokens.add(new Token("n_integer","20", "","", 7));
        tokens.add(new Token("n_integer","30", "","", 7));
        tokens.add(new Token("n_integer","40", "","", 7));
        tokens.add(new Token("end_command","end of command", "","", 7));

        // Where vacant is Vacant
        tokens.add(new Token("vacant","WHERE VACANT IS", "","", 8));
        tokens.add(new Token("alphanumeric","VACANT", "","", 8));
        tokens.add(new Token("end_command","end of command", "","", 8));

        try {
            SMLAParser parser = new SMLAParser(tokens);
            parser.parseProgram();
            System.out.println("\n Parser success");
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("\n - Parser result: ");
        //   SMLAParser parser = new SMLAParser(tokens);
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        System.out.println("\n*** Token list from Parser ");

        int j=1;
        for (Token token : getTokenList()) {
            if (token.getType().equals("variable")) {
                System.out.println(j + "-type: " + token.getType() + ", value: " +
                        token.getValue() + ", variable value: " + token.getVariableValue() + ", line: " + token.getLineNumber());
            } else {
                System.out.println(j + "-type: " + token.getType() + ", value: " + token.getValue() +
                        ", line: " + token.getLineNumber());
            }
            j++;
        }
        System.out.println("*** End of the list - Parser successful");

        // Save the node and token lists to a file
    /* File file = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\output.txt");
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
        }*/
    }
}
