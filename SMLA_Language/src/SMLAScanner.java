import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SMLAScanner {
    private Scanner scanner;
    private List<Token> tokenList;

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
        String val = "";  // store token value
        int i = 0;
        while (inputScanner.hasNext()) { // process each line in the input string one by one
            String line = inputScanner.next();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(Pattern.compile("\\s+"));

            while (lineScanner.hasNext()) {  // process each token in the line one by one
                String token = lineScanner.next();
                if (token.equals("SETUP")) {
                    scanSetupRun(lineScanner, token); // scan "SETUP SIMULATION (NAME) WITH NUMBER GROUPS"
                } else if (token.equals("WHERE")) {
                    while (lineScanner.hasNext()) { // scan each token
                        nextToken = lineScanner.next();
                        if (nextToken.equals("TYPE")) { // scan "WHERE TYPE IS SCHELLING"
                            scanWhere(lineScanner, nextToken);
                        } else if (nextToken.equals("VACANT")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE VACANT IS NUMBER"
                        } else if (nextToken.equals("GROUPS")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE GROUPS WITH (A: green: 20,...)"
                        } else if (nextToken.equals("SIMILARITY")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE SIMILARITY IS NUMBER"
                        } else if (nextToken.equals("MOVING")) {
                            scanWhere(lineScanner, nextToken); // scan "WHERE MOVING IS RANDOM"
                        } else {
                            throw new Exception("Invalid command: WHERE " + nextToken);
                        }
                    }
                } else if (token.equals("RUN")) {
                    scanSetupRun(lineScanner, token); // scan "RUN SIMULATION FOR (NUMBER|NO) TICKS"
                } else if (token.equals("CALCULATION")) {
                    scanCalculation(lineScanner, token); // scan the simple calculation (+,-,*,/)
                } else {
                    throw new Exception("Invalid token: " + token);
                }
            }
            // add an end-of-command token at the end of each line
            addUniqueToken(tokenList, new Token("end_command", "end of command", String.valueOf(i), 0));
            i++;
        }
    }

    // SCAN SETUP AND RUN COMMAND
    public void scanSetupRun(Scanner inputScanner, String token) throws Exception {
        String typ = "";  // store token type
        String val = String.valueOf(token); // store token value

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("SIMULATION")) { // check "SIMULATION"
                if (val.equals("SETUP")) {
                    val += " " + nextToken;
                    typ = "simulation";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                }
                if (val.equals("RUN")) {
                    val += " " + nextToken;
                    typ = "run";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                }
            } else {
                throw new Exception("Invalid command SETUP or RUN");
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN GROUP OF "WHERE COMMANDS"
    public void scanWhere(Scanner inputScanner, String token) throws Exception {
        String val = "WHERE" + " " + String.valueOf(token);
        String typ;

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("IS")) { // check "IS"
                if (val.equals("WHERE TYPE")) {
                    val += " " + nextToken;
                    typ = "typesimulation";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                } else if (val.equals("WHERE VACANT")) {
                    val += " " + nextToken;
                    typ = "vacant";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                } else if (val.equals("WHERE SIMILARITY")) {
                    val += " " + nextToken;
                    typ = "similarity";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                } else if (val.equals("WHERE MOVING")) {
                    val += " " + nextToken;
                    typ = "moving";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                }
            } else if (nextToken.equals("WITH")) { // check "WITH"
                if (val.equals("WHERE GROUPS")) {
                    val += " " + nextToken;
                    typ = "groups";
                    addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                }
            } else {
                throw new Exception("Invalid command WHERE");
            }
            scanNextToken(inputScanner, nextToken);
        }
    }

    // SCAN SIMPLE CALCULATION
    public void scanCalculation(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token);
        String typ;
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (val.equals("CALCULATION")) {
                typ = "calculation";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));  // save token "WHERE TYPE IS"
                scanNextToken(inputScanner, nextToken);
            }
        }
    }

    // SCAN NEXT TOKEN
    public void scanNextToken(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token);
        String typ;
        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (isInteger(nextToken)) {
                val = nextToken;
                typ = "n_integer";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (isAlphanumeric(extractString(nextToken))) {
                val = extractString(nextToken);
                typ = "letterordigit";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (nextToken.equals("RED") || nextToken.equals("GREEN") || nextToken.equals("YELLOW")
                    || nextToken.equals("CYAN") || nextToken.equals("PINK")) {
                val = nextToken;
                typ = "color";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (nextToken.equals("a") || nextToken.equals("b") || nextToken.equals("c")) {
                val = nextToken;
                typ = "indentifier";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (nextToken.equals("=")) {
                val = nextToken;
                typ = "equal";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (nextToken.equals("+") || nextToken.equals("-")) {
                val = nextToken;
                typ = "addoperator";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else if (nextToken.equals("*") || nextToken.equals("/")) {
                val = nextToken;
                typ = "multoperator";
                addUniqueToken(tokenList, new Token(typ, val, "", 0));
            } else {
                throw new Exception("Invalid character: " + nextToken);
            }
        }
    }

    // ADD EACH TOKEN INTO THE LIST
    public static void addUniqueToken(List<Token> tokenList, Token newToken) {
        String exactVal = extractString(newToken.getVal());
        newToken.addVal(exactVal);
        boolean exists = false;
        for (Token t : tokenList) {
            if (t.getTyp().equals(newToken.getTyp()) && t.getVal().equals(newToken.getVal())) {
                if (newToken.getCommands().equals(t.getCommands())) { // check if the new token is not unique
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) { // if the new token is unique
            tokenList.add(newToken);
        }
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
    public List<Token> getTokenList() {
        return tokenList;
    }

    // MAIN FUNCTION
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        SMLAScanner smlaScanner = new SMLAScanner(scanner);
        boolean continueScanning = true;
        StringBuilder input = new StringBuilder();
        System.out.println("\nEnter your commands (type 'run' to execute):");
        while (continueScanning) {
            smlaScanner.getTokenList().clear();
            String line = scanner.nextLine();
            if (line.equals("run")) {
                continueScanning = false;
            } else {

                String UpperLine = toUpperCase(line);
                input.append(UpperLine).append("\n");
            }
        }
        smlaScanner.getTokenList().clear(); // reset the TokenList before scanning new input
        smlaScanner.tokenizeInput(input.toString());
        System.out.println("\nPrint Token from List: ");
        int i = 1;
        for (Token token : smlaScanner.getTokenList()) {
            System.out.println(i + "-type: " + token.getTyp() + ", value: " + token.getVal());
            i++;
        }
    }
}



