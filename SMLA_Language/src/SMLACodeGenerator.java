import org.nlogo.app.App;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SMLACodeGenerator {

    public SMLACodeGenerator() {
        super();
    }

    // CREATE NETLOGO CODE
    public void generateNLogoFile(String saveLibrary, String name, int noOfGroups, String moving,
                                  String perc_vacant, List<String> perc_group, List<String> perc_similarity) {
        String finalScript = "";

        finalScript = finalScript + writeGlobals(noOfGroups) + "\n" + writeTurtles() + "\n" +
                writePatches(noOfGroups) + "\n" + writeSetup(noOfGroups, perc_vacant,
                perc_group, perc_similarity) + "\n" + writeToUpdate() + "\n" +
                writeComputeHappiness(noOfGroups) + "\n" + writeComputePotential(noOfGroups) + "\n";
        finalScript = finalScript + writeShowHappiness(moving) + "\n" + writeUpdateGlobals(noOfGroups) + "\n" +
                writeToGo(moving) + "\n" + writeMoveUnhappy(moving) + "\n" + writeRandomMove1() + "\n" +
                writeRandomMove2() + "\n" + writeSchellingMove(noOfGroups) + "\n" + writeEndString();

        // This procedure writes the setup procedure to a NetLogo-file
        String nameFileNetLogo = name + "_" + Integer.toString(noOfGroups) + "_" + moving;
        File file = new File(saveLibrary + nameFileNetLogo + ".nlogo");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(finalScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CREATE GLOBAL VARIABLES
    private String writeGlobals(int noOfGroups) {

        String twoblanks = "  ";
        String globals = "globals [\n";
        globals = globals + twoblanks + "percent_unhappy\n";

        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "percent_unhappy_group" + i + "\n";
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "percent_happy_group" + i + "\n";
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "perc_similarity_wanted_group" + i + "\n";
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "segregation_group" + i + "\n";
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "color" + i + "\n";
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals = globals + twoblanks + "perc_group" + i + "\n";
        }
        globals = globals + twoblanks + "perc_similarity_wanted\n";
        globals = globals + twoblanks + "perc_vacant_space\n";
        globals = globals + twoblanks + "move-type\n";
        globals = globals + "]\n";
        return globals;
    }

    // CREATE AGENTS
    private String writeTurtles() {
        String twoblanks = "  ";
        String turtlesOwn = "turtles-own [\n";
        String turtleCharacteristics = "happy\n" + twoblanks + "similar-nearby\n" + twoblanks + "total-nearby\n";
        return turtlesOwn + twoblanks + turtleCharacteristics + "]\n";
    }

    // CREATE PATCHES
    private String writePatches(int noOfGroups) {
        String twoblanks = "  ";
        String patchesOwn = "patches-own [\n";
        String patchesHappy = "";
        for (int i = 1; i <= noOfGroups; i++) {
            patchesHappy = patchesHappy + twoblanks + "happy" + i + "\n";
        }
        return patchesOwn + patchesHappy + "]\n";
    }

    // SETUP MODEL
    private String writeSetup(int noOfGroups, String perc_vacant,
                              List<String> perc_group, List<String> perc_similarity) {
        int[] colors = {45, 85, 25, 23, 44, 50, 75, 100, 106, 121};
        String twoblanks = "  ";
        String setupStart = "to setup\n" + twoblanks + "ca\n";
        String setupEnd = twoblanks + "update\n" + twoblanks + "reset-ticks\n" + "end\n";
        String setupNoOfPeople = twoblanks
                + "let number-of-people int (world-width * world-height * (1 - perc_vacant_space / 100))\n";
        String setPerc_similarity = twoblanks + "set perc_vacant_space " + perc_vacant + "\n";
        String setNumOfGroup = "";
        String createGroup = "";
        String getLastPercent = "";
        String setPerc_group = "";
        String setPerc_vacant = "";
        String sum1 = "";
        String sum2 = "";
        String lastGroup = "";
        for (int i = 1; i <= noOfGroups; i++) {
            if (i == noOfGroups) {
                sum1 = sum1 + "perc_group" + i;
                lastGroup = "perc_group" + i;
            } else {
                sum1 = sum1 + "perc_group" + i + " + ";
            }
            setPerc_group = setPerc_group + twoblanks + "set perc_group" + i + " " + perc_group.get(i - 1) + "\n";
            setPerc_similarity = setPerc_similarity + twoblanks + "set perc_similarity_wanted_group"
                    + i + " " + perc_similarity.get(i - 1) + "\n";
        }
        for (int i = 1; i <= noOfGroups - 1; i++) {
            if (i == noOfGroups - 1) {
                sum2 = sum2 + "perc_group" + i;
            } else {
                sum2 = sum2 + "perc_group" + i + " + ";
            }
        }
        getLastPercent = twoblanks + "if " + sum1 + " > 100\n" + twoblanks + "   [" + "set " + lastGroup + " 100 - "
                + "(" + sum2 + ")" + "]\n";
        for (int i = 1; i <= noOfGroups; i++) {
            setupStart = setupStart + twoblanks + "set color" + i + " " + colors[i - 1] + ";\n";
            setNumOfGroup = setNumOfGroup + twoblanks + "let number-of-group" + i
                    + " int (number-of-people * perc_group" + i + " / 100)\n";
            if (i == 1) {
                createGroup = twoblanks + "ask n-of number-of-group" + i
                        + " patches [sprout 1 [set color color" + i + "]]\n";
            } else {
                createGroup = createGroup + twoblanks + "ask n-of number-of-group" + i
                        + " patches with [not any? turtles-here] [" +
                        "sprout 1 [set color color" + i + "]]\n";
            }
        }
        return setupStart + setPerc_vacant + setPerc_group + getLastPercent
                + setPerc_similarity + setupNoOfPeople + setNumOfGroup + createGroup + setupEnd;
    }

    // UPDATE VARIABLES AND AGENTS HAPPINESS
    private String writeToUpdate() {
        String twoblanks = "  ";
        String updateStart = "to update\n";
        String updateEnd = "end\n";
        return updateStart + twoblanks + "show-happiness\n" + twoblanks
                + "update-globals\n" + twoblanks + updateEnd;
    }

    // CALCULATION HAPPINESS
    private String writeComputeHappiness(int noOfGroups) {
        String twoblanks = "  ";
        String computeHappinessStart = "to compute-happiness\n";
        String computeHappinessEnd = "end\n";
        String totalNearby = twoblanks + "set total-nearby count turtles-on neighbors\n";
        String setSimilarNearby = twoblanks
                + "set similar-nearby count (turtles-on neighbors) with [color = [color] of myself]\n";
        for (int i = 1; i <= noOfGroups; i++) {
            setSimilarNearby = setSimilarNearby + twoblanks + "if color = color" + i
                    + " [set perc_similarity_wanted perc_similarity_wanted_group" + i + "]\n";
        }
        String ifElseTotalNearby = twoblanks + "ifelse (total-nearby = 0) [\n"
                + twoblanks + twoblanks + "set happy true] [\n" + twoblanks + twoblanks
                + "set happy (similar-nearby / total-nearby >= perc_similarity_wanted / 100)]\n";
        String ifElseHappy = twoblanks + "ifelse (happy = true) [\n" + twoblanks + twoblanks
                + "set shape \"face happy\"\n" + twoblanks + "] [\n" + twoblanks + twoblanks
                + "set shape \"face sad\"\n" + twoblanks + "]\n";
        return computeHappinessStart + totalNearby + setSimilarNearby
                + ifElseTotalNearby + ifElseHappy + computeHappinessEnd;
    }

    // CALCULATION GROUPS POTENTIAL HAPPINESS
    private String writeComputePotential(int noOfGroups) {
        String twoblanks = "  ";
        String computePotentialStart = "to compute-potential-happiness\n"
                + twoblanks + "let nearby count turtles-on neighbors\n";
        String computePotentialEnd = "end\n";
        String nearby = "";
        String setHappy = twoblanks + " ifelse (nearby = 0) [\n";
        String calcuHappy = "";
        for (int i = 1; i <= noOfGroups; i++) {
            nearby = nearby + twoblanks + "let nearby" + i
                    + " count (turtle-set (turtles-on neighbors) with [color = color" + i + "])\n";
            setHappy = setHappy + twoblanks + " set happy" + i + " true\n";
            calcuHappy = calcuHappy + twoblanks + " set happy" + i + " (nearby" + i
                    + " / nearby >= perc_similarity_wanted / 100)\n";
        }
        return computePotentialStart + nearby + setHappy + twoblanks + " ] [\n"
                + calcuHappy + twoblanks + " ]\n" + computePotentialEnd;
    }

    // SHOW HAPPINESS
    private String writeShowHappiness(String moving) {
        String twoblanks = "  ";
        String showHappinessStart = "to show-happiness\n";
        String showHappinessEnd = "end\n";
        String showHappiness = twoblanks + "ask turtles [compute-happiness]\n";
        String showPotential = twoblanks + "ask patches [compute-potential-happiness]\n";
        String returnHappy = "";
        if (moving.equalsIgnoreCase("random")) {
            returnHappy = showHappinessStart + showHappiness + showHappinessEnd;
        } else if (moving.equalsIgnoreCase("schelling")) {
            returnHappy = showHappinessStart + showHappiness + showPotential + showHappinessEnd;
        }
        return returnHappy;
    }

    // UPDATE CALCULATION GLOBAL VARIABLES
    private String writeUpdateGlobals(int noOfGroups) {
        String twoblanks = "  ";
        String updateGlobalsStart = "to update-globals\n";
        String updateGlobalsEnd = "end\n";
        String setPercentUnhappy = twoblanks
                + "set percent_unhappy precision ((count turtles with [not happy] / count turtles) * 100) 2\n";
        String setPercentHappy = "";
        String setSegregation = "";
        for (int i = 1; i <= noOfGroups; i++) {
            setPercentHappy = setPercentHappy + twoblanks + "set percent_happy_group" + i
                    + " precision ((count turtles with [happy and color = color" + i
                    + "] * 100) / (count turtles with [color = color" + i + "])) 2\n";
            setPercentUnhappy = setPercentUnhappy + twoblanks + "set percent_unhappy_group" + i
                    + " precision ((count turtles with [not happy and color = color" + i
                    + "] * 100) / (count turtles with [color = color" + i + "])) 2\n";
            setSegregation = setSegregation + twoblanks + "set segregation_group" + i
                    + " precision mean [count (turtles-on neighbors) with [color =\n"
                    + "    color" + i + "]] of turtles with [color = color" + i + "] 2\n";
        }
        return updateGlobalsStart + setPercentHappy + setPercentUnhappy
                + setSegregation + updateGlobalsEnd;
    }

    // RUN MODEL
    private String writeToGo(String moving) {
        String twoblanks = "  ";
        String goStart = "to go\n";
        String goEnd = "end\n";
        String go = twoblanks + "if not any? turtles with [not happy] [stop]\n";
        String returnGo = "";
        String choosingMoving = twoblanks + "let unhappy-count count turtles with [not happy]\n" +
                twoblanks + "let unhappy-ratio unhappy-count / count turtles\n" +
                twoblanks + "if unhappy-ratio > 0.5 [set move-type \"schelling\"]\n" +
                twoblanks + "if (unhappy-ratio > 0.2) [set move-type \"random2\"]\n" +
                twoblanks + "if (unhappy-ratio > 0) [set move-type \"random1\"]\n";
        String moveUpdate = twoblanks + "move-unhappy\n" + twoblanks + "update\n" + twoblanks + "tick\n";
        if (moving.equalsIgnoreCase("random")) {
            returnGo = goStart + go + moveUpdate + goEnd;
        } else if (moving.equalsIgnoreCase("schelling")) {
            returnGo = goStart + go + choosingMoving + moveUpdate + goEnd;
        }
        return returnGo;
    }

    // TYPE OF MOVING WHEN AGENTS AREN'T HAPPY
    private String writeMoveUnhappy(String moving) {
        String twoblanks = "  ";
        String moveUnhappyStart = "to move-unhappy\n";
        String moveUnhappyEnd = "end\n";
        String returnMoving = "";
        String moveUnhappy1 = twoblanks + "ask turtles with [not happy] [random-move1]\n";
        String moveUnhappy2 = twoblanks
                + "if (move-type = \"random1\") [ask turtles with [not happy ] [ random-move1]]\n" + twoblanks
                + "if (move-type = \"random2\") [ask turtles with [not happy ] [ without-interruption [random-move2]]]\n" + twoblanks
                + "if (move-type = \"schelling\") [ask (turtles with [not happy]) [without-interruption [schelling-move]]]\n";
        if (moving.equalsIgnoreCase("random")) {
            returnMoving = moveUnhappyStart + moveUnhappy1 + moveUnhappyEnd;
        } else if (moving.equalsIgnoreCase("schelling")) {
            returnMoving = moveUnhappyStart + moveUnhappy2 + moveUnhappyEnd;
        }
        return returnMoving;
    }

    // RANDOM MOVE
    private String writeRandomMove1() {
        String twoblanks = "  ";
        String randomMoveStart = "to random-move1\n";
        String randomMoveEnd = "end\n";
        String randomMove = twoblanks + "setxy random-pxcor random-pycor\n"
                + twoblanks + "if any? other turtles-here [random-move1]\n";
        return randomMoveStart + randomMove + randomMoveEnd;
    }

    // RANDOM MOVE RELATIVE TO PATCHES
    private String writeRandomMove2() {
        String twoblanks = "  ";
        String randomMoveStart = "to random-move2\n";
        String randomMoveEnd = "end\n";
        String randomMove = twoblanks + "ask one-of patches with [not any? turtles-here] [\n" +
                twoblanks + " set xcor [pxcor] of myself\n" +
                twoblanks + " set ycor [pycor] of myself]\n";
        return randomMoveStart + randomMove + randomMoveEnd;
    }

    // MOVE FOLLOWING SCHELLING CALCULATION
    private String writeSchellingMove(int noOfGroups) {
        String twoblanks = "  ";
        String schellingMoveStart = "to schelling-move\n";
        String schellingMoveEnd = "end\n";
        String schellingMove = "";
        for (int i = 1; i <= noOfGroups; i++) {
            schellingMove = schellingMove + twoblanks + "if (color = color" + i + ") [\n" + twoblanks
                    + " let target min-one-of (patches with [happy" + i
                    + " and not any? turtles-here]) [abs (pxcor - [xcor] of myself) + abs (pycor - [ycor]\n" + twoblanks
                    + "  of myself)]\n" + twoblanks + " if (target != nobody) [\n" + twoblanks
                    + "  set xcor [pxcor] of target\n" + twoblanks + "  set ycor [pycor] of target]]\n";
        }
        return schellingMoveStart + schellingMove + schellingMoveEnd;
    }

    private String writeEndString() {

        String separator = "@#$#@#$#@\n";
        String graphicsString = "GRAPHICS-WINDOW\n" + "375\n10\n791\n427\n" +
                "-1\n-1\n8.0\n1\n10\n1\n1\n1\n0\n1\n1\n1\n-25\n25\n-25\n25\n1\n1\n1\nticks\n30.0\n";
        String infoString = "## WHAT IS IT?\n";
        String versionString = "NetLogo 6.3.0\n";

        String endString = separator + graphicsString + separator + infoString
                + separator + separator + versionString + separator + separator
                + separator + separator + separator + separator + "1\n" + separator;
        return endString;
    }
     // CHECK CODE GENERATOR
    public static void main(String[] argv) {
        SMLACodeGenerator codeGenerator = new SMLACodeGenerator();
        String ModelLibrary = "C:\\Users\\HAI\\OneDrive\\Desktop\\Model\\";
        String report = "Example";
        int numGroups = 3;
        int NoTicks = 10;
        String name = "Example_3_SCHELLING";
        String movingType = "SCHELLING";
        String perc_vacant_space = "60";
        List<String> perc_group = new ArrayList<>();
        perc_group.add("40");
        perc_group.add("35");
        perc_group.add("25");
        List<String> perc_similarity = new ArrayList<>();
        perc_similarity.add("50");
        perc_similarity.add("70");
        perc_similarity.add("75");

        codeGenerator.generateNLogoFile(ModelLibrary, report, numGroups, movingType,
                perc_vacant_space, perc_group, perc_similarity);
        App.main(argv);
        try {
            java.awt.EventQueue.invokeAndWait(
                    new Runnable() {
                        public void run() {
                            try {
                                App.app().open(ModelLibrary + name + ".nlogo", false);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
            App.app().command("setup");
            App.app().command("repeat " + NoTicks + " [ go ]");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}