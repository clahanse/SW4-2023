
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;


public class Main {
    public static void main(String[] args) {
        try {
            File inputFile = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\input.txt");
            if (!inputFile.exists()) {
                throw new Exception("Input file does not exist.");
            }
            if (inputFile.length() == 0) {
                throw new Exception("File is empty");
            }
            Scanner scanner = new Scanner(inputFile);
            SMLAScanner smlaScanner = new SMLAScanner(scanner);
            List<CommandNode> nodesList = new ArrayList<>();
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
            smlaScanner.tokenizeInput(input.toString());
            System.out.println("\n A- Token list from Scanner: ");
            int i = 1;
            for (Token token : SMLAScanner.getTokenList()) {
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
            System.out.println("*** End of the list - Scanner successful");

            // parse tokens using SMLAParser
            System.out.println("\n B- Parser result: ");
            SMLAParser parser = new SMLAParser(SMLAScanner.getTokenList());
            parser.parseProgram();

            System.out.println("\n*** Token list from Parser ");
            tokensParser = SMLAParser.getTokenList();
            int j = 1;
            for (Token token : tokensParser) {
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

            // parse tokens using SMLA_AST
            System.out.println("\n C- AST output: ");
            SMLA_AST ast = new SMLA_AST(SMLAParser.getTokenList());
            ast.parseCommands();
            System.out.println("*** End of node list - Create AST successfully");

            // save the node and token lists to a file
            File file = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\output.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("\nToken List:\n");
                for (Token token : SMLAScanner.getTokenList()) {
                    writer.println(token.toString());
                }
                //  writer.println(rootNode.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // run simulation in NetLogo
            System.out.println("\n D- SIMULATION: ");
            String modelType;
            String movingType = null;
            int numGroups = 0;
            int perc_vacant_space = 0;
            int perc_group1 = 0;
            int perc_group2 = 0;
            int perc_group3 = 0;
            int perc_similarity_wanted_group1 = 0;
            int perc_similarity_wanted_group2 = 0;
            int perc_similarity_wanted_group3 = 0;
            int NoTicks = 0;
            nodesList = ast.getNodes();
            // Iterate over all nodes in the list
            for (CommandNode node : nodesList) {
                // Check if the node is a SimulationCommandNode
                if (node instanceof CommandNode.SimulationCommandNode) {
                    CommandNode.SimulationCommandNode simNode = (CommandNode.SimulationCommandNode) node;
                    numGroups = simNode.getNInteger();
                    List<String> nIntegers = simNode.getNIntegers();
                    perc_group1 = Integer.parseInt(nIntegers.get(0));
                    perc_group2 = Integer.parseInt(nIntegers.get(1));
                    if(numGroups==3){
                        perc_group3 = Integer.parseInt(nIntegers.get(2));
                    }
                } else if (node instanceof CommandNode.PrefCommandNode) {
                    CommandNode.PrefCommandNode PrefNode = (CommandNode.PrefCommandNode) node;
                    List<String> nIntegers = PrefNode.getNIntegers();
                    perc_similarity_wanted_group1 = Integer.parseInt(nIntegers.get(0));
                    perc_similarity_wanted_group2 = Integer.parseInt(nIntegers.get(1));
                    if(numGroups==3){
                        perc_similarity_wanted_group3 = Integer.parseInt(nIntegers.get(2));
                    }
                } else if (node instanceof CommandNode.VacantCommandNode) {
                    CommandNode.VacantCommandNode VacantNode = (CommandNode.VacantCommandNode) node;
                    perc_vacant_space = Integer.parseInt(VacantNode.getIdentifier());
                } else if (node instanceof CommandNode.UsingCommandNode) {
                    CommandNode.UsingCommandNode UsingNode = (CommandNode.UsingCommandNode) node;
                    movingType = UsingNode.getIdentifier();
                } else if (node instanceof CommandNode.RunCommandNode) {
                    CommandNode.RunCommandNode RunNode = (CommandNode.RunCommandNode) node;
                    NoTicks = RunNode.getNInteger();
                }
            }

            // choose model
            if (numGroups == 2 && movingType.equals("RANDOM")) {
                modelType = "Schelling's model-A-Random";
                Model.runModelA(modelType, perc_vacant_space, perc_group1, perc_group2,
                        perc_similarity_wanted_group1, perc_similarity_wanted_group2, NoTicks);
            }
            if (numGroups == 2 && movingType.equals("SCHELLING")) {
                modelType = "Schelling's model-A-Schelling";
                Model.runModelA(modelType, perc_vacant_space, perc_group1, perc_group2,
                        perc_similarity_wanted_group1, perc_similarity_wanted_group2, NoTicks);
            }
            if (numGroups == 3 && movingType.equals("RANDOM")) {
                modelType = "Schelling's model-B-Random";
                Model.runModelB(modelType, perc_vacant_space, perc_group1,perc_group2, perc_group3,
                        perc_similarity_wanted_group1, perc_similarity_wanted_group2,
                        perc_similarity_wanted_group3, NoTicks);
            }if (numGroups == 3 && movingType.equals("SCHELLING")) {
                modelType = "Schelling's model-B-Schelling";
                Model.runModelB(modelType, perc_vacant_space, perc_group1,perc_group2, perc_group3,
                        perc_similarity_wanted_group1, perc_similarity_wanted_group2,
                        perc_similarity_wanted_group3, NoTicks);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
