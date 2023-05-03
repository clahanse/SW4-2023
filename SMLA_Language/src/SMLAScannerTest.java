import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Scanner;

import org.junit.Test;

public class SMLAScannerTest {
    // Test 'Report simulation example1 example2'
    @Test
    public void testReport() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input7.txt");
        Token[] expectedTokens = {
                new Token("report", "REPORT SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("alphanumeric", "EXAMPLE2", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Where vacant is vacant'
    @Test
    public void testVacant() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input2.txt");
        Token[] expectedTokens = {
                new Token("vacant", "WHERE VACANT IS", 1),
                new Token("alphanumeric", "VACANT", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Variable'
    @Test
    public void testVariable() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input1.txt");
        Token[] expectedTokens = {
                new Token("variable", "PREF1", "10", "", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getVariableValue(), actualTokens[i].getVariableValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Run simulation Example1 for 50 ticks'
    @Test
    public void testRun() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input3.txt");
        Token[] expectedTokens = {
                new Token("run", "RUN SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "FOR", 1),
                new Token("n_integer", "50", 1),
                new Token("phrase", "TICKS", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Using random move'
    @Test
    public void testUsing() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input4.txt");
        Token[] expectedTokens = {
                new Token("using", "USING", 1),
                new Token("typeMoving", "RANDOM", 1),
                new Token("phrase", "MOVE", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Setup simulation(Example1) with 4 as (Group1, Group2, Group3, ABC)'
    @Test
    public void testSetup() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input5.txt");
        Token[] expectedTokens = {
                new Token("simulation", "SETUP SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "WITH", 1),
                new Token("n_integer", "4", 1),
                new Token("phrase", "AS", 1),
                new Token("alphanumeric", "GROUP1", 1),
                new Token("alphanumeric", "GROUP2", 1),
                new Token("alphanumeric", "GROUP3", 1),
                new Token("alphanumeric", "ABC", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Where pref is (10, 20, 30, 40)'
    @Test
    public void testPref() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input6.txt");
        Token[] expectedTokens = {
                new Token("pref", "WHERE PREF IS", 1),
                new Token("n_integer", "10", 1),
                new Token("n_integer", "20", 1),
                new Token("n_integer", "30", 1),
                new Token("n_integer", "40", 1),
                new Token("end_command", "end of command", 1)
        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test all commands
    @Test
    public void testAllCommands() throws Exception {
        SMLAScanner smlaScanner = tokenizeInputFromFile("C:\\Users\\HAI\\OneDrive\\Desktop\\TestScan\\input8.txt");
        Token[] expectedTokens = {
                // pref1 = 10
                new Token("variable", "PREF1", "10", "", 1),
                new Token("end_command", "end of command", 1),
                // Vacant = 50
                new Token("variable", "VACANT", "50", "", 2),
                new Token("end_command", "end of command", 2),
                // SETUP SIMULATION (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
                new Token("simulation", "SETUP SIMULATION", 3),
                new Token("alphanumeric", "EXAMPLE1", 3),
                new Token("phrase", "WITH", 3),
                new Token("n_integer", "4", 3),
                new Token("phrase", "AS", 3),
                new Token("alphanumeric", "GROUP1", 3),
                new Token("alphanumeric", "GROUP2", 3),
                new Token("alphanumeric", "GROUP3", 3),
                new Token("alphanumeric", "ABC", 3),
                new Token("end_command", "end of command", 3),
                // WHERE PREF IS (10, 20, 30, 40)
                new Token("pref", "WHERE PREF IS", 4),
                new Token("n_integer", "10", 4),
                new Token("n_integer", "20", 4),
                new Token("n_integer", "30", 4),
                new Token("n_integer", "40", 4),
                new Token("end_command", "end of command", 4),
                // WHERE VACANT IS Vacant
                new Token("vacant", "WHERE VACANT IS", 5),
                new Token("alphanumeric", "VACANT", 5),
                new Token("end_command", "end of command", 5),
                // USING RANDOM MOVE
                new Token("using", "USING", 6),
                new Token("typeMoving", "RANDOM", 6),
                new Token("phrase", "MOVE", 6),
                new Token("end_command", "end of command", 6),
                // RUN SIMULATION Example1 FOR 50 TICKS
                new Token("run", "RUN SIMULATION", 7),
                new Token("alphanumeric", "EXAMPLE1", 7),
                new Token("phrase", "FOR", 7),
                new Token("n_integer", "50", 7),
                new Token("phrase", "TICKS", 7),
                new Token("end_command", "end of command", 7),
                // REPORT SIMULATION example1 example2
                new Token("report", "REPORT SIMULATION", 8),
                new Token("alphanumeric", "EXAMPLE1", 8),
                new Token("alphanumeric", "EXAMPLE2", 8),
                new Token("end_command", "end of command", 8)

        };
        Token[] actualTokens = smlaScanner.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Read file .txt
    public SMLAScanner tokenizeInputFromFile(String filePath) throws Exception {
        File inputFile = new File(filePath);
        if (!inputFile.exists()) {
            throw new Exception("Input file does not exist.");
        }
        if (inputFile.length() == 0) {
            throw new Exception("File is empty");
        }
        Scanner scanner = new Scanner(inputFile);
        SMLAScanner smlaScanner = new SMLAScanner(scanner);
        StringBuilder input = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                continue; // Skip blank lines
            }
            String upperLine = line.toUpperCase();
            input.append(upperLine).append("\n");
        }
        smlaScanner.tokenizeInput(input.toString());
        return smlaScanner;
    }
}


