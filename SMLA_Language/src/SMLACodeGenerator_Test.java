import org.nlogo.app.App;

import java.util.ArrayList;
import java.util.List;

public class SMLACodeGenerator_Test {
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
                    () -> {
                        try {
                            App.app().open(ModelLibrary + name + ".nlogo", false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
            App.app().command("setup");
            App.app().command("repeat " + NoTicks + " [ go ]");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
