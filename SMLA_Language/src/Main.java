
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
            String saveLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\"; // path to model and input file
            File inputFile = new File(saveLibrary + "input.txt");
            if (!inputFile.exists()) {
                throw new Exception("Input file does not exist.");
            }
            if (inputFile.length() == 0) {
                throw new Exception("File is empty");
            }
            Scanner scanner = new Scanner(inputFile);
            SMLAScanner smlaScanner = new SMLAScanner(scanner);
            List<CommandNode> nodesList = new ArrayList<>();
            List<Token> tokensParser = new ArrayList<>();
            StringBuilder input = new StringBuilder();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue; // Skip blank lines
                }
                String UpperLine = toUpperCase(line);
                input.append(UpperLine).append("\n");
            }
            // tokenize input using scanner
            smlaScanner.tokenizeInput(input.toString());
            System.out.println("\n A- Token list from Scanner: ");
            int i = 1;
            for (Token token : SMLAScanner.getTokenList()) {
                // tokens.add(token);
                if (token.getType().equals("variable")) { // print list of tokens to screen
                    System.out.println(i + "-type: " + token.getType() + ", value: " + token.getValue()
                            + ", variable value: " + token.getVariableValue() + ", line: " + token.getLineNumber());
                } else {
                    System.out.println(i + "-type: " + token.getType() + ", value: " + token.getValue() +
                            ", line: " + token.getLineNumber());
                }
                i++;
            }
            System.out.println("*** End of the list - Scanner successful");

            // parse tokens using SMLAParser
            System.out.println("\n B- Parser result: ");
            SMLAParser parser = new SMLAParser(SMLAScanner.getTokenList());
            parser.parseProgram();

            System.out.println("\n*** Token list from Parser ");
            tokensParser = SMLAParser.getTokenList();
            int j = 1;
            for (Token token : tokensParser) {
                if (token.getType().equals("variable")) { // print list of tokens to screen
                    System.out.println(j + "-type: " + token.getType() + ", value: " + token.getValue()
                            + ", variable value: " + token.getVariableValue() + ", line: " + token.getLineNumber());
                } else {
                    System.out.println(j + "-type: " + token.getType() + ", value: " + token.getValue() +
                            ", line: " + token.getLineNumber());
                }
                j++;
            }
            System.out.println("*** End of the list - Parser successful");

            // parse tokens using SMLA_AST
            System.out.println("\n C- AST output: ");
            SMLA_AST ast = new SMLA_AST(SMLAParser.getTokenList());
            ast.parseCommands();
            System.out.println("*** End of node list - Create AST successfully");

            // save the node token lists to a file
            File file = new File(saveLibrary + "output.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("\nToken List:\n");
                for (Token token : SMLAScanner.getTokenList()) {
                    writer.println(token.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // run simulation in NetLogo
            System.out.println("\n D- SIMULATION: ");
            SMLACodeGenerator codeGenerator = new SMLACodeGenerator();
            String movingType = "RANDOM";
            String report = "";
            int numGroups = 0;
            float perc_vacant_space = 50;
            int NoTicks = 0;
            int numReport = 0;
            String outputFile = "Report_";
            String outputFile1 = "";
            List<String> reportList = new ArrayList<>();
            List<Token> commands = new ArrayList<>();
            List<List<Token>> simulations = new ArrayList<>();
            nodesList = ast.getNodes();
            List<String> perc_group = new ArrayList<>();
            List<String> perc_similarity = new ArrayList<>();

            // Iterate over all nodes in the list
            for (int count = 0; count < nodesList.size(); count++) {
                CommandNode node = nodesList.get(count);
                // Check if the node is a SimulationCommandNode
                if (node instanceof CommandNode.SimulationCommandNode) { // node is setup simulation command
                    CommandNode.SimulationCommandNode simNode = (CommandNode.SimulationCommandNode) node;
                    numGroups = simNode.getNInteger();
                    commands.add(new Token("numGroups", Integer.toString(numGroups)));
                    commands.add(new Token("nameSimulation", simNode.getIdentifier()));
                    if (simNode.getNFloats() != null) { // get percentage of groups
                        List<String> nFloats = simNode.getNFloats();
                        for (int q = 0; q < numGroups; q++) {
                            commands.add(new Token("perc_group" + (q + 1), nFloats.get(q)));
                        }
                    } else { // set values for percentage of groups when they are zero
                        float value = (float) (100.0 / numGroups);
                        float newValue = (float) (Math.round(value * 10.0) / 10.0);

                        for (int q = 0; q < numGroups; q++) {
                            commands.add(new Token("perc_group" + (q + 1), Float.toString(newValue)));

                        }
                    }
                } else if (node instanceof CommandNode.PrefCommandNode) { // node is Pref command
                    CommandNode.PrefCommandNode PrefNode = (CommandNode.PrefCommandNode) node;
                    if (PrefNode.getnFloats() != null) {
                        List<String> nFloats = PrefNode.getnFloats();
                        for (int q = 0; q < numGroups; q++) { // get percent similarity of groups
                            commands.add(new Token("perc_similarity_wanted_group" + (q + 1), nFloats.get(q)));
                        }
                    }
                } else if (node instanceof CommandNode.VacantCommandNode) { // node is vacant command
                    CommandNode.VacantCommandNode VacantNode = (CommandNode.VacantCommandNode) node;
                    commands.add(new Token("perc_vacant_space", VacantNode.getIdentifier()));
                } else if (node instanceof CommandNode.UsingCommandNode) { // node is using command
                    CommandNode.UsingCommandNode UsingNode = (CommandNode.UsingCommandNode) node;
                    commands.add(new Token("movingType", UsingNode.getIdentifier()));
                } else if (node instanceof CommandNode.RunCommandNode) { // node is run command
                    CommandNode.RunCommandNode RunNode = (CommandNode.RunCommandNode) node;
                    int numTicks = RunNode.getNInteger();
                    if (numTicks == 0) { // set value for ticks if it is zero
                        numTicks = 10;
                    }
                    commands.add(new Token("NoTicks", Integer.toString(numTicks)));
                    // end of simulation
                    simulations.add(commands);
                    commands = new ArrayList<>();
                }
            }
            // get number and name of reports
            for (CommandNode node : nodesList) {
                if (node instanceof CommandNode.ReportCommandNode) {

                    CommandNode.ReportCommandNode reportNode = (CommandNode.ReportCommandNode) node;
                    numReport = reportNode.getIdentifiers().size();
                    for (int m = 0; m < reportNode.getIdentifiers().size(); m++) {
                        String reportIdentifier = reportNode.getIdentifiers().get(m);

                       boolean check= SMLAParser.matchNameSimulation(tokensParser,reportIdentifier);
                        if( check){
                            reportList.add(reportIdentifier);
                        }
                   }
                }
            }
            // Choose relevant simulation from list of simulations
            List<List<Token>> relevant_simulations = new ArrayList<>();
            for (int k = 0; k < reportList.size(); k++) {
                String reportName = reportList.get(k).toString();
                if(k == reportList.size()-1){ //get name for output file for more simulation
                    outputFile = outputFile+reportList.get(k);
                }else{
                    outputFile =outputFile +reportList.get(k)+"_";
                }
                for (List<Token> simulation : simulations) {
                    if (simulation.get(1).getValue().equals(reportName)) {
                        relevant_simulations.add(simulation);
                        System.out.println("- Relevant simulation: " + simulation);
                        break;
                    }
                }
            }
            // get parameters from list of relevant simulation
            for (int h = 0; h < relevant_simulations.size(); h++) {
                perc_group.clear();
                perc_similarity.clear();
                List<Token> currentSimulation = relevant_simulations.get(h);
                for (int l = 0; l < currentSimulation.size(); l++) {
                    Token token = currentSimulation.get(l);
                    if (token.getType().equals("numGroups")) {
                        numGroups = Integer.parseInt(token.getValue());
                    }
                    if (token.getType().equals("perc_group1")) {
                        int indexGroup = 0;
                        indexGroup = l;
                        for (int t = indexGroup; t < indexGroup + numGroups; t++) { // get percent groups and add them to list
                            perc_group.add(currentSimulation.get(t).getValue());
                        }
                    }
                    if (token.getType().equals("perc_similarity_wanted_group1")) {
                        int indexGroup = 0;
                        indexGroup = l;
                        for (int t = indexGroup; t < indexGroup + numGroups; t++) { // get percent similarity and add them to list
                            perc_similarity.add(currentSimulation.get(t).getValue());
                        }
                    }
                    if (token.getType().equals("perc_vacant_space")) { // get vacant
                        perc_vacant_space = Float.parseFloat(token.getValue());
                    }
                    if (token.getType().equals("movingType")) { // get type of moving: random or schelling
                        movingType = token.getValue();
                    }
                    if (token.getType().equals("NoTicks")) { // get number of ticks
                        NoTicks = Integer.parseInt(token.getValue());
                    }
                    if (token.getType().equals("nameSimulation")) { // get name of simulation
                        report = token.getValue();
                    }
                    if(h==0){ // get name for output file for a simulation
                        outputFile1 = "Report_"+ report + "_" + numGroups + "_" + movingType;
                    }
                }
                if (perc_similarity.size() == 0) { // set percent similarities if they don't be set
                    for (int y = 0; y < numGroups; y++) {
                        perc_similarity.add("50");
                    }
                }
                if(reportList.size()==1){
                      outputFile=outputFile1;
                }
                // choose Model or code generator
                if (numReport == 1 && numGroups == 2) { // using Model only for 2 groups and 1 report
                    // run and print simulation report to screen
                    System.out.println("\n+ REPORT SIMULATION " + report + ":");
                    Model.runModel(movingType, report, String.valueOf(perc_vacant_space), perc_group, perc_similarity, NoTicks);
                } else { // create NetLogo code for all the rest
                    // NetLogos code generator
                    codeGenerator.generateNLogoFile(saveLibrary, report, numGroups, movingType,
                            String.valueOf(perc_vacant_space), perc_group, perc_similarity);
                    // run and print simulation report to screen
                    System.out.println("\n+ REPORT SIMULATION " + report + ":");
                    Model.runModelCode(numGroups, movingType, report, NoTicks, outputFile);
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
