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
        String nameFileNetLogo = name + "_" + noOfGroups + "_" + moving;
        File file = new File(saveLibrary + nameFileNetLogo + ".nlogo");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println(finalScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CREATE GLOBAL VARIABLES
    private String writeGlobals(int noOfGroups) {

        String twoBlanks = "  ";
        StringBuilder globals = new StringBuilder("globals [\n");
        globals.append(twoBlanks).append("percent_unhappy\n");

        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("percent_unhappy_group").append(i).append("\n");
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("percent_happy_group").append(i).append("\n");
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("perc_similarity_wanted_group").append(i).append("\n");
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("segregation_group").append(i).append("\n");
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("color").append(i).append("\n");
        }
        for (int i = 1; i <= noOfGroups; i++) {
            globals.append(twoBlanks).append("perc_group").append(i).append("\n");
        }
        globals.append(twoBlanks).append("perc_similarity_wanted\n");
        globals.append(twoBlanks).append("perc_vacant_space\n");
        globals.append(twoBlanks).append("move-type\n");
        globals.append("]\n");
        return globals.toString();
    }

    // CREATE AGENTS
    private String writeTurtles() {
        String twoBlanks = "  ";
        String turtlesOwn = "turtles-own [\n";
        String turtleCharacteristics = "happy\n" + twoBlanks + "similar-nearby\n" + twoBlanks + "total-nearby\n";
        return turtlesOwn + twoBlanks + turtleCharacteristics + "]\n";
    }

    // CREATE PATCHES
    private String writePatches(int noOfGroups) {
        String twoBlanks = "  ";
        String patchesOwn = "patches-own [\n";
        StringBuilder patchesHappy = new StringBuilder();
        for (int i = 1; i <= noOfGroups; i++) {
            patchesHappy.append(twoBlanks).append("happy").append(i).append("\n");
        }
        return patchesOwn + patchesHappy + "]\n";
    }

    // SETUP MODEL
    private String writeSetup(int noOfGroups, String perc_vacant,
                              List<String> perc_group, List<String> perc_similarity) {
        int[] colors = {45, 85, 25, 23, 44, 50, 75, 100, 106, 121};
        String twoBlanks = "  ";
        StringBuilder setupStart = new StringBuilder("to setup\n" + twoBlanks + "ca\n");
        String setupEnd = twoBlanks + "update\n" + twoBlanks + "reset-ticks\n" + "end\n";
        String setupNoOfPeople = twoBlanks
                + "let number-of-people int (world-width * world-height * (1 - perc_vacant_space / 100))\n";
        StringBuilder setPerc_similarity = new StringBuilder(twoBlanks + "set perc_vacant_space " + perc_vacant + "\n");
        StringBuilder setNumOfGroup = new StringBuilder();
        StringBuilder createGroup = new StringBuilder();
        String getLastPercent;
        StringBuilder setPerc_group = new StringBuilder();
        String setPerc_vacant = "";
        StringBuilder sum1 = new StringBuilder();
        StringBuilder sum2 = new StringBuilder();
        String lastGroup = "";
        for (int i = 1; i <= noOfGroups; i++) {
            if (i == noOfGroups) {
                sum1.append("perc_group").append(i);
                lastGroup = "perc_group" + i;
            } else {
                sum1.append("perc_group").append(i).append(" + ");
            }
            setPerc_group.append(twoBlanks).append("set perc_group").append(i).append(" ").
                    append(perc_group.get(i - 1)).append("\n");
            setPerc_similarity.append(twoBlanks).append("set perc_similarity_wanted_group").
                    append(i).append(" ").append(perc_similarity.get(i - 1)).append("\n");
        }
        for (int i = 1; i <= noOfGroups - 1; i++) {
            if (i == noOfGroups - 1) {
                sum2.append("perc_group").append(i);
            } else {
                sum2.append("perc_group").append(i).append(" + ");
            }
        }
        getLastPercent = twoBlanks + "if " + sum1 + " > 100\n" + twoBlanks + "   [" + "set " + lastGroup + " 100 - "
                + "(" + sum2 + ")" + "]\n";
        for (int i = 1; i <= noOfGroups; i++) {
            setupStart.append(twoBlanks).append("set color").append(i).append(" ").
                    append(colors[i - 1]).append(";\n");
            setNumOfGroup.append(twoBlanks).append("let number-of-group").append(i).
                    append(" int (number-of-people * perc_group").append(i).append(" / 100)\n");
            if (i == 1) {
                createGroup = new StringBuilder(twoBlanks + "ask n-of number-of-group" + i
                        + " patches [sprout 1 [set color color" + i + "]]\n");
            } else {
                createGroup.append(twoBlanks).append("ask n-of number-of-group").append(i).
                        append(" patches with [not any? turtles-here] [").
                        append("sprout 1 [set color color").append(i).append("]]\n");
            }
        }
        return setupStart + setPerc_vacant + setPerc_group + getLastPercent
                + setPerc_similarity + setupNoOfPeople + setNumOfGroup + createGroup + setupEnd;
    }

    // UPDATE VARIABLES AND AGENTS HAPPINESS
    private String writeToUpdate() {
        String twoBlanks = "  ";
        String updateStart = "to update\n";
        String updateEnd = "end\n";
        return updateStart + twoBlanks + "show-happiness\n" + twoBlanks
                + "update-globals\n" + twoBlanks + updateEnd;
    }

    // CALCULATION HAPPINESS
    private String writeComputeHappiness(int noOfGroups) {
        String twoBlanks = "  ";
        String computeHappinessStart = "to compute-happiness\n";
        String computeHappinessEnd = "end\n";
        String totalNearby = twoBlanks + "set total-nearby count turtles-on neighbors\n";
        StringBuilder setSimilarNearby = new StringBuilder(twoBlanks
                + "set similar-nearby count (turtles-on neighbors) with [color = [color] of myself]\n");
        for (int i = 1; i <= noOfGroups; i++) {
            setSimilarNearby.append(twoBlanks).append("if color = color").append(i).
                    append(" [set perc_similarity_wanted perc_similarity_wanted_group").append(i).append("]\n");
        }
        String ifElseTotalNearby = twoBlanks + "ifelse (total-nearby = 0) [\n"
                + twoBlanks + twoBlanks + "set happy true] [\n" + twoBlanks + twoBlanks
                + "set happy (similar-nearby / total-nearby >= perc_similarity_wanted / 100)]\n";
        String ifElseHappy = twoBlanks + "ifelse (happy = true) [\n" + twoBlanks + twoBlanks
                + "set shape \"face happy\"\n" + twoBlanks + "] [\n" + twoBlanks + twoBlanks
                + "set shape \"face sad\"\n" + twoBlanks + "]\n";
        return computeHappinessStart + totalNearby + setSimilarNearby
                + ifElseTotalNearby + ifElseHappy + computeHappinessEnd;
    }

    // CALCULATION GROUPS POTENTIAL HAPPINESS
    private String writeComputePotential(int noOfGroups) {
        String twoBlanks = "  ";
        String computePotentialStart = "to compute-potential-happiness\n"
                + twoBlanks + "let nearby count turtles-on neighbors\n";
        String computePotentialEnd = "end\n";
        StringBuilder nearby = new StringBuilder();
        StringBuilder setHappy = new StringBuilder(twoBlanks + " ifelse (nearby = 0) [\n");
        StringBuilder calcuHappy = new StringBuilder();
        for (int i = 1; i <= noOfGroups; i++) {
            nearby.append(twoBlanks).append("let nearby").append(i).
                    append(" count (turtle-set (turtles-on neighbors) with [color = color").append(i).append("])\n");
            setHappy.append(twoBlanks).append(" set happy").append(i).append(" true\n");
            calcuHappy.append(twoBlanks).append(" set happy").append(i).append(" (nearby").
                    append(i).append(" / nearby >= perc_similarity_wanted / 100)\n");
        }
        return computePotentialStart + nearby + setHappy + twoBlanks + " ] [\n"
                + calcuHappy + twoBlanks + " ]\n" + computePotentialEnd;
    }

    // SHOW HAPPINESS
    private String writeShowHappiness(String moving) {
        String twoBlanks = "  ";
        String showHappinessStart = "to show-happiness\n";
        String showHappinessEnd = "end\n";
        String showHappiness = twoBlanks + "ask turtles [compute-happiness]\n";
        String showPotential = twoBlanks + "ask patches [compute-potential-happiness]\n";
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
        String twoBlanks = "  ";
        String updateGlobalsStart = "to update-globals\n";
        String updateGlobalsEnd = "end\n";
        StringBuilder setPercentUnhappy = new StringBuilder(twoBlanks
                + "set percent_unhappy precision ((count turtles with [not happy] / count turtles) * 100) 2\n");
        StringBuilder setPercentHappy = new StringBuilder();
        StringBuilder setSegregation = new StringBuilder();
        for (int i = 1; i <= noOfGroups; i++) {
            setPercentHappy.append(twoBlanks).append("set percent_happy_group").append(i).
                    append(" precision ((count turtles with [happy and color = color").append(i).
                    append("] * 100) / (count turtles with [color = color").append(i).append("])) 2\n");
            setPercentUnhappy.append(twoBlanks).append("set percent_unhappy_group").append(i).
                    append(" precision ((count turtles with [not happy and color = color").append(i).
                    append("] * 100) / (count turtles with [color = color").append(i).append("])) 2\n");
            setSegregation.append(twoBlanks).append("set segregation_group").append(i).
                    append(" precision mean [count (turtles-on neighbors) with [color =\n").append("    color").
                    append(i).append("]] of turtles with [color = color").append(i).append("] 2\n");
        }
        return updateGlobalsStart + setPercentHappy + setPercentUnhappy
                + setSegregation + updateGlobalsEnd;
    }

    // RUN MODEL
    private String writeToGo(String moving) {
        String twoBlanks = "  ";
        String goStart = "to go\n";
        String goEnd = "end\n";
        String go = twoBlanks + "if not any? turtles with [not happy] [stop]\n";
        String returnGo = "";
        String choosingMoving = twoBlanks + "let unhappy-count count turtles with [not happy]\n" +
                twoBlanks + "let unhappy-ratio unhappy-count / count turtles\n" +
                twoBlanks + "if unhappy-ratio > 0.5 [set move-type \"schelling\"]\n" +
                twoBlanks + "if (unhappy-ratio > 0.2) [set move-type \"random2\"]\n" +
                twoBlanks + "if (unhappy-ratio > 0) [set move-type \"random1\"]\n";
        String moveUpdate = twoBlanks + "move-unhappy\n" + twoBlanks + "update\n" + twoBlanks + "tick\n";
        if (moving.equalsIgnoreCase("random")) {
            returnGo = goStart + go + moveUpdate + goEnd;
        } else if (moving.equalsIgnoreCase("schelling")) {
            returnGo = goStart + go + choosingMoving + moveUpdate + goEnd;
        }
        return returnGo;
    }

    // TYPE OF MOVING WHEN AGENTS AREN'T HAPPY
    private String writeMoveUnhappy(String moving) {
        String twoBlanks = "  ";
        String moveUnhappyStart = "to move-unhappy\n";
        String moveUnhappyEnd = "end\n";
        String returnMoving = "";
        String moveUnhappy1 = twoBlanks + "ask turtles with [not happy] [random-move1]\n";
        String moveUnhappy2 = twoBlanks
                + "if (move-type = \"random1\") [ask turtles with [not happy ] [ random-move1]]\n" + twoBlanks
                + "if (move-type = \"random2\") [ask turtles with [not happy ] [ without-interruption [random-move2]]]\n" + twoBlanks
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
        String twoBlanks = "  ";
        String randomMoveStart = "to random-move1\n";
        String randomMoveEnd = "end\n";
        String randomMove = twoBlanks + "setxy random-pxcor random-pycor\n"
                + twoBlanks + "if any? other turtles-here [random-move1]\n";
        return randomMoveStart + randomMove + randomMoveEnd;
    }

    // RANDOM MOVE RELATIVE TO PATCHES
    private String writeRandomMove2() {
        String twoBlanks = "  ";
        String randomMoveStart = "to random-move2\n";
        String randomMoveEnd = "end\n";
        String randomMove = twoBlanks + "ask one-of patches with [not any? turtles-here] [\n" +
                twoBlanks + " set xcor [pxcor] of myself\n" +
                twoBlanks + " set ycor [pycor] of myself]\n";
        return randomMoveStart + randomMove + randomMoveEnd;
    }

    // MOVE FOLLOWING SCHELLING CALCULATION
    private String writeSchellingMove(int noOfGroups) {
        String twoBlanks = "  ";
        String schellingMoveStart = "to schelling-move\n";
        String schellingMoveEnd = "end\n";
        StringBuilder schellingMove = new StringBuilder();
        for (int i = 1; i <= noOfGroups; i++) {
            schellingMove.append(twoBlanks).append("if (color = color").append(i).append(") [\n").
                    append(twoBlanks).append(" let target min-one-of (patches with [happy").append(i).
                    append(" and not any? turtles-here]) [abs (pxcor - [xcor] of myself) + abs (pycor - [ycor]\n").
                    append(twoBlanks).append("  of myself)]\n").append(twoBlanks).append(" if (target != nobody) [\n").
                    append(twoBlanks).append("  set xcor [pxcor] of target\n").append(twoBlanks).
                    append("  set ycor [pycor] of target]]\n");
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
}