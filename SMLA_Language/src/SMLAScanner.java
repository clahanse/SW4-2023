import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class SMLAScanner {
    private Scanner scanner;
    private static List<Token> tokenList;
    int lineNum = 1;

    public SMLAScanner(Scanner scanner) { // constructor
        this.scanner = scanner;
        this.tokenList = new ArrayList<>(); // list stores tokens
    }

    public SMLAScanner() { // constructor
    }

    // MAIN FUNCTION SCANNING AND DIVIDING INPUT DATA INTO TOKENS
    public void tokenizeInput(String input) throws Exception {
        Scanner inputScanner = new Scanner(input);  // scan input string
        // split the input string into lines
        inputScanner.useDelimiter(Pattern.compile("[\r\n]+"));
        String nextToken = ""; // store the next token

        while (inputScanner.hasNext()) { // process each line in the input string one by one
            String line = inputScanner.next();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(Pattern.compile("\\s+"));
            if (line.matches("\\s*//.*")) { // skip comment lines
                continue;
            }
            while (lineScanner.hasNext()) {  // process each token in the line one by one
                String token = lineScanner.next();
                switch (token) {
                    case "SETUP" -> scanSetupRunReport(lineScanner, token); // scan "SETUP SIMULATION..."
                    case "WHERE" -> {
                        if (!lineScanner.hasNext()) {
                            throw new Exception("\nInvalid command on line " + lineNum + ": WHERE is missing word");
                        }
                        while (lineScanner.hasNext()) {
                            nextToken = lineScanner.next();
                            switch (nextToken) {
                                case "PREF", "VACANT" -> scanWhere(lineScanner, nextToken);
                                default ->
                                        throw new Exception("\nInvalid command on line " + lineNum + ": WHERE " + nextToken);
                            }
                        }
                    }
                    case "USING" -> scanUsing(lineScanner, token); // scan "USING RANDOM..."
                    case "RUN", "REPORT" ->
                            scanSetupRunReport(lineScanner, token); // scan "RUN" and "REPORT SIMULATION..."
                    default -> scanVariable(lineScanner, token); // scan Variable
                }
            }
            // add an end-of-command token at the end of each line
            if (!line.startsWith("//")) { // skip adding end_command for comment lines
                tokenList.add(new Token("end_command", "end of command", lineNum));
            }
            lineNum++;
        }
    }


    // SCAN SETUP, RUN AND REPORT COMMAND
    public void scanSetupRunReport(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token); // store token value
        if (!inputScanner.hasNext()) {
            throw new Exception("\nError on line " + lineNum + " 'command' is missing word");
        }
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("SIMULATION")) { // check "SIMULATION"
                if (val.equals("SETUP")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "simulation", val, lineNum); // save token "SETUP SIMULATION..."
                }
                if (val.equals("RUN")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "run", val, lineNum); // save token "RUN SIMULATION..."
                }
                if (val.equals("REPORT")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "report", val, lineNum); // save token "REPORT SIMULATION..."
                }
            } else {
                throw new Exception("\nInvalid command on line " + lineNum + ", unexpected: " + nextToken);
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN WHERE PREF AND WHERE VACANT COMMANDS
    public void scanWhere(Scanner inputScanner, String token) throws Exception {
        String val = "WHERE" + " " + String.valueOf(token);
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("IS")) { // check "IS"
                switch (val) {
                    case "WHERE PREF" -> {
                        val += " " + nextToken;
                        addUniqueToken(tokenList, "pref", val, lineNum);  // save token "WHERE PREF IS..."
                    }
                    case "WHERE VACANT" -> {
                        val += " " + nextToken;
                        addUniqueToken(tokenList, "vacant", val, lineNum); // save token "WHERE VACANT IS..."
                    }
                }
            } else {
                throw new Exception("\nInvalid command on line " + lineNum + ", unexpected: " + nextToken);
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN MOVING COMMAND
    public void scanUsing(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token);
        if (!inputScanner.hasNext()) {
            throw new Exception("\nError on line " + lineNum + " 'using command' is missing word");
        }
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (val.equals("USING")) {
                addUniqueToken(tokenList, "using", val, lineNum);
            }
            if (nextToken.equals("RANDOM")||nextToken.equals("SCHELLING")) {
                addUniqueToken(tokenList, "typeMoving", nextToken, lineNum);
            } else {
                addUniqueToken(tokenList, "alphanumeric", nextToken, lineNum);
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN VARIABLE COMMAND
    public void scanVariable(Scanner inputScanner, String token) throws Exception {
        String val = extractString(token);
        if (isAlphanumeric(val)&& !val.equals("setup")&&!val.equals("where")&&
                !val.equals("run")&&!val.equals("using")&&!val.equals("report")) {
            if (!inputScanner.hasNext()) {
                throw new Exception("\nError on line " + lineNum + " Missing equal '=' or value of variable");
            }
            String nextToken = inputScanner.next();
            if (!nextToken.equals("=")) {
                throw new Exception("\nError on line " + lineNum + " Missing equal '='");
            }
            if (!inputScanner.hasNext()) {
                throw new Exception("\nError on line " + lineNum + " Missing value of variable");
            }
            nextToken = inputScanner.next();
            if (inputScanner.hasNextInt() || inputScanner.hasNextFloat() || isAlphanumeric(extractString(nextToken))) {
                addUniqueToken(tokenList, "variable", val, extractString(nextToken), lineNum);
            } else {
                throw new Exception("\nError on line " + lineNum + " Invalid value of variable");
            }
            if (inputScanner.hasNext()) {
                throw new Exception("\nError on line " + lineNum + " Invalid value of variable");
            }
        } else {
            throw new Exception("\nInvalid command on line " + lineNum + ", unexpected: " + token);
        }
    }

    // SCAN NEXT TOKEN
    public void scanNextToken(Scanner inputScanner, String token) throws Exception {
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            String exactSt = extractString(nextToken);
            if (isInteger(nextToken)) {
                addUniqueToken(tokenList, "n_integer", exactSt, lineNum);
            } else if (isAlphanumeric(exactSt)) {
                if (exactSt.equals("RED") || exactSt.equals("GREEN") || exactSt.equals("YELLOW")
                        || exactSt.equals("CYAN") || exactSt.equals("PINK") || exactSt.equals("BLUE")) {
                    addUniqueToken(tokenList, "color", exactSt, lineNum);
                } else if (exactSt.equals("WITH") || exactSt.equals("GROUPS") || exactSt.equals("TICKS")
                        || exactSt.equals("FOR") || exactSt.equals("MOVE") || exactSt.equals("AS")) {
                    addUniqueToken(tokenList, "phrase", nextToken, lineNum);
                } else if (isInteger(exactSt)) {
                    addUniqueToken(tokenList, "n_integer", exactSt, lineNum);
                } else {
                    addUniqueToken(tokenList, "alphanumeric", exactSt, lineNum);
                }
            } else if (nextToken.equals("=")) {
                addUniqueToken(tokenList, "assignmentOperator", nextToken, lineNum);
            } else if (nextToken.equals("+") || nextToken.equals("-")) {
                addUniqueToken(tokenList, "addOperator", nextToken, lineNum);
            } else if (nextToken.equals("*") || nextToken.equals("/")) {
                addUniqueToken(tokenList, "multiOperator", nextToken, lineNum);
            } else {
                throw new Exception("\nInvalid command on line " + lineNum + ", unexpected: " + nextToken);
            }
        }
    }

    // ADD EACH TOKEN INTO THE LIST
    public void addUniqueToken(List<Token> tokenList, String typ, String val, int lineNumber) {
        tokenList.add(new Token(typ, val, lineNum));
    }

    public void addUniqueToken(List<Token> tokenList, String typ, String val, String variable, int lineNumber) {
        tokenList.add(new Token(typ, val, variable, "", lineNumber));
    }

    // GET EXACTLY STRING
    public static String extractString(String input) {
        // Define the regular expression pattern
        Pattern pattern = Pattern.compile("^[#(\"\']|[:,),\"\']$");

        // Pattern pattern = Pattern.compile("^[#(]|[,:)]$");
        input = pattern.matcher(input).replaceAll("");
        return input;
    }

    // CHECK IF STRING IS INTEGER
    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // CHECK IF STRING IS ONLY ALPHABETS AND NUMBERS
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9_]+$");
    }

    public static String toUpperCase(String input) {
        return input.toUpperCase();
    }

    // CLASS TOKEN


    // FUNCTION GETS "tokenList"
    public static List<Token> getTokenList() {
        return tokenList;
    }

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        SMLAScanner smlaScanner = new SMLAScanner(scanner);

        boolean continueScanning = true;
        StringBuilder input = new StringBuilder();
        System.out.println("\nEnter your commands (type 'run' to execute):");
        while (continueScanning) {
            getTokenList().clear();
            String line = scanner.nextLine();
            if (line.equals("run")) {
                continueScanning = false;
            } else {
                String UpperLine = toUpperCase(line);
                input.append(UpperLine).append("\n");
            }
        }
        getTokenList().clear(); // reset the TokenList before scanning new input
        smlaScanner.tokenizeInput(input.toString());

     /*  File inputFile = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\input.txt");
        if (!inputFile.exists()) {
            throw new Exception("Input file does not exist.");
        }
        if (inputFile.length() == 0) {
            throw new Exception("File is empty");
        }
        Scanner scanner = new Scanner(inputFile);
        SMLAScanner smlaScanner = new SMLAScanner(scanner);
        //  List<Token> tokens = new ArrayList<>();
        List<Token> tokensParser = new ArrayList<>();
        StringBuilder input = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue; // Skip blank lines
            }
            String UpperLine = toUpperCase(line);
            input.append(UpperLine).append("\n");
        }
        // tokenize input using scanner
        smlaScanner.tokenizeInput(input.toString());*/

        System.out.println("\nPrint Token from List: ");
        int i = 1;
        for (Token token : getTokenList()) {
            // tokens.add(token);
            if (token.getType().equals("variable")) {
                System.out.println(i + "-type: " + token.getType() + ", value: " +
                        token.getValue() + ", variable value: " + token.getVariableValue() + ", line: " + token.getLineNumber());
            } else {
                System.out.println(i + "-type: " + token.getType() + ", value: " + token.getValue() +
                        ", line: " + token.getLineNumber());
            }
            i++;
        }
    }
}



