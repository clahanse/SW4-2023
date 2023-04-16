
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

    // MAIN FUNCTION SCANNING AND DIVIDING INPUT DATA INTO TOKENS
    public void tokenizeInput(String input) throws Exception {
        Scanner inputScanner = new Scanner(input);  // scan input string
        // split the input string into tokens based on whitespace
        inputScanner.useDelimiter(Pattern.compile("\\s+"));
        String nextToken = ""; // store the next token
        String typ = "";  // store token type
        String val = "";  // store token value

        while (inputScanner.hasNext()) {  // process each token in the input string one by one
            String token = inputScanner.next();
            if (token.equals("SETUP")) {
                scanSetup(inputScanner, token); // scan "SETUP SIMULATION (NAME) WITH NUMBER GROUPS"
            } else if (token.equals("WHERE")) {
                while (inputScanner.hasNext()) { // scan each token
                    nextToken = inputScanner.next();
                    if (nextToken.equals("TYPE")) { // scan "WHERE TYPE IS SCHELLING"
                        scanWhere(inputScanner, nextToken);
                    } else if (nextToken.equals("VACANT")) {
                        scanWhere(inputScanner, nextToken); // scan "WHERE VACANT IS NUMBER"
                    } else if (nextToken.equals("GROUPS")) {
                        scanWhereGroups(inputScanner, nextToken); // scan "WHERE GROUPS WITH (A: green: 20,...)"
                    } else if (nextToken.equals("SIMILARITY")) {
                        scanWhere(inputScanner, nextToken); // scan "WHERE SIMILARITY IS NUMBER"
                    } else if (nextToken.equals("MOVING")) {
                        scanWhere(inputScanner, nextToken); // scan "WHERE MOVING IS RANDOM"
                    } else {
                        addData(nextToken); // store any input data into list
                    }
                }
            } else if (token.equals("RUN")) {
                scanRun(inputScanner, token); // scan "RUN SIMULATION FOR (NUMBER|NO) TICKS"
            } else if (token.equals("a") || token.equals("b") || token.equals("c")) {
                scanCalculation(inputScanner, token); // scan the simple calculation (+,-,*,/)
            } else {
                val = token;
                addData(val);
            }
        }
    }

    // FUNCTION SCANS "SETUP SIMULATION (NAME) WITH NUMBER GROUPS"
    public void scanSetup(Scanner inputScanner, String token) {
        String val = String.valueOf(token);
        String typ;
        int i = 1;

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (i == 1) {
                if (nextToken.equals("SIMULATION")) { // check "SIMULATION"
                    val += " " + nextToken;
                    typ = "simulation";
                    addUniqueToken(tokenList, new Token(typ, val, ""));  // store "SETUP SIMULATION" into tokenList
                    i++;
                    // print to console to check
                    System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
                } else {
                    val += " " + nextToken;
                    addData(val);
                    i++;
                }
            } else if (i == 2) {  // check NAME
                val = nextToken;
                typ = "letterordigit";
                addUniqueToken(tokenList, new Token(typ, val, "")); // store token "NAME" into tokenList
                i++;
                // print to console to check
                System.out.println("2- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else if (i == 3) { // check "WITH"
                val = nextToken;
                typ = "letterordigit";
                addUniqueToken(tokenList, new Token(typ, val, "")); // store token "WITH" into tokenList
                i++;
                // print to console to check
                System.out.println("3- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else if (i == 4) {  // check "NUMBER"
                val = nextToken;
                typ = "integer";
                addUniqueToken(tokenList, new Token(typ, val, "setup")); // store token "NUMBER" into tokenList
                i++;
                // print to console to check
                System.out.println("4- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else if (i == 5) { // check "GROUPS"
                val = nextToken;
                typ = "letterordigit";
                addUniqueToken(tokenList, new Token(typ, val, "")); // store token "GROUPS" into tokenList
                i++;
                // print to console to check
                System.out.println("5- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else {
                addData(nextToken);
                i++;
            }
        }
    }

    // FUNCTION SCANS GROUP OF "WHERE COMMANDS"
    public void scanWhere(Scanner inputScanner, String token) {
        String val = "WHERE" + " " + String.valueOf(token);
        String typ;
        int i = 1;

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (i == 1) {
                if (nextToken.equals("IS")) { // check "IS"
                    val += " " + nextToken;
                    typ = "typesimulation";
                    addUniqueToken(tokenList, new Token(typ, val, ""));  // store token "WHERE TYPE IS" into tokenList
                    i++;
                    // print to console to check
                    System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
                } else {
                    val += " " + nextToken;
                    addData(val);
                    i++;
                }
            } else if (i == 2) {  // check "SCHELLING, NUMBER, RANDOM"
                val = nextToken;
                if (isInteger(val)) {
                    typ = "integer";
                } else {
                    typ = "letterordigit";
                }
                addUniqueToken(tokenList, new Token(typ, val, "")); // store token into tokenList
                i++;
                // print to console to check
                System.out.println("2- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else {
                addData(nextToken);
                i++;
            }
        }
    }

    // FUNCTION SCANS "WHERE GROUPS WITH (A: green: 20,...)"
    public void scanWhereGroups(Scanner inputScanner, String token) throws Exception {
        String val = "WHERE" + " " + String.valueOf(token);
        String typ;

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (nextToken.equals("WITH")) { // check "WITH"
                val += " " + nextToken;
                typ = "group";
                addUniqueToken(tokenList, new Token(typ, val, ""));  // store token "WHERE TYPE IS" into tokenList
                // print to console to check
                System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else {
                val += " " + nextToken;
                addData(val);
            }
            // scan groups (NAME1: COLOR1: RATIO1, ... )
            String line = inputScanner.nextLine(); // scan line
            Scanner lineScanner = new Scanner(line);
            int n = 1; // mark ratio command
            while (lineScanner.hasNext()) { // scan each token
                String group = lineScanner.next(); // scan name
                String color = lineScanner.next(); // scan color
                String ratio = lineScanner.next(); // scan ratio
                // Input group, color, and ratio strings in function ScanGroup to create and store tokens
                ScanGroup(group, color, ratio, String.valueOf(n));
                n++;
            }
        }
    }

    // FUNCTION SCANS "RUN SIMULATION FOR (NUMBER|NO) TICKS"
    public void scanRun(Scanner inputScanner, String token) {
        String val = String.valueOf(token);
        String typ;
        int i = 1;

        while (inputScanner.hasNext()) { // scan each token
            String nextToken = inputScanner.next();
            if (i == 1) {
                if (nextToken.equals("SIMULATION")) { // check "SIMULATION"
                    val += " " + nextToken;
                    i++;
                } else {
                    val += " " + nextToken;
                    i++;
                }
            } else if (i == 2) {  // check "FOR"
                if (nextToken.equals("FOR")) { // check "FOR"
                    val += " " + nextToken;
                    typ = "simulation";
                    addUniqueToken(tokenList, new Token(typ, val, ""));  // store "SETUP SIMULATION" into tokenList
                    i++;
                    // print to console to check
                    System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
                } else {
                    val += " " + nextToken;
                    addData(val);
                    i++;
                }
            } else if (i == 3) { // check "NUMBER TICKS/NO"
                val = nextToken;
                if (isInteger(val)) {
                    typ = "integer";
                } else {
                    typ = "letterordigit";
                }
                addUniqueToken(tokenList, new Token(typ, val, "")); // store token into tokenList
                i++;
                // print to console to check
                System.out.println("3- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else if (i == 4) {  // check "TICKS"
                val = nextToken;
                typ = "letterordigi";
                addUniqueToken(tokenList, new Token(typ, val, "setup")); // store token "NUMBER" into tokenList
                i++;
                // print to console to check
                System.out.println("4- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else {
                addData(nextToken);
                i++;
            }
        }
    }

    // FUNCTION SCANS THE SIMPLE CALCULATION (+,-,*,/)
    public void scanCalculation(Scanner inputScanner, String token) throws Exception {
        String val = String.valueOf(token);
        String typ;

        if (val.contains("a") || val.contains("b") || val.contains("c")) {
            typ = "letter";
            addUniqueToken(tokenList, new Token(typ, val, "")); // store token "a|b|c" into tokenList
            // print to console to check
            System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
        } else {
            throw new Exception("Expected 'a|b|c' but found " + val);
        }
        String assign = inputScanner.next();
        if (assign.equals("=")) {
            val = assign;
            typ = "equal";
            addUniqueToken(tokenList, new Token(typ, val, "")); // store token "=" into tokenList
            // print to console to check
            System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
        } else {
            throw new Exception("Expected '=' but found " + assign);
        }
        while (inputScanner.hasNext()) { // scan each token

            String nextToken = inputScanner.next();
            val = nextToken;
            if (isInteger(val)) {
                typ = "integer";
                addUniqueToken(tokenList, new Token(typ, val, ""));  // store number into tokenList
                // print to console to check
                System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else if (val.contains("*") || val.contains("/") || val.contains("+") || val.contains("-")) {
                if (val.contains("*") || val.contains("/")) {
                    typ = "multoperator";
                }
                if (val.contains("+") || val.contains("-")) {
                    typ = "addoperator";
                }
                addUniqueToken(tokenList, new Token(typ, val, ""));  // store operator into tokenList

                // print to console to check
                System.out.println("1- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
            } else {
                throw new Exception("Expected 'number' or 'operator' but found " + val);
            }
        }
    }

    // FUNCTION STORES GROUP OF THE TOKENS (NAME: COLOR: RATIO) INTO THE LIST
    public void ScanGroup(String group1, String color1, String ratio1, String command) throws Exception {
        String val, typ;
        val = String.valueOf(group1);
        typ = "letterordigit";
        addUniqueToken(tokenList, new Token(typ, val, "")); // store token "name of group" into tokenList
        // print to console to check
        System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
        val = String.valueOf(color1);
        typ = "color";
        addUniqueToken(tokenList, new Token(typ, val, "")); // store token "color of group" into tokenList
        // print to console to check
        System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
        val = String.valueOf(ratio1);
        typ = "integer";
        addUniqueToken(tokenList, new Token(typ, val, command)); // store token "ratio of group" into tokenList
        // print to console to check
        System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
    }

    // FUNCTION ADDS EACH TOKEN INTO THE LIST
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

    // FUNCTION ADDS OTHER DATA INTO THE LIST
    private void addData(String val) {
        String typ = "unknown";
        addUniqueToken(tokenList, new Token(typ, val, ""));
        System.out.println("- Token(type: " + typ + "," + " " + "value: " + "'" + val + "'" + ")");
    }

    // FUNCTION GETS EXACTLY STRING
    public static String extractString(String input) {
        // Define the regular expression pattern
        Pattern pattern = Pattern.compile("^[#(]|[,:)]$");
        input = pattern.matcher(input).replaceAll("");
        return input;
    }

    // FUNCTION TO CHECK IF STRING IS INTEGER
    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // CLASS TOKEN
    public class Token {
        private String typ;
        private String val;
        private List<String> commands;

        public Token(String typ, String val, String command) {
            this.typ = typ;
            this.val = val;
            this.commands = new ArrayList<>();
            this.commands.add(command);
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
        while (continueScanning) {
            System.out.print("\n Enter a command ('exit' to quit): ");
            String input = scanner.nextLine();
            System.out.println("\n Print Token from variables: ");
            if (input.equals("exit")) {
                continueScanning = false;
            } else {
                smlaScanner.tokenizeInput(input);
                System.out.println("\n Print Token from List: ");
                int i = 1;
                for (Token token : smlaScanner.getTokenList()) {
                    System.out.println(i + "-type: " + token.getTyp() + ", value: " + token.getVal());
                    i++;
                }
            }
        }
    }
}


