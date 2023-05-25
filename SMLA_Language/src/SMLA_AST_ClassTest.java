import java.util.ArrayList;
import java.util.List;

public class SMLA_AST_ClassTest {
       // CHECK AST CLASS
       public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        // Input from Parser
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

        System.out.println("\n *** AST output: ");
        try {
            new SMLA_AST(tokens).parseProgram();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*** End of node list - Create AST successfully");
    }
}
