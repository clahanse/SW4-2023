package main.lexer;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.lang.NullPointerException;

//    Lexer: A class that takes an input stream and produces a stream of tokens. This class typically has the following methods:

public class Lexer {

    private List<Token> tokens;
    private Reader input;
    
    // Lexer(Reader input): A constructor that takes an input stream (e.g., a Reader) and initializes the lexer.
    public Lexer(Reader input) {
        this.input = input;
        tokens = new ArrayList<>();      
    }

    public List<Token> tokenizeInput() throws LexerException, IOException, NullPointerException {
        List<Token> tokens = new ArrayList<>();
        int lineNumber = 1;
        BufferedReader reader = new BufferedReader(input);
        String line;
        
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new IOException("Der er noget i vejen med inputtet", e);

        } catch (NullPointerException e) {
            throw new NullPointerException("Input er null");
        }

        while (line != null) {
            String[] tokenStrings = line.split(TokenType.WHITESPACE.getPattern() + "|" + TokenType.SEMICOLON.getPattern());
            System.out.println("TokenStrings: " + tokenStrings[0].toString());
            for (String tokenString : tokenStrings) {
                if (!tokenString.isEmpty()){
                    Token token = parseToken(tokenString, lineNumber);
                    tokens.add(token);
                }
            }
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new LexerException("Der er noget i vejen med inputtet", e);
            }
            lineNumber++;
        }
        return tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

     
    public Token parseToken(String tokenString, int lineNumber) throws LexerException {
        for (TokenType tokenType : TokenType.values()) {
            if (tokenString.matches(tokenType.getPattern())) {
                String type = tokenType.getType();
                String value = tokenString;
                String command = "";
                Token token = new Token(type, value, command, lineNumber);
                return token;
            }
        }
        throw new LexerException("Syntax error: " + tokenString, null);
    }

/* 
private Token parseToken(String tokenString, int lineNumber) throws LexerException {
    try {
        String command = "";
        TokenType tokenType = TokenType.valueOf(tokenString);
        return new Token(tokenType, tokenString, command, lineNumber);
    } catch (IllegalArgumentException e) {
        // Not a keyword
    }
    // Check for operators and separators
    switch (tokenString) {
        case "+":
            return new Token(TokenType.PLUS, tokenString, lineNumber);
        case "-":
            return new Token(TokenType.MINUS, tokenString, lineNumber);
        case "*":
            return new Token(TokenType.MULTIPLICATION, tokenString, lineNumber);
        case "/":
            return new Token(TokenType.DIVISION, tokenString, lineNumber);
        case "=":
            return new Token(TokenType.ASSIGN, tokenString, lineNumber);
        case ";":
            return new Token(TokenType.SEMICOLON, tokenString, lineNumber);
        case "(":
            return new Token(TokenType.OPENPAREN, tokenString, lineNumber);
        case ")":
            return new Token(TokenType.CLOSEPAREN, tokenString, lineNumber);
        case ",":
            return new Token(TokenType.COMMA, tokenString, lineNumber);
        case "\"":
        case "'":
            return new Token(TokenType.QUOTE, tokenString, lineNumber);
        case "\n":
            return new Token(TokenType.NEWLINE, tokenString, lineNumber);
        case "//":
            return new Token(TokenType.COMMENT, tokenString, lineNumber);
        default:
            // Check for literals
            if (tokenString.matches(TokenType.STRING.getPattern())) {
                return new Token(TokenType.STRING, tokenString, lineNumber);
            } else if (tokenString.matches(TokenType.INTEGER.getPattern())) {
                return new Token(TokenType.INTEGER, tokenString, lineNumber);
            } else if (tokenString.matches(TokenType.FLOAT.getPattern())) {
                return new Token(TokenType.FLOAT, tokenString, lineNumber);
            } else if (tokenString.matches(TokenType.IDENTIFIER.getPattern())) {
                return new Token(TokenType.IDENTIFIER, tokenString, lineNumber);
            } else {
                // Invalid token
                throw new LexerException("Syntax error: " + tokenString, lineNumber);
            }
    }
}
*/



    public String readUntilSemicolon(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            char ch = (char) c;
            sb.append(ch);
            if (ch == ';') {
                break;
            }
        }
        String result = sb.toString();
        // Do something with the result
        return result;
    }
    
    public class LexerException extends RuntimeException {
        String message = "Der skete en fejl med scanneren";
        public LexerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}





// getToken(): A method that reads the next token from the input stream and returns it.

// peekToken(): A method that returns the next token without consuming it.

// isKeyword(String str): A method that checks if a given string is a keyword in the language being parsed.

// isIdentifierStart(char c): A method that checks if a given character can be the start of an identifier.

// isIdentifierPart(char c): A method that checks if a given character can be part of an identifier.

// skipWhitespace(): A method that skips over whitespace in the input stream.

// skipComment(): A method that skips over a comment in the input stream.

// readNumber(): A method that reads a number from the input stream and returns a token representing it.

// readString(): A method that reads a string literal from the input stream and returns a token representing it.

// readIdentifier(): A method that reads an identifier from the input stream and returns a token representing it.

// readKeywordOrIdentifier(): A method that reads either a keyword or an identifier from the input stream and returns a token representing it.



/* 
    public List<Token> InitializeLexer(Reader input) {
    
        List<Token> tokens = new ArrayList<Token>();
        // int lineNumber = 1;

        try {
            String line = readUntilSemicolon(input);
            line.toCharArray();
            System.out.println(line);
        } catch (LexerException e) {
            System.out.println("Error reading file");
        }
        return tokens;
    }

    /* 
        while (input.hasNext()) {
            String line = input.nextLine();
            while (!line.isEmpty()) {
                Token token = getToken(line, lineNumber);
                tokens.add(token);
                line = line.substring(token.getValue().length());
            }
            lineNumber++;
        }

        */

