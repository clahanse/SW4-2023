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
            Scanner scanner = new Scanner(System.in);
            SMLAScanner smlaScanner = new SMLAScanner(scanner);
            List<SMLAParser.Token> tokenList = new ArrayList<>();
            boolean continueScanning = true;
            StringBuilder input = new StringBuilder();
            System.out.println("\nEnter your commands (type 'run' to execute):");
            while (continueScanning) {
                SMLAScanner.getTokenList().clear();
                String line = scanner.nextLine();
                if (line.equals("run")) {
                    continueScanning = false;
                } else {
                    String UpperLine = toUpperCase(line);
                    input.append(UpperLine).append("\n");
                }
            }
            // tokenize input using scanner
            smlaScanner.tokenizeInput(input.toString());
            System.out.println("\nPrint Token from List of Scanner: ");
            String type ="";
            String value="";
            String command ="";
            int line =0;
            int i = 1;
            for (SMLAScanner.Token token : SMLAScanner.getTokenList()) {
                type = token.getTyp();
                value = token.getVal();
                command = token.getCommands().toString();
                line = token.getLineNumber();
                tokenList.add(new SMLAParser.Token(type,value,command,line));
                System.out.println(i + "-type: " + token.getTyp() + ", value: " + token.getVal());
                i++;
            }

            // pass TokenList to parser and parse program
            SMLAParser parser = new SMLAParser(tokenList);
            parser.parseProgram();
            System.out.println("\nParser success");

            // save the node and token lists to a file
            File file = new File("C:\\Users\\HAI\\OneDrive\\Desktop\\output.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Node List:");
                for (SMLAParser.Node node : SMLAParser.nodeList) {
                    writer.println(node.toString());
                }
                writer.println("\nToken List:");
                for (SMLAParser.Token token : SMLAParser.tokenList) {
                    writer.println(token.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}

