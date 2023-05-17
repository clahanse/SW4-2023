import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SMLA_AST_Test {

    // Test 'Using schelling move'
    @Test
    public void testUsing() throws Exception {
        List<Token> tokens = new ArrayList<>();
        // 'Using Schelling move'
        tokens.add(new Token("using", "USING", 1));
        tokens.add(new Token("typeMoving", "SCHELLING", 1));
        tokens.add(new Token("phrase", "MOVE", 1));

        String[] expectedArray = {"MOVE", "SCHELLING"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[2];
        CommandNode node = nodeList.get(0);
        if (node instanceof CommandNode.UsingCommandNode) { // node is using command
            CommandNode.UsingCommandNode UsingNode = (CommandNode.UsingCommandNode) node;
            actualArray[0] = UsingNode.getKeyword();
            actualArray[1] = UsingNode.getIdentifier();
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
    }

    // Test 'Report simulation example1'
    @Test
    public void testSetup() throws Exception {
        List<Token> tokens = new ArrayList<>();
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "35", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "25", 1));

        String[] expectedArray = {"SETUP", "EXAMPLE1", "4", "20", "35", "20", "25"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[7];
        CommandNode node = nodeList.get(0);
        if (node instanceof CommandNode.SimulationCommandNode) { // node is setup command
            CommandNode.SimulationCommandNode SimuNode = (CommandNode.SimulationCommandNode) node;
            actualArray[0] = SimuNode.getKeyword();
            actualArray[1] = SimuNode.getIdentifier();
            actualArray[2] = String.valueOf(SimuNode.getNInteger());
            actualArray[3] = SimuNode.getNFloats().get(0);
            actualArray[4] = SimuNode.getNFloats().get(1);
            actualArray[5] = SimuNode.getNFloats().get(2);
            actualArray[6] = SimuNode.getNFloats().get(3);
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
    }

    // Test 'Where pref is (60, 50, 40, 70)'
    @Test
    public void testPref() throws Exception {
        List<Token> tokens = new ArrayList<>();
        //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "35", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "25", 1));
        // Where pref is (60, 50, 40, 70)
        tokens.add(new Token("pref", "WHERE PREF IS", 1));
        tokens.add(new Token("n_float", "60", 1));
        tokens.add(new Token("n_float", "50", 1));
        tokens.add(new Token("n_float", "40", 1));
        tokens.add(new Token("n_float", "70", 1));

        String[] expectedArray = {"PREF", "60", "50", "40", "70"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[5];
        CommandNode node = nodeList.get(1);
        if (node instanceof CommandNode.PrefCommandNode) { // node is pref command
            CommandNode.PrefCommandNode PrefNode = (CommandNode.PrefCommandNode) node;
            actualArray[0] = PrefNode.getKeyword();
            actualArray[1] = PrefNode.getnFloats().get(0);
            actualArray[2] = PrefNode.getnFloats().get(1);
            actualArray[3] = PrefNode.getnFloats().get(2);
            actualArray[4] = PrefNode.getnFloats().get(3);
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
    }

    // Test 'Where vacant is 65'
    @Test
    public void testVacant() throws Exception {
        List<Token> tokens = new ArrayList<>();
        //'Where vacant is 65)'
        tokens.add(new Token("vacant", "WHERE VACANT IS", 1));
        tokens.add(new Token("n_float", "65", 1));

        String[] expectedArray = {"VACANT", "65"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[2];
        CommandNode node = nodeList.get(0);
        if (node instanceof CommandNode.VacantCommandNode) { // node is vacant command
            CommandNode.VacantCommandNode VacantNode = (CommandNode.VacantCommandNode) node;
            actualArray[0] = VacantNode.getKeyword();
            actualArray[1] = VacantNode.getIdentifier();
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
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
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "35", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "25", 1));
        // Run simulation example1 for 15 ticks
        tokens.add(new Token("run", "RUN SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "FOR", 1));
        tokens.add(new Token("n_integer", "15", 1));
        tokens.add(new Token("phrase", "TICKS", 1));

        String[] expectedArray = {"RUN", "EXAMPLE1", "15"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[3];
        CommandNode node = nodeList.get(1);
        if (node instanceof CommandNode.RunCommandNode) { // node is vacant command
            CommandNode.RunCommandNode RunNode = (CommandNode.RunCommandNode) node;
            actualArray[0] = RunNode.getKeyword();
            actualArray[1] = RunNode.getIdentifier();
            actualArray[2] = String.valueOf(RunNode.getNInteger());
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
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
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "35", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "25", 1));
        // Report simulation example1
        tokens.add(new Token("report", "REPORT SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));

        String[] expectedArray = {"REPORT", "EXAMPLE1"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[2];
        CommandNode node = nodeList.get(1);
        if (node instanceof CommandNode.ReportCommandNode) { // node is vacant command
            CommandNode.ReportCommandNode ReportNode = (CommandNode.ReportCommandNode) node;
            actualArray[0] = ReportNode.getKeyword();
            actualArray[1] = ReportNode.getIdentifiers().get(0);
        }
        assertEquals(expectedArray.length, actualArray.length);
        assertArrayEquals(expectedArray, actualArray);
    }

    // Test all commands
    @Test
    public void testAllCommands() throws Exception {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("simulation", "SETUP SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "WITH", 1));
        tokens.add(new Token("n_integer", "4", 1));
        tokens.add(new Token("phrase", "AS", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "35", 1));
        tokens.add(new Token("n_float", "20", 1));
        tokens.add(new Token("n_float", "25", 1));
        // Where pref is (60, 50, 40, 70)
        tokens.add(new Token("pref", "WHERE PREF IS", 1));
        tokens.add(new Token("n_float", "60", 1));
        tokens.add(new Token("n_float", "50", 1));
        tokens.add(new Token("n_float", "40", 1));
        tokens.add(new Token("n_float", "70", 1));
        //'Where vacant is 65)'
        tokens.add(new Token("vacant", "WHERE VACANT IS", 1));
        tokens.add(new Token("n_float", "65", 1));
        // Using schelling move
        tokens.add(new Token("using", "USING", 1));
        tokens.add(new Token("typeMoving", "SCHELLING", 1));
        tokens.add(new Token("phrase", "MOVE", 1));
        // Run simulation example1 for 15 ticks
        tokens.add(new Token("run", "RUN SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));
        tokens.add(new Token("phrase", "FOR", 1));
        tokens.add(new Token("n_integer", "15", 1));
        tokens.add(new Token("phrase", "TICKS", 1));
        // Report simulation example1
        tokens.add(new Token("report", "REPORT SIMULATION", 1));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", 1));

        String[] expectedArray = {"SETUP", "EXAMPLE1", "4", "20", "35", "20", "25"};
        String[] expectedArray1 = {"PREF", "60", "50", "40", "70"};
        String[] expectedArray2 = {"VACANT", "65"};
        String[] expectedArray3 = {"MOVE", "SCHELLING"};
        String[] expectedArray4 = {"RUN", "EXAMPLE1", "15"};
        String[] expectedArray5 = {"REPORT", "EXAMPLE1"};

        SMLA_AST ast = new SMLA_AST(tokens);
        ast.parseCommands();
        List<CommandNode> nodeList = new ArrayList<>();
        nodeList = ast.getNodes();
        String[] actualArray = new String[7];
        String[] actualArray1 = new String[5];
        String[] actualArray2 = new String[2];
        String[] actualArray3 = new String[2];
        String[] actualArray4 = new String[3];
        String[] actualArray5 = new String[2];
        CommandNode node = nodeList.get(0);
        if (node instanceof CommandNode.SimulationCommandNode) { // node is setup command
            CommandNode.SimulationCommandNode SimuNode = (CommandNode.SimulationCommandNode) node;
            actualArray[0] = SimuNode.getKeyword();
            actualArray[1] = SimuNode.getIdentifier();
            actualArray[2] = String.valueOf(SimuNode.getNInteger());
            actualArray[3] = SimuNode.getNFloats().get(0);
            actualArray[4] = SimuNode.getNFloats().get(1);
            actualArray[5] = SimuNode.getNFloats().get(2);
            actualArray[6] = SimuNode.getNFloats().get(3);
            assertEquals(expectedArray.length, actualArray.length);
            assertArrayEquals(expectedArray, actualArray);
        }
        node = nodeList.get(1);
        if (node instanceof CommandNode.PrefCommandNode) { // node is pref command
            CommandNode.PrefCommandNode PrefNode = (CommandNode.PrefCommandNode) node;
            actualArray1[0] = PrefNode.getKeyword();
            actualArray1[1] = PrefNode.getnFloats().get(0);
            actualArray1[2] = PrefNode.getnFloats().get(1);
            actualArray1[3] = PrefNode.getnFloats().get(2);
            actualArray1[4] = PrefNode.getnFloats().get(3);
            assertEquals(expectedArray1.length, actualArray1.length);
            assertArrayEquals(expectedArray1, actualArray1);
        }
        node = nodeList.get(2);
        if (node instanceof CommandNode.VacantCommandNode) { // node is vacant command
            CommandNode.VacantCommandNode VacantNode = (CommandNode.VacantCommandNode) node;
            actualArray2[0] = VacantNode.getKeyword();
            actualArray2[1] = VacantNode.getIdentifier();
            assertEquals(expectedArray2.length, actualArray2.length);
            assertArrayEquals(expectedArray2, actualArray2);
        }
        node = nodeList.get(3);
        if (node instanceof CommandNode.UsingCommandNode) { // node is using command
            CommandNode.UsingCommandNode UsingNode = (CommandNode.UsingCommandNode) node;
            actualArray3[0] = UsingNode.getKeyword();
            actualArray3[1] = UsingNode.getIdentifier();
            assertEquals(expectedArray3.length, actualArray3.length);
            assertArrayEquals(expectedArray3, actualArray3);
        }
        node = nodeList.get(4);
        if (node instanceof CommandNode.RunCommandNode) { // node is run command
            CommandNode.RunCommandNode RunNode = (CommandNode.RunCommandNode) node;
            actualArray4[0] = RunNode.getKeyword();
            actualArray4[1] = RunNode.getIdentifier();
            actualArray4[2] = String.valueOf(RunNode.getNInteger());
            assertEquals(expectedArray4.length, actualArray4.length);
            assertArrayEquals(expectedArray4, actualArray4);
        }  //'Setup simulation(Example1) with 4 as (20, 35, 20, 25)'
        node = nodeList.get(5);
        if (node instanceof CommandNode.ReportCommandNode) { // node is report command
            CommandNode.ReportCommandNode ReportNode = (CommandNode.ReportCommandNode) node;
            actualArray5[0] = ReportNode.getKeyword();
            actualArray5[1] = ReportNode.getIdentifiers().get(0);
            assertEquals(expectedArray5.length, actualArray5.length);
            assertArrayEquals(expectedArray5, actualArray5);
        }
    }
}
