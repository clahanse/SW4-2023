import java.io.File;
import java.util.List;
import java.util.Scanner;

import static com.sun.tools.javac.util.StringUtils.toUpperCase;

public class SMLAScanner_ClassTest {
    // CHECK SCANNER CLASS
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

            System.out.println("\n *** Token list from Scanner: "); // print token list to the screen
            smlaScanner.printTokens(tokensScannerList);
            System.out.println("*** End of the list - Scanner successful");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
