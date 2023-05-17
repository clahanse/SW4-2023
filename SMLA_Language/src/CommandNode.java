import java.util.ArrayList;
import java.util.List;

public abstract class CommandNode {
   // Setup simulation node
    static class SimulationCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private int nInteger;
        private List<String> nFloats;
        private List<CommandNode> children = new ArrayList<>();

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public void setNInteger(int nInteger) {
            this.nInteger = nInteger;
        }

        public String getIdentifier() {
            return identifier;
        }

        public int getNInteger() {
            return nInteger;
        }

        public void setNFloats(List<String> nFloats) {
            this.nFloats = nFloats;
        }

        public List<String> getNFloats() {
            return nFloats;
        }

        @Override
        public void execute() {
        }

        public List<CommandNode> getChildren() {
            return children;
        }
    }

    // Variable node
    static class VariableCommandNode extends CommandNode {
        private String identifier;
        private int nInteger;
        private List<String> identifiers;
        private List<CommandNode> children;

        public List<String> getIdentifiers() {
            return identifiers;
        }

        public void setIdentifiers(List<String> identifiers) {
            this.identifiers = identifiers;
        }

        public VariableCommandNode() {
            children = new ArrayList<>();
        }

        @Override
        public void execute() {
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    // Pref node
    static class PrefCommandNode extends CommandNode {
        private String keyword;
        private String identifier;

        private List<String> nFloats;

        public void setNFloats(List<String> nFloats) {
            this.nFloats = nFloats;
        }

        public List<String> getnFloats() {
            return nFloats;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        @Override
        public void execute() {
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    // Vacant node
    static class VacantCommandNode extends CommandNode {
        private String keyword;
        private String identifier;

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        @Override
        public void execute() {
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    // Using node
    static class UsingCommandNode extends CommandNode {
        private String keyword;
        private String identifier;

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        @Override
        public void execute() {
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    // Run node
    static class RunCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private int nInteger;

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public int getNInteger() {
            return nInteger;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setNInteger(int nInteger) {
            this.nInteger = nInteger;
        }

        @Override
        public void execute() {
            // execute run command
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    // Report node
    static class ReportCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private List<String> identifiers;
        private List<CommandNode> children = new ArrayList<>();

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setIdentifiers(List<String> identifiers) {
            this.identifiers = identifiers;
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void execute() {
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    public abstract void execute();

    public abstract List<CommandNode> getChildren();

}
