import java.util.ArrayList;
import java.util.List;

public abstract class CommandNode {
    static class SimulationCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private int nInteger;
        private List<String> nIntegers;
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

        public void setNIntegers(List<String> nIntegers) {
            this.nIntegers = nIntegers;
        }

        public List<String> getNIntegers() {
            return nIntegers;
        }


        @Override
        public void execute() {
            // execute simulation command
        }

        public void addChild(CommandNode child) {
            children.add(child);
        }

        public List<CommandNode> getChildren() {
            return children;
        }

    }

    static class VariableCommandNode extends CommandNode {
        private String identifier;
        private int nInteger;
        private List<String> identifiers;
        private List<CommandNode> children;

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public int getNInteger() {
            return nInteger;
        }

        public String getIdentifier() {
            return identifier;
        }

        public List<String> getIdentifiers() {
            return identifiers;
        }

        public void setIdentifiers(List<String> identifiers) {
            this.identifiers = identifiers;
        }

        public void setNInteger(int nInteger) {
            this.nInteger = nInteger;
        }

        public VariableCommandNode() {
            children = new ArrayList<>();
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

    static class PrefCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private int nInteger;

        private List<String> nIntegers;

        public void setNIntegers(List<String> nIntegers) {
            this.nIntegers = nIntegers;
        }

        public List<String> getNIntegers() {
            return nIntegers;
        }

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
            // execute report command
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    static class VacantCommandNode extends CommandNode {
        private String keyword;
        private String identifier;
        private int nInteger;

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
            // execute run command
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    static class UsingCommandNode extends CommandNode {
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

        public String getIdentifier() {
            return identifier;
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

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public void execute() {
            // execute report command
        }

        @Override
        public List<CommandNode> getChildren() {
            return new ArrayList<>();
        }
    }

    public abstract void execute();

    public abstract List<CommandNode> getChildren();

}
