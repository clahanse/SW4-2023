import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SMLAParser_Test {
    // Test 'Report simulation example1'
    @Test
    public void testSetup() throws Exception {
        List<Token> tokens = new ArrayList<>();
        // Output from Scanner
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "35", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "25", 1));
        tokens.add(new Token("end_command", "end of command", 1));

        Token[] expectedTokens = {
                new Token("simulation", "SETUP SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "WITH", 1),
                new Token("n_integer", "4", 1),
                new Token("phrase", "AS", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "35", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "25", 1),

        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Where pref is (60, 50, 40, 70)'
    @Test
    public void testPref() throws Exception {
        List<Token> tokens = new ArrayList<>();
        // Output from Scanner
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "35", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "25", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        // Where pref is (60, 50, 40, 70)
        tokens.add(new Token("pref", "WHERE PREF IS", 1));
        tokens.add(new Token("n_integer", "60", 1));
        tokens.add(new Token("n_integer", "50", 1));
        tokens.add(new Token("n_integer", "40", 1));
        tokens.add(new Token("n_integer", "70", 1));
        tokens.add(new Token("end_command", "end of command", 1));

        Token[] expectedTokens = {
                new Token("simulation", "SETUP SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "WITH", 1),
                new Token("n_integer", "4", 1),
                new Token("phrase", "AS", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "35", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "25", 1),

                new Token("pref", "WHERE PREF IS", 1),
                new Token("n_float", "60", 1),
                new Token("n_float", "50", 1),
                new Token("n_float", "40", 1),
                new Token("n_float", "70", 1),
        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
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
        List<Token> tokens = new ArrayList<>();
        //'Where vacant is 65)'
        tokens.add(new Token("vacant", "WHERE VACANT IS", 1));
        tokens.add(new Token("n_integer", "65", 1));
        tokens.add(new Token("end_command", "end of command", 1));

        Token[] expectedTokens = {
                new Token("vacant", "WHERE VACANT IS", 1),
                new Token("n_float", "65", 1),
        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
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
        List<Token> tokens = new ArrayList<>();
        // 'Pref1 = 10'
        tokens.add(new Token("variable", "PREF1", "", "", 1));
        tokens.add(new Token("n_integer", "10", "", "", 1));
        tokens.add(new Token("end_command", "end of command", 1));

        Token[] expectedTokens = {
                new Token("variable", "PREF1", "10", "", 1),
        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
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
        List<Token> tokens = new ArrayList<>();
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "35", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "25", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        // Run simulation example1 for 15 ticks
        tokens.add(new Token("run", "RUN SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "FOR", 1));
        tokens.add(new Token("n_integer", "15", 1));
        tokens.add(new Token("phrase", "TICKS", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        Token[] expectedTokens = {
                new Token("simulation", "SETUP SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "WITH", 1),
                new Token("n_integer", "4", 1),
                new Token("phrase", "AS", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "35", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "25", 1),

                new Token("run", "RUN SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "FOR", 1),
                new Token("n_integer", "15", 1),
                new Token("phrase", "TICKS", 1),
        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
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
        List<Token> tokens = new ArrayList<>();
        // Using schelling move
        tokens.add(new Token("using", "USING", 1));
        tokens.add(new Token("typeMoving", "SCHELLING", 1));
        tokens.add(new Token("phrase", "MOVE", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        Token[] expectedTokens = {
                new Token("using", "USING", 1),
                new Token("typeMoving", "SCHELLING", 1),
                new Token("phrase", "MOVE", 1),
       };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }

    // Test 'Setup simulation(Example1) with 4 as (Group1, Group2, Group3, ABC)'
    @Test
    public void testReport() throws Exception {
        List<Token> tokens = new ArrayList<>();
       //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "35", 1));
        tokens.add(new Token("n_integer", "20", 1));
        tokens.add(new Token("n_integer", "25", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        // Report simulation example1
        tokens.add(new Token("report", "REPORT SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        Token[] expectedTokens = {
                new Token("simulation", "SETUP SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
                new Token("phrase", "WITH", 1),
                new Token("n_integer", "4", 1),
                new Token("phrase", "AS", 1),
                new Token("n_float", "20",  1),
                new Token("n_float", "35", 1),
                new Token("n_float", "20", 1),
                new Token("n_float", "25", 1),

                new Token("report", "REPORT SIMULATION", 1),
                new Token("alphanumeric", "EXAMPLE1", 1),
            };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
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
        List<Token> tokens = new ArrayList<>();
        // 'Pref1 = 10'
        tokens.add(new Token("variable", "PREF1", "", "", 1));
        tokens.add(new Token("n_integer", "10", "", "", 1));
        tokens.add(new Token("end_command", "end of command", 1));
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 2));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 2));
        tokens.add(new Token("phrase", "WITH", 2));
        tokens.add(new Token("n_integer", "4", 2));
        tokens.add(new Token("phrase", "AS", 2));
        tokens.add(new Token("n_integer", "20", 2));
        tokens.add(new Token("n_integer", "35", 2));
        tokens.add(new Token("n_integer", "20", 2));
        tokens.add(new Token("n_integer", "25", 2));
        tokens.add(new Token("end_command", "end of command", 2));
        // Where pref is (60, 50, 40, 70)
        tokens.add(new Token("pref", "WHERE PREF IS", 3));
        tokens.add(new Token("n_integer", "60", 3));
        tokens.add(new Token("n_integer", "50", 3));
        tokens.add(new Token("n_integer", "40", 3));
        tokens.add(new Token("n_integer", "70", 3));
        tokens.add(new Token("end_command", "end of command", 3));
        //'Where vacant is 65)'
        tokens.add(new Token("vacant", "WHERE VACANT IS", 4));
        tokens.add(new Token("n_integer", "65", 4));
        tokens.add(new Token("end_command", "end of command", 4));
        // Using schelling move
        tokens.add(new Token("using", "USING", 5));
        tokens.add(new Token("typeMoving", "SCHELLING", 5));
        tokens.add(new Token("phrase", "MOVE", 5));
        tokens.add(new Token("end_command", "end of command", 5));
        // Run simulation example1 for 15 ticks
        tokens.add(new Token("run", "RUN SIMULATION", 6));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 6));
        tokens.add(new Token("phrase", "FOR", 6));
        tokens.add(new Token("n_integer", "15", 6));
        tokens.add(new Token("phrase", "TICKS", 6));
        tokens.add(new Token("end_command", "end of command", 6));
        // Report simulation example1
        tokens.add(new Token("report", "REPORT SIMULATION", 7));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 7));
        tokens.add(new Token("end_command", "end of command", 7));

        Token[] expectedTokens = {
                new Token("variable", "PREF1", "10", "", 1),
                // SETUP SIMULATION (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
                new Token("simulation", "SETUP SIMULATION", 2),
                new Token("alphanumeric", "EXAMPLE1", 2),
                new Token("phrase", "WITH", 2),
                new Token("n_integer", "4", 2),
                new Token("phrase", "AS", 2),
                new Token("n_float", "20", 2),
                new Token("n_float", "35", 2),
                new Token("n_float", "20", 2),
                new Token("n_float", "25", 2),
                // WHERE PREF IS (10, 20, 30, 40)
                new Token("pref", "WHERE PREF IS", 3),
                new Token("n_float", "60", 3),
                new Token("n_float", "50", 3),
                new Token("n_float", "40", 3),
                new Token("n_float", "70", 3),
                // WHERE VACANT IS Vacant
                new Token("vacant", "WHERE VACANT IS", 4),
                new Token("n_float", "65", 4),
                // USING SCHELLING MOVE
                new Token("using", "USING", 5),
                new Token("typeMoving", "SCHELLING", 5),
                new Token("phrase", "MOVE", 5),
                // RUN SIMULATION Example1 FOR 50 TICKS
                new Token("run", "RUN SIMULATION", 6),
                new Token("alphanumeric", "EXAMPLE1", 6),
                new Token("phrase", "FOR", 6),
                new Token("n_integer", "15", 6),
                new Token("phrase", "TICKS", 6),
                // REPORT SIMULATION example1 example2
                new Token("report", "REPORT SIMULATION", 7),
                new Token("alphanumeric", "EXAMPLE1", 7),
        };
        SMLAParser parser = new SMLAParser(tokens);
        parser.parseProgram();

        Token[] actualTokens = SMLAParser.getTokenList().toArray(new Token[0]);
        assertEquals(expectedTokens.length, actualTokens.length);
        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals(expectedTokens[i].getType(), actualTokens[i].getType());
            assertEquals(expectedTokens[i].getValue(), actualTokens[i].getValue());
            assertEquals(expectedTokens[i].getLineNumber(), actualTokens[i].getLineNumber());
        }
    }
}
