import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SMLAParser_ClassTest {
    // CHECK PARSER CLASS
    public static void main(String[] args) throws Exception {
        List<Token> tokens = new ArrayList<>();
        // Input from Scanner
        // Variable group1 = 30.2, group2 = 25, group3 = 20, ABC = 25
        tokens.add(new Token("variable", "GROUP1", "", "", 1));
        tokens.add(new Token("n_float", "29.5", "", "", 1));
        tokens.add(new Token("end_command", "end of command", "", "", 1));
        tokens.add(new Token("variable", "GROUP2", "", "", 2));
        tokens.add(new Token("n_integer", "25", "", "", 2));
        tokens.add(new Token("end_command", "end of command", "", "", 2));
        tokens.add(new Token("variable", "GROUP3", "", "", 3));
        tokens.add(new Token("n_integer", "20", "", "", 3));
        tokens.add(new Token("end_command", "end of command", "", "", 3));
        tokens.add(new Token("variable", "ABC", "", "", 4));
        tokens.add(new Token("n_integer", "25", "", "", 4));
        tokens.add(new Token("end_command", "end of command", "", "", 4));

        // Run simulation Example1 FOR 50 TICKS
        tokens.add(new Token("run", "RUN SIMULATION", "", "", 5));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 5));
        tokens.add(new Token("phrase", "FOR", "", "", 5));
        tokens.add(new Token("n_integer", "50", "", "", 5));
        tokens.add(new Token("phrase", "TICKS", "", "", 5));
        tokens.add(new Token("end_command", "end of command", "", "", 5));

        // Report simulation Example1
        tokens.add(new Token("report", "REPORT SIMULATION", "", "", 6));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 6));
        tokens.add(new Token("end_command", "end of command", "", "", 6));

        // Using random move
        tokens.add(new Token("using", "USING", "", "", 7));
        tokens.add(new Token("typeMoving", "RANDOM", "", "", 7));
        tokens.add(new Token("phrase", "MOVE", "", "", 7));
        tokens.add(new Token("end_command", "end of command", "", "", 7));

        // Setup simulation (Example1) WITH 4 AS (Group1, Group2, Group3, ABC)
        tokens.add(new Token("simulation", "SETUP SIMULATION", "", "", 8));
        tokens.add(new Token("alphanumeric", "EXAMPLE1", "", "", 8));
        tokens.add(new Token("phrase", "WITH", "", "", 8));
        tokens.add(new Token("n_integer", "4", "", "", 8));
        tokens.add(new Token("phrase", "AS", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP1", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP2", "", "", 8));
        tokens.add(new Token("alphanumeric", "GROUP3", "", "", 8));
        tokens.add(new Token("alphanumeric", "ABC", "", "", 8));
        tokens.add(new Token("end_command", "end of command", "", "", 8));

        // Where pref is (10, 20, 30, 40)
        tokens.add(new Token("pref", "WHERE PREF IS", "", "", 9));
        tokens.add(new Token("n_integer", "10", "", "", 9));
        tokens.add(new Token("n_integer", "20", "", "", 9));
        tokens.add(new Token("n_integer", "30", "", "", 9));
        tokens.add(new Token("n_integer", "40", "", "", 9));
        tokens.add(new Token("end_command", "end of command", "", "", 9));

        // Where vacant is Vacant
        tokens.add(new Token("vacant", "WHERE VACANT IS", "", "", 10));
        tokens.add(new Token("n_integer", "65", "", "", 10));
        tokens.add(new Token("end_command", "end of command", "", "", 10));

        try {
            System.out.println("\n *** Parser result: ");
            SMLAParser parser = new SMLAParser(tokens);
            parser.parseProgram();
            System.out.println(" *** Parser success");
        } catch (ParseException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("\n*** Token list from Parser ");
        SMLAScanner smlaScanner = new SMLAScanner();
        smlaScanner.printTokens(SMLAParser.getTokenList());
        System.out.println("*** End of the list - Parser successful");
    }
}
