
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class Main {
    public static void main(String[] args) {
        try {
            String saveLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\"; // path to model and input file
            File inputFile = new File(saveLibrary + "input.txt");
            if (!inputFile.exists()) {
                throw new Exception("Input file does not exist.");
            }
            if (inputFile.length() == 0) {
                throw new Exception("File is empty");
            }

            // Scan input file
            Scanner scanner = new Scanner(inputFile);
            StringBuilder input = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue; // Skip blank lines
                }
                String UpperLine = toUpperCase(line);
                input.append(UpperLine).append("\n");
            }

            // Tokenize input using scanner
            SMLAScanner smlaScanner = new SMLAScanner();
            smlaScanner.tokenizeInput(input.toString());
            List<Token> tokensScannerList;
            tokensScannerList = SMLAScanner.getTokenList();

            System.out.println("\n A- Token list from Scanner: "); // print token list to the screen
            smlaScanner.printTokens(tokensScannerList);
            System.out.println("*** End of the list - Scanner successful");

            // Parse tokens using SMLAParser
            System.out.println("\n B- Parser result: ");
            SMLAParser parser = new SMLAParser(SMLAScanner.getTokenList());
            parser.parseProgram();
            List<Token> tokensParserList;
            tokensParserList = SMLAParser.getTokenList();

            System.out.println("\n*** Token list from Parser "); // print token list to the screen
            smlaScanner.printTokens(tokensParserList);
            System.out.println("*** End of the list - Parser successful");

            // Parse tokens using SMLA_AST
            System.out.println("\n C- AST output: ");
            SMLA_AST ast = new SMLA_AST(SMLAParser.getTokenList());
            ast.parseCommands();
            System.out.println("*** End of node list - Create AST successfully");

            // Run simulation in NetLogo
            System.out.println("\n D- SIMULATION: ");
            SMLACodeGenerator codeGenerator = new SMLACodeGenerator();
            List<CommandNode> nodesList;
            List<String> reportList = new ArrayList<>();
            List<List<Token>> simulations = new ArrayList<>();
            nodesList = ast.getNodes();

            ast.processNodesList(nodesList, simulations); // iterate over all nodes and get parameters
            ast.getReportList(nodesList, reportList); // get name of simulation in report
            Model model = new Model();// Choose and run relevant simulations
            model.processRelevantSimulations(reportList, simulations, codeGenerator, saveLibrary);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
