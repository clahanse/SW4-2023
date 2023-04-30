package main.lexer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenType {


        // Keywords
        SETUP("SETUP", "^SETUP"),
        SIMULATION("SIMULATION", "^SIMULATION"),
        WITH("WITH", "^WITH"),
        GROUPS("GROUPS", "^GROUPS"),
        TYPE("TYPE", "^TYPE"),
        SIZE("SIZE", "^SIZE"),
        VACANT("VACANT", "^VACANT"),
        USING("USING", "^USING"),
        RUN("RUN", "^RUN"),
        FOR("FOR", "^FOR"),
        TICKS("TICKS", "^TICKS"),
        REPORT("REPORT", "^REPORT"),

        // Operators
        PLUS("PLUS", "\\+"),
        MINUS("MINUS", "-"),
        MULTIPLICATION("MULTIPLICATION", "\\*"),
        DIVISION("DIVISION", "/"),
        ASSIGN("ASSIGN", "="),

        // Separators
        WHITESPACE("WHITESPACE", "\\s+"),
        SEMICOLON("SEMICOLON", ";"),
        OPENPAREN("OPENPAREN", "\\("),
        CLOSEPAREN("CLOSEPAREN", "\\)"),
        COMMA("COMMA", ","),
        QUOTE("QUOTE", "\"|'"),
        NEWLINE("NEWLINE", "\\n"),
        COMMENT("COMMENT", "//"),

        // Literals
        STRING("STRING", "\"[^\"]*\""),
        INTEGER("INTEGER", "^[0-9]+"),
        FLOAT("FLOAT", "^[0-9]+\\.[0-9]+"),
        IDENTIFIER("IDENTIFIER", "^[a-zA-Z_][a-zA-Z0-9_]*$"),

        

        // add more token types as needed
        ;
    
        private final String type;
        private final String pattern;
    
        TokenType(String type, String pattern) {
            this.type = type;
            this.pattern = pattern;
        }
    
        public String getType() {
            return type;
        }
    
        public String getPattern() {
            return pattern;
        }
}
