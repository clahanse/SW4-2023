import org.nlogo.app.App;
import org.nlogo.headless.HeadlessWorkspace;

public class Model {
    // run Schelling model with 2 groups and interface
    public static void runModelA(String modelType, int perc_vacant_space, int perc_group1, int perc_group2,
                                 int perc_similarity_wanted_group1, int perc_similarity_wanted_group2, int NoTicks) {
        String ModelLibrary = "C:/Program Files/NetLogo 6.3.0/models/Sample Models/Social Science/";

        App.main(new String[]{});
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        App.app().open(ModelLibrary + modelType + ".nlogo", false);
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            App.app().command("set perc_group1 " + perc_group1);
            App.app().command("set perc_group2 " + perc_group2);
            App.app().command("set perc_vacant_space " + perc_vacant_space);
            App.app().command("set perc_similarity_wanted_group1 " + perc_similarity_wanted_group1);
            App.app().command("set perc_similarity_wanted_group2 " + perc_similarity_wanted_group2);
            App.app().command("setup");
            App.app().command("repeat " + NoTicks + " [ go ]");

            System.out.println("- Number of agents: " + App.app().report("count turtles"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [color = color1 ]") + ", " +
                    "* Group 2: " + App.app().report("count turtles with [color = color2 ]"));
            System.out.println("- Agents happy: " + App.app().report("count turtles with [happy]"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [happy and color = color1]")
                    + ", " + "* Percentage: " + App.app().report("percent_happy_group1"));
            System.out.println("  * Group 2: " + App.app().report("count turtles with [happy and color = color2]")
                    + ", " + "* Percentage: " + App.app().report("percent_happy_group2"));
            System.out.println("- Agents unhappy: " + App.app().report("count turtles with [not happy]"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [not happy and color = color1]")
                    + ", " + "* Percentage: " + App.app().report("percent_unhappy_group1") + ", " +
                    "* Segregation: " + App.app().report("segregation_group1"));
            System.out.println("  * Group 2: " + App.app().report("count turtles with [not happy and color = color2]")
                    + ", " + "* Percentage: " + App.app().report("percent_unhappy_group2") + ", " +
                    "* Segregation: " + App.app().report("segregation_group2"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // run Schelling model with 3 groups and interface
    public static void runModelB(String modelType, int perc_vacant_space, int perc_group1, int perc_group2,
                                 int perc_group3, int perc_similarity_wanted_group1, int perc_similarity_wanted_group2,
                                 int perc_similarity_wanted_group3, int NoTicks) {
        String ModelLibrary = "C:/Program Files/NetLogo 6.3.0/models/Sample Models/Social Science/";

        App.main(new String[]{});
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        App.app().open(ModelLibrary + modelType + ".nlogo", false);
                    } catch (java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            App.app().command("set perc_group1 " + perc_group1);
            App.app().command("set perc_group2 " + perc_group2);
            App.app().command("set perc_group3 " + perc_group3);

            App.app().command("set perc_vacant_space " + perc_vacant_space);
            App.app().command("set perc_similarity_wanted_group1 " + perc_similarity_wanted_group1);
            App.app().command("set perc_similarity_wanted_group2 " + perc_similarity_wanted_group2);
            App.app().command("set perc_similarity_wanted_group3 " + perc_similarity_wanted_group3);
            App.app().command("setup");
            App.app().command("repeat " + NoTicks + " [ go ]");

            System.out.println("- Number of agents: " + App.app().report("count turtles"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [color = color1 ]") + ", " +
                    "* Group 2: " + App.app().report("count turtles with [color = color2 ]") + ", " +
                    "* Group 3: " + App.app().report("count turtles with [color = color3 ]"));
            System.out.println("- Agents happy: " + App.app().report("count turtles with [happy]"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [happy and color = color1]")
                    + ", " + "* Percentage: " + App.app().report("percent_happy_group1"));
            System.out.println("  * Group 2: " + App.app().report("count turtles with [happy and color = color2]")
                    + ", " + "* Percentage: " + App.app().report("percent_happy_group2"));
            System.out.println("  * Group 3: " + App.app().report("count turtles with [happy and color = color3]")
                    + ", " + "* Percentage: " + App.app().report("percent_happy_group3"));
            System.out.println("- Agents unhappy: " + App.app().report("count turtles with [not happy]"));
            System.out.println("  * Group 1: " + App.app().report("count turtles with [not happy and color = color1]")
                    + ", " + "* Percentage: " + App.app().report("percent_unhappy_group1") + ", " +
                    "* Segregation: " + App.app().report("segregation_group1"));
            System.out.println("  * Group 2: " + App.app().report("count turtles with [not happy and color = color2]")
                    + ", " + "* Percentage: " + App.app().report("percent_unhappy_group2") + ", " +
                    "* Segregation: " + App.app().report("segregation_group2"));
            System.out.println("  * Group 3: " + App.app().report("count turtles with [not happy and color = color3]")
                    + ", " + "* Percentage: " + App.app().report("percent_unhappy_group3") + ", " +
                    "* Segregation: " + App.app().report("segregation_group3"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
  
}
