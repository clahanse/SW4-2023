import org.nlogo.app.App;
import org.nlogo.headless.HeadlessWorkspace;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class Model {

    // run Schelling model with 2 groups and interface
    public static void runModelA(String report, String modelType, float perc_vacant_space, List<String> perc_group,
                                 List<String> perc_similarity, int NoTicks, String outputFile) {
        String ModelLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\";
        PrintWriter writer = null;
        float perc_group1 = Float.parseFloat(perc_group.get(0));
        float perc_group2 = Float.parseFloat(perc_group.get(1));
        float perc_similarity_wanted_group1 = Float.parseFloat(perc_similarity.get(0));
        float perc_similarity_wanted_group2 = Float.parseFloat(perc_similarity.get(1));

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
            // Modify percentage of groups, similarity and vacant
            App.app().command("set perc_group1 " + perc_group1);
            App.app().command("set perc_group2 " + perc_group2);
            App.app().command("set perc_vacant_space " + perc_vacant_space);
            App.app().command("set perc_similarity_wanted_group1 " + perc_similarity_wanted_group1);
            App.app().command("set perc_similarity_wanted_group2 " + perc_similarity_wanted_group2);
            App.app().command("setup");
            App.app().command("repeat " + NoTicks + " [ go ]");
            // Set name output file and path
            String nameFileNetLogo = "Report_" + report + "_" + "2" + "_" + modelType;
            File file = new File(ModelLibrary + nameFileNetLogo + ".txt");
            // Create a PrintWriter to write to the output file
            writer = new PrintWriter(new FileWriter(file));
            writer.println("\n+ REPORT SIMULATION " + report + ":");
            writer.println("- Number of agents: " + App.app().report("count turtles"));
            for (int i = 1; i <= 2; i++) {
                String groupColor = "color" + i;
                writer.println("  * Group " + i + ": " + App.app().report("count turtles with [color = "
                        + groupColor + " ]"));
                writer.println("    - Happy agents: " + App.app().report("count turtles with [happy and color = "
                        + groupColor + "]")
                        + ", Percentage: " + App.app().report("percent_happy_group" + i));
                writer.println("    - Unhappy agents: " + App.app().report("count turtles with [not happy and color = "
                        + groupColor + "]") + ", Percentage: " + App.app().report("percent_unhappy_group" + i)
                        + ", Segregation: " + App.app().report("segregation_group" + i));
            }
            // Print information for each group to console
            System.out.println("- Number of agents: " + App.app().report("count turtles"));
            for (int i = 1; i <= 2; i++) {
                String groupColor = "color" + i;
                System.out.println("  * Group " + i + ": " + App.app().report("count turtles with [color = "
                        + groupColor + " ]"));
                System.out.println("    - Happy agents: " + App.app().report("count turtles with [happy and color = "
                        + groupColor + "]") + ", Percentage: " + App.app().report("percent_happy_group" + i));
                System.out.println("    - Unhappy agents: " + App.app().report("count turtles with [not happy and color = "
                        + groupColor + "]") + ", Percentage: " + App.app().report("percent_unhappy_group" + i)
                        + ", Segregation: " + App.app().report("segregation_group" + i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    // Run Schelling model from code generator
    public static void runModelSchelling(String report, String modelType, int NoTicks,
                                         int numGroups, String outputFile) throws InterruptedException {
        HeadlessWorkspace workspace = HeadlessWorkspace.newInstance();
        String ModelLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\";
        PrintWriter writer = null;

        try {
            // Open the model
            workspace.open(ModelLibrary + modelType + ".nlogo", false);
            workspace.command("setup");
            workspace.command("repeat " + NoTicks + " [ go ]");
            // Path for output file
            File file = new File(ModelLibrary + outputFile + ".txt");
            // Create a PrintWriter to write to the output file
            if (file.exists()) {
                writer = new PrintWriter(new FileWriter(file, true));
            } else {
                writer = new PrintWriter(new FileWriter(file));
            }
            writer.println("\n+ REPORT SIMULATION " + report + ":");
            writer.println("- Number of agents: " + workspace.report("count turtles"));
            for (int i = 1; i <= numGroups; i++) {
                String groupColor = "color" + i;
                writer.println("  * Group " + i + ": " + workspace.report("count turtles with [color = "
                        + groupColor + " ]"));
                writer.println("    - Happy agents: " + workspace.report("count turtles with [happy and color = "
                        + groupColor + "]")
                        + ", Percentage: " + workspace.report("percent_happy_group" + i));
                writer.println("    - Unhappy agents: " + workspace.report("count turtles with [not happy and color = "
                        + groupColor + "]") + ", Percentage: " + workspace.report("percent_unhappy_group" + i)
                        + ", Segregation: " + workspace.report("segregation_group" + i));
            }
            // Print information for each group to console
            System.out.println("- Number of agents: " + workspace.report("count turtles"));
            for (int i = 1; i <= numGroups; i++) {
                String groupColor = "color" + i;
                System.out.println("  * Group " + i + ": " + workspace.report("count turtles with [color = "
                        + groupColor + " ]"));
                System.out.println("    - Happy agents: " + workspace.report("count turtles with [happy and color = "
                        + groupColor + "]") + ", Percentage: " + workspace.report("percent_happy_group" + i));
                System.out.println("    - Unhappy agents: " + workspace.report("count turtles with [not happy and color = "
                        + groupColor + "]") + ", Percentage: " + workspace.report("percent_unhappy_group" + i)
                        + ", Segregation: " + workspace.report("segregation_group" + i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
            workspace.dispose();
        }
    }

    // Run available model
    public static void runModel(String movingType, String report, String perc_vacant_space,
                                List<String> perc_group, List<String> perc_similarity, int NoTicks) throws InterruptedException {
        String outputFilePath = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\simulation.txt";
        String modelType = "";
        if (movingType.equals("RANDOM")) {
            modelType = "Schelling's model-A-Random";
            Model.runModelA(report, modelType, Float.parseFloat(perc_vacant_space), perc_group,
                    perc_similarity, NoTicks, outputFilePath);
        }
        if (movingType.equals("SCHELLING")) {
            modelType = "Schelling's model-A-Schelling";
            Model.runModelA(report, modelType, Float.parseFloat(perc_vacant_space), perc_group,
                    perc_similarity, NoTicks, outputFilePath);
        }
    }

    // Run Model created from code generator
    public static void runModelCode(int numGroups, String movingType, String report,
                                    int NoTicks, String outputFile) throws InterruptedException {
        String modelType = report + "_" + numGroups + "_" + movingType;
        Model.runModelSchelling(report, modelType, NoTicks, numGroups, outputFile);
    }
}
