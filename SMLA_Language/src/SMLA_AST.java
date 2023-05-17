import java.util.ArrayList;
import java.util.List;

public class SMLA_AST {
    private static int currentTokenIndex;
    private static Token currentToken;
    private List<Token> tokens;
    private int numGroups;
    private List<CommandNode> nodes;

    public SMLA_AST(List<Token> tokens) {
        this.tokens = tokens;
        currentTokenIndex = 0;
        currentToken = tokens.get(0);
        nodes = new ArrayList<>();
    }

    public List<CommandNode> getNodes() {
        return nodes;
    }

    public void parseProgram() throws Exception {
        parseCommands();
    }

    // MAIN FUNCTION
    public void parseCommands() throws Exception {
        currentToken = tokens.get(currentTokenIndex);
        while (currentTokenIndex < tokens.size() && !currentToken.getType().equals("EOF")) {
            switch (currentToken.getType()) {
                case "simulation" -> parseSimulation(tokens); // parse 'setup simulation command'
                case "pref" -> parsePref(tokens); // parse 'pref command'
                case "vacant" -> parseVacant(tokens); // parse 'vacant command'
                case "using" -> parseUsing(tokens); // parse 'using command'
                case "run" -> parseRun(tokens); // parse 'run simulation command'
                case "report" -> parseReport(tokens); // parse 'report command'
                case "variable" -> parseVariable(tokens); // parse 'variable command'
                default -> throw new IllegalArgumentException("\nInvalid statement type at line: "
                        + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue());
            }
        }
    }

    // PARSER VARIABLE COMMAND
    private void parseVariable(List<Token> tokens) throws Exception {
        if (tokens.get(currentTokenIndex).getType().equals("variable") ||
                tokens.get(currentTokenIndex).getType().equals("alphanumeric")) {
            List<String> identifiers = new ArrayList<>();
            identifiers.add(tokens.get(currentTokenIndex).getValue()); // add name of variable
            identifiers.add(tokens.get(currentTokenIndex).getVariableValue()); // add value of variable
            CommandNode.VariableCommandNode node = new CommandNode.VariableCommandNode();
            node.setIdentifiers(identifiers); // add variable and its value into node
            printAST(node); // print node to console
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex); // move to next token

        } else {
            throw new Exception("Invalid token sequence");
        }
    }

    // PARSER SETUP SIMULATION COMMAND
    private void parseSimulation(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("simulation")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("SETUP SIMULATION")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: SETUP SIMULATION");
        }
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex++);
        Token identifierToken = tokens.get(currentTokenIndex);
        String keyword = "SETUP";
        // check name of simulation
        if (!identifierToken.getType().equals(SMLAParser.TokenType.alphanumeric.toString())) {
            throw new Exception("Expected an alphanumeric identifier");
        }
        String identifier = identifierToken.getValue();
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex); // move next token
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        Token nIntegerToken = tokens.get(currentTokenIndex);
        // check number of groups
        if (!nIntegerToken.getType().equals(SMLAParser.TokenType.n_integer.toString())) {
            throw new Exception("Expected an n_integer value");
        }
        int nInteger = Integer.parseInt(nIntegerToken.getValue());
        numGroups = nInteger;
        String nextValue = tokens.get(currentTokenIndex + 1).getValue();
        if (nextValue.equals("GROUPS")) {
            CommandNode.SimulationCommandNode node = new CommandNode.SimulationCommandNode();
            node.setKeyword(keyword);
            node.setIdentifier(identifier);
            node.setNInteger(nInteger);
            nodes.add(node);
            printAST(node);
            currentTokenIndex++;
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        } else if (nextValue.equals("AS")) {
            currentTokenIndex = currentTokenIndex + 2;
            List<String> nFloats = new ArrayList<>();
            // check and add groups into identifiers
            for (int i = currentTokenIndex; i < currentTokenIndex + numGroups; i++) {
                Token token = tokens.get(i);
                if (token.getType().equals(SMLAParser.TokenType.n_float.toString())) {
                    nFloats.add(token.getValue());
                }
            }
            // add keyword, name, number and groups into node
            CommandNode.SimulationCommandNode node = new CommandNode.SimulationCommandNode();
            node.setKeyword(keyword);
            node.setIdentifier(identifier);
            node.setNInteger(nInteger);
            node.setNFloats(nFloats);
            nodes.add(node);
            printAST(node);
            currentTokenIndex = currentTokenIndex + (numGroups - 1);
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        }
    }

    // PARSER PREF COMMAND
    private void parsePref(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("pref")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("WHERE PREF IS")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: WHERE PREF IS");
        }
        List<String> nFloats = new ArrayList<>();
        String keyword = "PREF";
        currentTokenIndex++; // move past the "pref" keyword
        // check and add prefs into list nIntegers
        for (int i = currentTokenIndex; i < currentTokenIndex + numGroups; i++) {
            Token token = tokens.get(i);
            if (token.getType().equals(SMLAParser.TokenType.n_float.toString())) {
                nFloats.add(token.getValue());
            }
        }
        // add list nFloats into node
        CommandNode.PrefCommandNode node = new CommandNode.PrefCommandNode();
        node.setKeyword(keyword);
        node.setNFloats(nFloats);
        nodes.add(node);
        printAST(node);
        currentTokenIndex = currentTokenIndex + (numGroups - 1);
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // PARSER VACANT COMMAND
    private void parseVacant(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("vacant")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("WHERE VACANT IS")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: WHERE VACANT IS");
        }
        // Get the identifier token
        currentTokenIndex++;
        Token identifierToken = tokens.get(currentTokenIndex);
        String keyword = "VACANT";
        if (!identifierToken.getType().equals(SMLAParser.TokenType.n_float.toString())) {
            throw new Exception("Expected an variable or integer identifier");
        }
        String identifier = identifierToken.getValue();
        // Create the simulation command subtree
        CommandNode.VacantCommandNode node = new CommandNode.VacantCommandNode();
        node.setKeyword(keyword);
        node.setIdentifier(identifier);
        nodes.add(node);
        printAST(node);
        // currentTokenIndex++;
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // PARSER USING COMMAND
    private void parseUsing(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("using")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("USING")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: USING");
        }
        // Get the identifier token
        Token identifierToken = tokens.get(currentTokenIndex + 1);
        String keyword = "MOVE";
        if (!identifierToken.getType().equals(SMLAParser.TokenType.typeMoving.toString())) {
            throw new Exception("Expected an alphanumeric identifier");
        }
        String identifier = identifierToken.getValue();
        // Create the simulation command subtree
        CommandNode.UsingCommandNode node = new CommandNode.UsingCommandNode();
        node.setKeyword(keyword);
        node.setIdentifier(identifier);
        nodes.add(node);
        printAST(node);
        currentTokenIndex = currentTokenIndex + 2;
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // PARSER RUN COMMAND
    private void parseRun(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("run")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("RUN SIMULATION")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: RUN SIMULATION");
        }
        // Get the identifier token
        currentTokenIndex++;
        Token identifierToken = tokens.get(currentTokenIndex);
        String keyword = "RUN";
        if (!identifierToken.getType().equals(SMLAParser.TokenType.alphanumeric.toString())) {
            throw new Exception("Expected an alphanumeric identifier");
        }
        String identifier = identifierToken.getValue(); // get name of simulation
        if (tokens.get(currentTokenIndex + 1).getValue().equals("FOR")) {
            // Get the n_integer token
            currentTokenIndex = currentTokenIndex + 2;
            Token nIntegerToken = tokens.get(currentTokenIndex);
            if (!nIntegerToken.getType().equals(SMLAParser.TokenType.n_integer.toString())) {
                throw new Exception("Expected an n_integer value");
            }
            int nInteger = Integer.parseInt(nIntegerToken.getValue()); // get number of ticks
            // Create the simulation command subtree
            CommandNode.RunCommandNode node = new CommandNode.RunCommandNode();
            node.setKeyword(keyword);
            node.setIdentifier(identifier);
            node.setNInteger(nInteger);
            nodes.add(node);
            printAST(node);
            currentTokenIndex++;
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        } else {
            CommandNode.RunCommandNode node = new CommandNode.RunCommandNode();
            node.setKeyword(keyword);
            node.setIdentifier(identifier);
            nodes.add(node);
            printAST(node);
            //  currentTokenIndex++;
            currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
        }
    }

    // PARSER REPORT COMMAND
    private void parseReport(List<Token> tokens) throws Exception {
        if (!tokens.get(currentTokenIndex).getType().equals("report")
        ) {
            throw new Exception("Invalid token sequence");
        }
        if (!tokens.get(currentTokenIndex).getValue().equals("REPORT SIMULATION")) {
            throw new IllegalArgumentException("\nInvalid statement type at line: "
                    + currentToken.getLineNumber() + ", unexpected: " + currentToken.getValue()
                    + ", expected: REPORT SIMULATION");
        }
        List<String> identifiers = new ArrayList<>();
        String keyword = "REPORT";
        currentTokenIndex++; // move past the "report" keyword
        currentToken = tokens.get(currentTokenIndex);
        // check name of report in the list
        while (currentTokenIndex < tokens.size()) {
            if (currentToken.getType().equals("alphanumeric")) {
                identifiers.add(currentToken.getValue());
                currentTokenIndex++;
                if (currentTokenIndex < tokens.size()) {
                    currentToken = tokens.get(currentTokenIndex);
                }
            } else {
                break;
            }
        }
        // add and print node
        CommandNode.ReportCommandNode node = new CommandNode.ReportCommandNode();
        node.setKeyword(keyword);
        node.setIdentifiers(identifiers);
        nodes.add(node);
        printAST(node);
        currentTokenIndex--;
        currentTokenIndex = advanceToNextToken(tokens, currentTokenIndex);
    }

    // move to next token
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

    // print node
    public static <ASTNode extends CommandNode> void printAST(ASTNode node) {
        if (node instanceof CommandNode.VariableCommandNode variableNode) {
            System.out.println("- Variable Command: " + variableNode.getIdentifiers());
        } else if (node instanceof CommandNode.SimulationCommandNode) {
            CommandNode.SimulationCommandNode simNode = (CommandNode.SimulationCommandNode) node;
            System.out.println("- Setup Simulation Command: " + simNode.getKeyword() + ", " + simNode.getIdentifier() + ", "
                    + simNode.getNInteger() + ", " + simNode.getNFloats());
        } else if (node instanceof CommandNode.PrefCommandNode) {
            CommandNode.PrefCommandNode prefNode = (CommandNode.PrefCommandNode) node;
            System.out.println("- Pref Command: " + prefNode.getKeyword() + ", " + prefNode.getnFloats());

        } else if (node instanceof CommandNode.VacantCommandNode) {
            CommandNode.VacantCommandNode vacantNode = (CommandNode.VacantCommandNode) node;
            System.out.println("- Vacant Command: " + vacantNode.getKeyword() + ", " + vacantNode.getIdentifier());

        } else if (node instanceof CommandNode.UsingCommandNode) {
            CommandNode.UsingCommandNode usingNode = (CommandNode.UsingCommandNode) node;
            System.out.println("- Moving type Command: " + usingNode.getKeyword() + ", " + usingNode.getIdentifier());
        } else if (node instanceof CommandNode.RunCommandNode) {
            CommandNode.RunCommandNode runNode = (CommandNode.RunCommandNode) node;
            System.out.println("- Run Simulation Command: " + runNode.getKeyword() + ", " + runNode.getIdentifier() + ", " + runNode.getNInteger());
        } else if (node instanceof CommandNode.ReportCommandNode) {
            CommandNode.ReportCommandNode reportNode = (CommandNode.ReportCommandNode) node;
            System.out.println("- Report Command: " + reportNode.getKeyword() + ", " + reportNode.getIdentifiers());
        }

        List<ASTNode> children = (List<ASTNode>) node.getChildren();
        for (ASTNode child : children) {
            printAST(child);
        }
    }

    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        // Output from Parser
        // Variable pref1 = 10, vacant = 50
        tokens.add(new Token("variable", "PREF1", "10", "", 1));
        tokens.add(new Token("variable", "VACANT", "50", "", 2));

        // Run simulation Example1 FOR 50 TICKS
        tokens.add(new Token("run", "RUN SIMULATION", "", "", 3));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 3));
        tokens.add(new Token("phrase", "FOR", "", "", 3));
        tokens.add(new Token("n_integer", "50", "", "", 3));
        tokens.add(new Token("phrase", "TICKS", "", "", 3));

        // Report simulation Example1
        tokens.add(new Token("report", "REPORT SIMULATION", "", "", 4));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 4));

        // Using random move
        tokens.add(new Token("using", "USING", "", "", 5));
        tokens.add(new Token("typeMoving", "RANDOM", "", "", 5));
        tokens.add(new Token("phrase", "MOVE", "", "", 5));

        // Setup simulation (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
        tokens.add(new Token("simulation", "SETUP SIMULATION", "", "", 6));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 6));
        tokens.add(new Token("phrase", "WITH", "", "", 6));
        tokens.add(new Token("n_integer", "4", "", "", 6));
        tokens.add(new Token("phrase", "AS", "", "", 6));
        tokens.add(new Token("n_float", "30", "", "", 6));
        tokens.add(new Token("n_float", "20", "", "", 6));
        tokens.add(new Token("n_float", "30", "", "", 6));
        tokens.add(new Token("n_float", "20", "", "", 6));

        // Where pref is (10, 20, 30, 40)
        tokens.add(new Token("pref", "WHERE PREF IS", "", "", 7));
        tokens.add(new Token("n_float", "10", "", "", 7));
        tokens.add(new Token("n_float", "20", "", "", 7));
        tokens.add(new Token("n_float", "30", "", "", 7));
        tokens.add(new Token("n_float", "40", "", "", 7));

        // Where vacant is Vacant
        tokens.add(new Token("vacant", "WHERE VACANT IS", "", "", 8));
        tokens.add(new Token("n_float", "50", "", "", 8));

        CommandNode node = null;
        System.out.println("\n - AST output: ");
        try {
            new SMLA_AST(tokens).parseProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*** End of node list - Create AST successfully");
    }
}
