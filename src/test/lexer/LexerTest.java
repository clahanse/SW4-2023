package test.lexer;

import main.lexer.Lexer;
import main.lexer.Token;
import main.lexer.TokenType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.lang.reflect.Method;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class LexerTest {
    
    private File testFile = new File("C:/Coding/Scanner/src/test/resources/test.txt");
    private Reader testReader;

    @BeforeEach
    public void setUp() {
        try {
            testReader = new BufferedReader(new FileReader(testFile));
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
            fail("test.txt file not found");
        }
    }


    @Test
    public void testConstructor() {
        Lexer lexer = new Lexer(testReader);
        assertNotNull(lexer);
    }


    @Test
    public void testConstructorWithValidInput() {
        String input = "SETUP SIMULATION WITH GROUPS WHERE TYPE IS 'human' AND SIZE IS 10;";
        Lexer lexer = new Lexer(new StringReader(input));
        assertNotNull(lexer);
    }
    
    @Test
    public void testConstructorWithEmptyInput() {
        String input = "";
        Lexer lexer = new Lexer(new StringReader(input));
        assertNotNull(lexer);
    }
    
    @Test
    public void testConstructorWithNullInput() {
        String input = null;
        assertThrows(NullPointerException.class, () -> new Lexer(new StringReader(input)));
    }

   
    // Test of the tokenizeInput method, of class Lexer.
    @Test
    public void testTokenizeInput() throws Exception {
        System.out.println("tokenizeInput");
        
        // Act
        Lexer instance = null;
        
        // Assert
        assertThrows(NullPointerException.class, () -> instance.tokenizeInput());
    }

    // Test of tokenizeInput method, of class Lexer - test invalid string.
    @Test
    public void testTokenizeInputThrowsLexerException() throws Exception {
        // Arrange
        System.out.println("tokenizeInput");
        String input = "\n\n TJING chang #21; HJallaballa\n\n";
        Lexer instance = new Lexer(new StringReader(input));
        
        // Act
        main.lexer.Lexer.LexerException thrown = assertThrows(main.lexer.Lexer.LexerException.class, () -> instance.tokenizeInput());
        
        // Assert
        assertTrue(thrown.getMessage().contains("Syntax error: #21"));
    }

    // Test of tokenizeInput method, of class Lexer.
    @Test
    public void testTokenizeInputReturnsListofTokens() throws Exception {
        // Arrange
        System.out.println("tokenizeInput");
        String input = "SETUP SIMULATION DETTE ER EN PROVE\n WITH GROUPS;";
        Lexer instance = new Lexer(new StringReader(input));
        
        // Act
        List<Token> tokens = instance.tokenizeInput();
        
        // Assert
        assertNotNull(tokens);
        assertTrue(tokens instanceof List<Token>);
    }

    // Test of tokenizeInput method, of class Lexer.
    @Test
    public void testTokenizeInputReturnsCorrectNumberOfTokens() throws Exception {
        // Arrange
        System.out.println("tokenizeInput");
        String input = "SETUP SIMULATION DETTE ER EN PROVE\n WITH GROUPS;\n RUN SIMULATION FOR 50 TICKS;";
        Lexer instance = new Lexer(new StringReader(input));
        
        // Act
        List<Token> tokens = instance.tokenizeInput();
        
        // Assert
        assertNotNull(tokens);
        assertTrue(tokens.size() == 13);
    }

    // Test of ParseToken method, of class Lexer.
    @Test
    public void testParseToken() throws LexerException {
        // Arrange
        String input = "";
        System.out.println("ParseToken");
        final StringReader testReader = new StringReader(input);
        Lexer instance = new Lexer(testReader);
        
        // Act
        String testString1 = "SETUP";
        String testString2 = "SIMULATION";
        int lineNumber1 = 1;
        int lineNumber2 = 2;
        Token result1 = instance.parseToken(testString1, lineNumber1);
        Token result2 = instance.parseToken(testString2, lineNumber2);
        String result1String = result1.getType();
        String result2String = result2.getType();

        
        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1String == TokenType.SETUP.getType());
        assertTrue(result2String == TokenType.SIMULATION.getType());
    }


    public class LexerException extends RuntimeException {
        public LexerException(String message) {
            super(message);
        }
    }
    
}
