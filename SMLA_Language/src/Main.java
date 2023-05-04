
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
            //   SMLAParser parser = new SMLAParser(tokens);
            SMLAParser parser = new SMLAParser(SMLAScanner.getTokenList());
            parser.parseProgram();

            System.out.println("\n*** Token list from Parser ");
            tokensParser = SMLAParser.getTokenList();
            int j=1;
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
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
