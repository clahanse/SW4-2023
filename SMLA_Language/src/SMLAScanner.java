import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SMLAScanner {
    private Scanner scanner;
    private static List<Token> tokenList;

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
        int i = 0;

        while (inputScanner.hasNext()) { // process each line in the input string one by one
            String line = inputScanner.next();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(Pattern.compile("\\s+"));

            while (lineScanner.hasNext()) {  // process each token in the line one by one
                String token = lineScanner.next();
                if (token.equals("SETUP")) {
                    scanSetup(lineScanner, token); // scan "SETUP SIMULATION (NAME) WITH NUMBER GROUPS"
                } else if (token.equals("WHERE")) {
                    while (lineScanner.hasNext()) { // scan each token
                        nextToken = lineScanner.next();
                        if (nextToken.equals("TYPE")) { // scan "WHERE TYPE IS SCHELLING"
                            scanWhere(lineScanner, nextToken);
                        } else if (nextToken.equals("VACANT")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE VACANT IS NUMBER"
                        } else if (nextToken.equals("GROUPS")) {
                            scanGroups(lineScanner, nextToken); // scan "WHERE GROUPS WITH (A: green: 20,...)"
                        } else if (nextToken.equals("SIMILARITY")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE SIMILARITY IS NUMBER"
                        } else if (nextToken.equals("MOVING")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE MOVING IS RANDOM"
                        } else {
                            throw new Exception("Invalid command: WHERE " + nextToken);
                        }
                    }
                } else if (token.equals("RUN")) {
                    scanRun(lineScanner, token); // scan "RUN SIMULATION FOR (NUMBER|NO) TICKS"
                } else if (token.equals("CALCULATION")) {
                    scanCalculation(lineScanner, token); // scan the simple calculation (+,-,*,/)
                } else {
                    throw new Exception("Invalid token: " + token);
                }
            }
            // add an end-of-command token at the end of each line
            tokenList.add(new Token("end_command", "end of command", String.valueOf(i), 0));
            i++;
        }
    }

    // SCAN SETUP COMMAND
    public void scanSetup(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token); // store token value
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("SIMULATION")) { // check "SIMULATION"
                if (val.equals("SETUP")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "simulation",val); // save token "WHERE TYPE IS"
                }
            } else {
                throw new Exception("Invalid command SETUP");
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN GROUP OF "WHERE COMMANDS"
    public void scanWhere(Scanner inputScanner, String token) throws Exception {
        String val = "WHERE" + " " + String.valueOf(token);
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("IS")) { // check "IS"
                if (val.equals("WHERE TYPE")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "typesimulation",val);  // save token "WHERE TYPE IS"
                } else if (val.equals("WHERE VACANT")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "vacant",val); // save token "WHERE VACANT IS"
                } else if (val.equals("WHERE SIMILARITY")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "similarity",val);// save token "WHERE SIMILARITY IS"
                } else if (val.equals("WHERE MOVING")) {
                    val += " " + nextToken;
                    addUniqueToken(tokenList, "moving",val);  // save token "WHERE MOVING IS"
                }
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN WHERE GROUPS COMMAND
    public void scanGroups(Scanner inputScanner, String token) throws Exception {
        String val = "WHERE" + " " + String.valueOf(token);
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("WITH")) { // check "WITH"
                val += " " + nextToken;
                addUniqueToken(tokenList, "group",val);  // save token "WHERE GROUPS WITH"
            } else {
                throw new Exception("Invalid command WHERE GROUPS");
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN RUN COMMAND
    public void scanRun(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token); // store token value
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            String next = inputScanner.next();
            if (val.equals("RUN") && (next.equals("FOR"))) {
                val += " " + nextToken + " " + next;
                addUniqueToken(tokenList, "run",val);  // save token "WHERE TYPE IS"
            } else {
                throw new Exception("Invalid command RUN");
            }
            scanNextToken(inputScanner, nextToken);
        }
    }
    // SCAN SIMPLE CALCULATION
    public void scanCalculation(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token);
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (val.equals("CALCULATION")) {
                addUniqueToken(tokenList, "calculation","CALCULATION");
            }
            if (nextToken.matches("^(?!.[ABC]).*$")) {
                addUniqueToken(tokenList, "identifier",nextToken);}
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN NEXT TOKEN
    public void scanNextToken(Scanner inputScanner, String token) throws Exception {
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            String exactSt = extractString(nextToken);
            if (isInteger(nextToken)) {
                addUniqueToken(tokenList, "n_integer",exactSt);
            } else if (isAlphanumeric(exactSt)) {
                if (exactSt.equals("RED") || exactSt.equals("GREEN") || exactSt.equals("YELLOW")
                        || exactSt.equals("CYAN") || exactSt.equals("PINK") || exactSt.equals("BLUE")) {
                    addUniqueToken(tokenList, "color",exactSt);
                }else if (exactSt.equals("WITH") || exactSt.equals("GROUPS") || exactSt.equals("TICKS")) {
                    addUniqueToken(tokenList, "phrase",nextToken);
                }else if (exactSt.equals("RANDOM") || exactSt.equals("SCHELLING") ) {
                    addUniqueToken(tokenList, "typemoving", exactSt);
                }
                else if (isInteger(exactSt)) {
                    addUniqueToken(tokenList, "n_integer",exactSt);
                }
                else {
                    addUniqueToken(tokenList, "letterordigit",exactSt);
                }
            } else if (nextToken.equals("=")) {
                addUniqueToken(tokenList, "equal",nextToken);
            } else if (nextToken.equals("+") || nextToken.equals("-")) {
                addUniqueToken(tokenList, "addoperator",nextToken);
            } else if (nextToken.equals("*") || nextToken.equals("/")) {
                addUniqueToken(tokenList, "multoperator",nextToken);
            } else {
                throw new Exception("Invalid character: " + nextToken);
            }
        }
    }

    // ADD EACH TOKEN INTO THE LIST
    public void addUniqueToken(List<Token> tokenList, String typ, String val) {
        tokenList.add(new Token(typ, val, "", 0));
    }

    // GET EXACTLY STRING
    public static String extractString(String input) {
        // Define the regular expression pattern
        Pattern pattern = Pattern.compile("^[#(]|[,:)]$");
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

    // CHECK ÌF STRING IS ONLY ALPHABETS AND NUMBERS
    public static boolean isAlphanumeric(String str) {
        return str != null && str.matches("^[a-zA-Z0-9]+$");
    }

    public static String toUpperCase(String input) {
        return input.toUpperCase();
    }

    // CLASS TOKEN
    public class Token {
        private String typ;
        private String val;
        private int lineNumber;
        private List<String> commands;

        public Token(String typ, String val, String command, int lineNumber) {
            this.typ = typ;
            this.val = val;
            this.commands = new ArrayList<>();
            this.commands.add(command);
            this.lineNumber = lineNumber;
        }

        public String getVal() {
            return val;
        }

        public String getTyp() {
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

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

    // FUNCTION GETS "tokenList"
    public static List<Token> getTokenList() {
        return tokenList;
    }

    // MAIN FUNCTION
   /* public static void main(String[] args) throws Exception {
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
        System.out.println("\nPrint Token from List: ");
        int i = 1;
        for (Token token : getTokenList()) {
            System.out.println(i + "-type: " + token.getTyp() + ", value: " + token.getVal());
            i++;
        }
    }*/
}

