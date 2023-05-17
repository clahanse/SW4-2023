import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SMLAParser {

    private static int currentTokenIndex;
    private static Token currentToken;
    String nameSimulation;
    static int totalGroups; // number of groups
    private List<Token> tokens; // list to receive input data
    static List<Token> tokenList = new ArrayList<>(); // token list to save output data

    // TYPE OF THE TOKENS
    public enum TokenType {
        simulation("SETUP SIMULATION"),
        pref("WHERE PREF IS"),
        vacant("WHERE VACANT IS"),
        using("USING"),
        run("RUN SIMULATION"),
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
                            + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                            + ", expected: alphanumeric, setup, where, using, run or report");
                }
            }
        }
    }

    // 1. PARSER VARIABLE
    private void parseVariable() throws Exception {
        matchType(TokenType.variable);
        currentTokenIndex++;
        currentToken = tokens.get(currentTokenIndex);
        prefGroup(tokens, currentToken);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        System.out.println("'Variable' parsed successfully");
    }

    // 2. PARSER "SETUP SIMULATION '(NAME)' WITH 'numberGroups' AS (Group1, Group2, Group3)"
    private void parseSetupSimulation() throws Exception {
        int i = 0;
        matchType(TokenType.simulation); // match 'SETUP SIMULATION'
        matchKeywordValue(TokenType.simulation);
        matchType(TokenType.alphanumeric); // match 'NAME'
        assignVariable(tokens, currentToken);
        // matchStringValue(TokenType.alphanumeric);
        matchType(TokenType.phrase); // match 'WITH'
        matchPhraseValue(TokenType.phrase);
        String currentValue = tokens.get(currentTokenIndex).getValue(); // match 'numberGroups'
        if (isInteger(currentValue)) { // GROUPS is integer
            matchType(TokenType.n_integer);
            matchIntegerValue(TokenType.n_integer);
            totalGroups = Integer.parseInt(currentValue);
        } else { // GROUPS is variable
            assignVariable(tokens, currentToken); // assign value to variable
        }
        matchType(TokenType.phrase); // match 'AS or GROUPS'
        matchPhraseValue(TokenType.phrase);
        String previous_currentValue = tokens.get(currentTokenIndex - 1).getValue();
        if (previous_currentValue.equals("GROUPS")) {
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
            System.out.println("'Vacant' parsed successfully");
        } else if (previous_currentValue.equals("AS")) {
            String returnValue = "";
            float sum = 0.0F;
            while (!currentToken.getType().equals("end_command")) { // match 'Group1, Group2...'
                returnValue = prefGroup(tokens, currentToken);
                i++;
                sum = sum + Float.parseFloat(returnValue);
                if (sum > 100) {
                    throw new Exception("Sum of percent groups <= 100" + ", line " + currentToken.getLineNumber());
                }
            }
            if (i != totalGroups) { // check number of groups
                throw new ParseException("\nInvalid number of groups, line " +
                        currentToken.getLineNumber(), currentToken.getLineNumber());
            }
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
            System.out.println("'Setup Simulation' parsed successfully");
        }
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
        System.out.println("'Pref simulation' parsed successfully");
    }

    // 4. PARSER "WHERE VACANT IS Vacant"
    private void parseSetVacant() throws Exception {
        matchType(TokenType.vacant); // match 'WHERE VACANT IS'
        matchKeywordValue(TokenType.vacant);
        String currentValue = tokens.get(currentTokenIndex).getValue();
        while (!currentToken.getType().equals("end_command")) { // match 'vacant'
            prefGroup(tokens, currentToken);
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
        matchType(TokenType.run); // match 'RUN SIMULATION'
        matchKeywordValue(TokenType.run);
        matchType(TokenType.alphanumeric); // match 'NAME'
        assignVariable(tokens, currentToken);
        if (tokens.get(currentTokenIndex).getType().equals("end_command")) {
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
            System.out.println("'Run simulation' parsed successfully");
        } else {
            matchType(TokenType.phrase); // match 'FOR'
            matchPhraseValue(TokenType.phrase);
            String currentValue = tokens.get(currentTokenIndex).getValue();
            if (isInteger(currentValue)) { // number is integer
                matchType(TokenType.n_integer);
                matchIntegerValue(TokenType.n_integer);
            } else { // number is variable
                assignVariable(tokens, currentToken);
            }
            matchType(TokenType.phrase); // match 'TICKS'
            matchPhraseValue(TokenType.phrase);
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
            System.out.println("'Run simulation' parsed successfully");
        }
    }

    // 7. PARSER "REPORT SIMULATION 'NAME1', 'NAME2'..."
    private void parseReportSimulation() throws Exception {
        matchType(TokenType.report); // match 'REPORT SIMULATION'
        matchKeywordValue(TokenType.report);
        while (!currentToken.getType().equals("end_command")) { // match name
            matchType(TokenType.alphanumeric);
            assignVariable(tokens, currentToken);
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
        // check the values are type of moving, operator
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        String[] ar_ValueInEnum = TokenType.valueOf(currentToken.getType()).getValues();
        List<String> allowedTypes = Arrays.asList("identifier", "pref", "addOperator", "multiOperator");
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

    // 4. MATCH THE VALUE OF PHRASES "WITH", "FOR", "TICKS"...
    private void matchPhraseValue(TokenType expectedType) throws ParseException {
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        int previousIndex1 = currentTokenIndex - 1;
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
            if (!currentToken.getValue().trim().equals("AS") && !currentToken.getValue().trim().equals("GROUPS")) {
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

    // 5. MATCH INTEGER
    private void matchIntegerValue(TokenType expectedType) throws ParseException {
        // check the value is an integer
        String expectedValue = Objects.requireNonNull(SMLAParser.getTokenValue(String.valueOf(expectedType))).trim();
        if (currentToken.getType().equals("n_integer")) {
            if (!isInteger(currentToken.getValue().trim())) {
                handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
            }
        } else {
            handleSyntaxError(currentToken.getValue(), expectedValue, currentToken.getLineNumber());
        }
        // add node to the list of nodes and token to the list of tokens
        currentToken.getCommands().add(nameSimulation);
        addNodeAndToken(currentToken);
        // advance to the next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
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
            currentToken = new Token("EOF", "", "", "", 0);
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
    private String prefGroup(List<Token> tokens, Token currentToken) throws Exception {
        String returnValue = "";
        String currentValue = currentToken.getValue();
        float newValue = 0;
        // calculation (float), (float +|-|*|/ variable), (float +|-|*|/ float)
        if (isFloat(currentValue)) { // check token value is a float, fx. 7.0
            if (Float.parseFloat(currentValue) == 0) {
                throw new Exception("\nInvalid value: can't be zero, line " + currentToken.getLineNumber());
            }
            if (!isOperator(tokens.get(currentTokenIndex + 1).getValue())) { // check next token isn't '+,-,*,/'
                currentToken.getCommands().add(nameSimulation);
                returnValue = currentValue;
                if (tokens.get(currentTokenIndex - 1).getType().equals("variable")) {
                    tokens.get(currentTokenIndex - 1).setVariableValue(currentValue);
                    addNodeAndToken(tokens.get(currentTokenIndex - 1));
                } else {
                    addNodeAndToken(new Token("n_float", currentValue, nameSimulation, "",
                            tokens.get(currentTokenIndex).getLineNumber()));
                }
            } else {  // if exist '+,-,*,/' after current token
                newValue = Float.parseFloat(currentValue);
                String nextValue = (tokens.get(currentTokenIndex + 2).getValue());
                if (!isFloat(tokens.get(currentTokenIndex + 2).getValue())) { // after operator is variable, fx. 7.0 + x
                    returnValue = performMathOperation1(tokens, currentTokenIndex, nextValue, newValue);
                    currentTokenIndex += 2;
                } else { // after operator is a float,
                    returnValue = performMathOperation3(currentTokenIndex, newValue, Float.parseFloat(nextValue));
                    currentTokenIndex += 2;
                }
            }
            // calculation (variable), (variable +|-|*|/ variable), (variable +|-|*|/ float)
        } else if (isAlphanumeric(currentValue)) { // check token is variable, fx. y
            boolean found = false;
            int lineNum = currentToken.getLineNumber();
            for (Token t : tokens) {
                if (t.getValue().equals(currentValue) && t.getType().equals("variable")) { // search variable in the list
                    float variableValue = Float.parseFloat(t.getVariableValue()); // convert to float

                    newValue = variableValue; // get variable value from t token
                    if (!isOperator(tokens.get(currentTokenIndex + 1).getValue())) { // don't exist operator after variable
                        returnValue = Float.toString(variableValue);
                        if (tokens.get(currentTokenIndex - 1).getType().equals("variable")) {
                            tokens.get(currentTokenIndex - 1).setVariableValue(returnValue);
                            addNodeAndToken(tokens.get(currentTokenIndex - 1));
                        } else {
                            addNodeAndToken(new Token("n_float", Float.toString(variableValue),
                                    nameSimulation, "", lineNum));
                        }
                    } else { // exist operator after variable '+,-,*,/', fx. y +
                        String nextValue = (tokens.get(currentTokenIndex + 2).getValue()); // get value after operator
                        if (!isFloat(tokens.get(currentTokenIndex + 2).getValue())) { // exist variable after operator,
                            returnValue = performMathOperation1(tokens, currentTokenIndex, nextValue, newValue);
                            currentTokenIndex += 2;
                        } else { // after operator is float
                            returnValue = performMathOperation3(currentTokenIndex, newValue, Float.parseFloat(nextValue));
                            currentTokenIndex += 2;
                        }
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(" - New variable: " + tokens.get(currentTokenIndex - 1).getValue());
                if (tokens.get(currentTokenIndex - 1).getType().equals("variable")) {
                    tokens.get(currentTokenIndex - 1).setVariableValue(currentToken.getValue());
                    addNodeAndToken(tokens.get(currentTokenIndex - 1));
                }
            }
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        return returnValue;
    }

    // Match operator in pref
    private String performMathOperation1(List<Token> tokens, int currentTokenIndex,
                                         String nextValue, float newValue) throws Exception {
        String returnValue = "";
        for (Token t2 : tokens) {
            if (t2.getValue().equals(nextValue) && t2.getType().equals("variable")) { // search next variable in the list
                returnValue = performMathOperation2(t2, currentTokenIndex, newValue);
            }
        }
        return returnValue;
    }

    // Calculation two variables
    private String performMathOperation2(Token t2, int currentTokenIndex, float newValue) throws Exception {
        String returnValue = "";
        float variableValue = Float.parseFloat(t2.getVariableValue()); // convert value of next variable to float (t2)
        if (tokens.get(currentTokenIndex + 1).getValue().equals("+")) { // check '+' after the first variable (t)
            newValue += variableValue; // y + z
        } else if (tokens.get(currentTokenIndex + 1).getValue().equals("-")) {
            newValue -= variableValue; // y - z
            if (newValue <= 0) {
                throw new Exception("\nInvalid value: zero or negative, line " + currentToken.getLineNumber());
            }
        } else if (tokens.get(currentTokenIndex + 1).getValue().equals("*")) {
            newValue = newValue * variableValue; // y * z
        } else if (tokens.get(currentTokenIndex + 1).getValue().equals("/")) {
            if (variableValue == 0) {
                throw new Exception("\nInvalid value: cannot be divided by 0, line " + currentToken.getLineNumber());
            }
            newValue = newValue / variableValue; // y / z
        }
        float newValue1 = (float) (Math.round(newValue * 10.0) / 10.0);
        int lineNum = tokens.get(currentTokenIndex).getLineNumber();
        returnValue = String.valueOf(newValue1);
        if (tokens.get(currentTokenIndex - 1).getType().equals("variable")) {
            tokens.get(currentTokenIndex - 1).setVariableValue(returnValue);
            addNodeAndToken(tokens.get(currentTokenIndex - 1));
        } else {
            addNodeAndToken(new Token("n_float", String.valueOf(newValue1),
                    nameSimulation, "", lineNum));
        }
        return returnValue;
    }

    // Calculation one variable and one number
    private String performMathOperation3(int currentTokenIndex, float newValue, float nextValue) throws Exception {
        String returnValue = "";
        String nextValue1 = String.valueOf(tokens.get(currentTokenIndex + 1).getValue()).trim();
        if (nextValue1.equals("+")) {
            newValue += nextValue; // y + 5
        } else if (nextValue1.equals("-")) {
            newValue -= nextValue; // y - 5
            if (newValue <= 0) {
                throw new Exception("\nInvalid value: zero or negative, line " + currentToken.getLineNumber());
            }
        } else if (nextValue1.equals("*")) {
            newValue = newValue * nextValue; // y * 5
        } else if (nextValue1.equals("/")) {
            if (nextValue == 0) {
                throw new Exception("\nInvalid value: cannot be divided by 0, line " + currentToken.getLineNumber());
            }
            newValue = newValue / nextValue; // y / 5
        }
        float newValue1 = (float) (Math.round(newValue * 10.0) / 10.0);
        int lineNum = tokens.get(currentTokenIndex).getLineNumber();
        returnValue = String.valueOf(newValue1);
        if (tokens.get(currentTokenIndex - 1).getType().equals("variable")) {
            tokens.get(currentTokenIndex - 1).setVariableValue(returnValue);
            addNodeAndToken(tokens.get(currentTokenIndex - 1));
        } else {
            addNodeAndToken(new Token("n_float", String.valueOf(newValue1),
                    nameSimulation, "", lineNum));
        }
        return returnValue;
    }

    // Check string is operator
    private boolean isOperator(String s) {
        if (s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/")) {
            return true;
        } else {
            return false;
        }
    }

    // Assign value to variable
    public void assignVariable(List<Token> tokens, Token currentToken) throws Exception {
        int lineNum = currentToken.getLineNumber();
        String typ = "";
        boolean isNewVariable = true;
        for (Token t : tokens) {
            if (t.getValue().equals(currentToken.getValue()) && t.getType().equals("variable")) {
                if (isInteger(t.getVariableValue()) || isFloat(t.getVariableValue())) {
                    typ = "n_integer";
                    String currentValue = String.valueOf(Math.round(Float.parseFloat(t.getVariableValue())));
                    addNodeAndToken(new Token(typ, currentValue, "", "", lineNum));
                    int previousIndex1 = currentTokenIndex - 1;
                    String value_pre_index1 = tokens.get(previousIndex1).getValue();
                    if (value_pre_index1.equals("WITH") || value_pre_index1.equals("IS")) {
                        totalGroups = Integer.parseInt(currentValue);
                    }
                } else {
                    typ = "alphanumeric";
                    String currentValue = t.getVariableValue();
                    addNodeAndToken(new Token(typ, currentValue, "", "", lineNum));
                }
                currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
                isNewVariable = false;
                break;
            }
        }
        if (isNewVariable) {
            currentToken.getCommands().add(nameSimulation);
            addNodeAndToken(currentToken);
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        }
    }

    public static boolean matchNameSimulation(List<Token> tokens, String nameSimulation) throws Exception {
        int i = 0;
        for (Token t : tokens) {
            if (t.getValue().equals(nameSimulation)) {
                i++;
            }
        }
        if (i < 2) {
            throw new Exception("\nInvalid name of simulation report");
        }
        return true;
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

    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
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
        tokens.add(new Token("variable", "GROUP1", "30", "", 1));
        tokens.add(new Token("end_command", "end of command", "", "", 1));
        tokens.add(new Token("variable", "GROUP2", "25", "", 2));
        tokens.add(new Token("end_command", "end of command", "", "", 2));
        tokens.add(new Token("variable", "GROUP3", "20", "", 3));
        tokens.add(new Token("end_command", "end of command", "", "", 3));
        tokens.add(new Token("variable", "ABC", "25", "", 4));
        tokens.add(new Token("end_command", "end of command", "", "", 4));

        // Run simulation Example1 FOR 50 TICKS
        tokens.add(new Token("run", "RUN SIMULATION", "", "", 5));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 5));
        tokens.add(new Token("phrase", "FOR", "", "", 5));
        tokens.add(new Token("n_integer", "50", "", "", 5));
        tokens.add(new Token("phrase", "TICKS", "", "", 5));
        tokens.add(new Token("end_command", "end of command", "", "", 5));

        // Report simulation Example1
        tokens.add(new Token("report", "REPORT SIMULATION", "", "", 6));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 6));
        tokens.add(new Token("end_command", "end of command", "", "", 6));

        // Using random move
        tokens.add(new Token("using", "USING", "", "", 7));
        tokens.add(new Token("typeMoving", "RANDOM", "", "", 7));
        tokens.add(new Token("phrase", "MOVE", "", "", 7));
        tokens.add(new Token("end_command", "end of command", "", "", 7));

        // Setup simulation (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
        tokens.add(new Token("simulation", "SETUP SIMULATION", "", "", 8));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 8));
        tokens.add(new Token("phrase", "WITH", "", "", 8));
        tokens.add(new Token("n_integer", "4", "", "", 8));
        tokens.add(new Token("phrase", "AS", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP1", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP2", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP3", "", "", 8));
        tokens.add(new Token("alphanumeric", "ABC", "", "", 8));
        tokens.add(new Token("end_command", "end of command", "", "", 8));

        // Where pref is (10, 20, 30, 40)
        tokens.add(new Token("pref", "WHERE PREF IS", "", "", 9));
        tokens.add(new Token("n_integer", "10", "", "", 9));
        tokens.add(new Token("n_integer", "20", "", "", 9));
        tokens.add(new Token("n_integer", "30", "", "", 9));
        tokens.add(new Token("n_integer", "40", "", "", 9));
        tokens.add(new Token("end_command", "end of command", "", "", 9));

        // Where vacant is Vacant
        tokens.add(new Token("vacant", "WHERE VACANT IS", "", "", 10));
        tokens.add(new Token("n_integer", "65", "", "", 10));
        tokens.add(new Token("end_command", "end of command", "", "", 10));

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

        int j = 1;
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
    }
}
