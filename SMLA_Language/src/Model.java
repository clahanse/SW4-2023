import org.nlogo.app.App;
import org.nlogo.headless.HeadlessWorkspace;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Model {

    // run Schelling model with 2 groups and interface
    public static void runModelA(String report, String modelType, float perc_vacant_space, List<String> perc_group,
                                 List<String> perc_similarity, int NoTicks) {
        String ModelLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\";
        PrintWriter writer = null;
        float perc_group1 = Float.parseFloat(perc_group.get(0));
        float perc_group2 = Float.parseFloat(perc_group.get(1));
        float perc_similarity_wanted_group1 = Float.parseFloat(perc_similarity.get(0));
        float perc_similarity_wanted_group2 = Float.parseFloat(perc_similarity.get(1));

        App.main(new String[]{});
        try {
            java.awt.EventQueue.invokeAndWait(() -> {
                try {
                    App.app().open(ModelLibrary + modelType + ".nlogo", false);
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
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
                                List<String> perc_group, List<String> perc_similarity, int NoTicks) {
        String modelType;
        if (movingType.equals("RANDOM")) {
            modelType = "Schelling's model-A-Random";
            Model.runModelA(report, modelType, Float.parseFloat(perc_vacant_space), perc_group,
                    perc_similarity, NoTicks);
        }
        if (movingType.equals("SCHELLING")) {
            modelType = "Schelling's model-A-Schelling";
            Model.runModelA(report, modelType, Float.parseFloat(perc_vacant_space), perc_group,
                    perc_similarity, NoTicks);
        }
    }

    // Run Model created from code generator
    public static void runModelCode(int numGroups, String movingType, String report,
                                    int NoTicks, String outputFile) throws InterruptedException {
        String modelType = report + "_" + numGroups + "_" + movingType;
        runModelSchelling(report, modelType, NoTicks, numGroups, outputFile);
    }

    // Simulate relevant simulations
    public void processRelevantSimulations(List<String> reportList, List<List<Token>> simulations,
                       SMLACodeGenerator codeGenerator, String saveLibrary) throws InterruptedException {
        List<List<Token>> relevantSimulations = new ArrayList<>();
        StringBuilder outputFile = new StringBuilder();
        // Choose relevant simulations from report list
        for (int k = 0; k < reportList.size(); k++) {
            String reportName = reportList.get(k);
            if (k == reportList.size() - 1) { // get name for output file for more simulation
                outputFile.append(reportList.get(k));
            } else {
                outputFile.append(reportList.get(k)).append("_");
            }
            for (List<Token> simulation : simulations) {
                if (simulation.get(1).getValue().equals(reportName)) {
                    relevantSimulations.add(simulation);
                    System.out.println("- Relevant simulation: " + simulation);
                    break;
                }
            }
        }
        // Run simulation in relevant simulations list
        for (int h = 0; h < relevantSimulations.size(); h++) {
            List<String> percGroup = new ArrayList<>();
            List<String> percSimilarity = new ArrayList<>();
            List<Token> currentSimulation = relevantSimulations.get(h);
            int numGroups = 0;
            int NoTicks = 0;
            float percVacantSpace = 50;
            String movingType = "RANDOM";
            String report = "";
            String outputFile1 = "";
            // Get parameters from relevant simulation
            for (Token token : currentSimulation) {
                switch (token.getType()) {
                    case "numGroups" -> numGroups = Integer.parseInt(token.getValue());
                    case "perc_group1" -> {
                        int indexGroup = currentSimulation.indexOf(token);

                        for (int t = indexGroup; t < indexGroup + numGroups; t++) {
                            percGroup.add(currentSimulation.get(t).getValue());
                        }
                    }
                    case "perc_similarity_wanted_group1" -> {
                        int indexGroup = currentSimulation.indexOf(token);

                        for (int t = indexGroup; t < indexGroup + numGroups; t++) {
                            percSimilarity.add(currentSimulation.get(t).getValue());
                        }
                    }
                    case "perc_vacant_space" -> percVacantSpace = Float.parseFloat(token.getValue());
                    case "movingType" -> movingType = token.getValue();
                    case "NoTicks" -> NoTicks = Integer.parseInt(token.getValue());
                    case "nameSimulation" -> report = token.getValue();
                }

                if (h == 0) { // get name for output file for a simulation
                    outputFile1 = "Report_" + report + "_" + numGroups + "_" + movingType;
                }
            }

            if (percSimilarity.isEmpty()) { // set percent similarities if they haven't been set
                for (int y = 0; y < numGroups; y++) {
                    percSimilarity.add("50");
                }
            }

            if (reportList.size() == 1) {
                outputFile = new StringBuilder(outputFile1);
            }

            if (numGroups == 2 && reportList.size() == 1) { // using Model only for 2 groups and 1 report
                // run and print simulation report to the screen
                System.out.println("\n+ REPORT SIMULATION " + report + ":");
                runModel(movingType, report, String.valueOf(percVacantSpace), percGroup, percSimilarity, NoTicks);
            } else {
                // NetLogo code generator
               codeGenerator.generateNLogoFile(saveLibrary, report, numGroups, movingType,
                        String.valueOf(percVacantSpace), percGroup, percSimilarity);
                // run and print simulation report to the screen
                System.out.println("\n+ REPORT SIMULATION " + report + ":");
                runModelCode(numGroups, movingType, report, NoTicks, outputFile.toString());
            }
        }
    }
}
