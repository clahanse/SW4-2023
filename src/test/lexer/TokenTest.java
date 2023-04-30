package test.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import main.lexer.Token;

public class TokenTest {

    @Test
    public void testConstructorAndGetters() {
        Token token = new Token("IDENTIFIER", "foo", "PRINT", 1);
        Assertions.assertEquals("IDENTIFIER", token.getType());
        Assertions.assertEquals("foo", token.getValue());
        Assertions.assertEquals("PRINT", token.getCommand());
        Assertions.assertEquals(1, token.getLineNumber());
    }
}
/* 
    @Test
    public void testTokenTypeValues() {
        Token token = new Token("NEWLINE", "\\n", "TK_NEWLINE", 1);
        assertEquals(TokenType.TK_NEWLINE, "TK_NEWLINE");
/*         assertEquals(TokenType.TK_MINUS.toString(), "TK_MINUS");
        assertEquals(TokenType.TK_PLUS.toString(), "TK_PLUS");
        assertEquals(TokenType.TK_MUL.toString(), "TK_MUL");
        assertEquals(TokenType.TK_DIV.toString(), "TK_DIV");
        assertEquals(TokenType.TK_ASSIGN.toString(), "TK_ASSIGN");
        assertEquals(TokenType.TK_OPEN.toString(), "TK_OPEN");
        assertEquals(TokenType.TK_CLOSE.toString(), "TK_CLOSE");
        assertEquals(TokenType.TK_SEMI.toString(), "TK_SEMI");
        assertEquals(TokenType.TK_COMMA.toString(), "TK_COMMA");
        assertEquals(TokenType.TK_QUOTE.toString(), "TK_QUOTE");
        assertEquals(TokenType.TK_KEY_SETUP.toString(), "TK_KEY_SETUP");
        assertEquals(TokenType.TK_KEY_WITH.toString(), "TK_KEY_WITH");
        assertEquals(TokenType.TK_KEY_GROUPS.toString(), "TK_KEY_GROUPS");
        assertEquals(TokenType.TK_KEY_TYPE.toString(), "TK_KEY_TYPE");
        assertEquals(TokenType.TK_KEY_SIZE.toString(), "TK_KEY_SIZE");
        assertEquals(TokenType.TK_KEY_VACANT.toString(), "TK_KEY_VACANT");
        assertEquals(TokenType.TK_KEY_USING.toString(), "TK_KEY_USING");
        assertEquals(TokenType.TK_KEY_RUN.toString(), "TK_KEY_RUN");
        assertEquals(TokenType.TK_KEY_FOR.toString(), "TK_KEY_FOR");
        assertEquals(TokenType.TK_KEY_TICKS.toString(), "TK_KEY_TICKS");
        assertEquals(TokenType.TK_KEY_REPORT.toString(), "TK_KEY_REPORT");
        assertEquals(TokenType.TK_KEY_SCHELLING.toString(), "TK_KEY_SCHELLING");
        assertEquals(TokenType.STRING.toString(), "STRING");
        assertEquals(TokenType.INTEGER.toString(), "INTEGER");
        assertEquals(TokenType.FLOAT.toString(), "FLOAT");
        assertEquals(TokenType.IDENTIFIER.toString(), "IDENTIFIER");
        assertEquals(TokenType.COMMENT.toString(), "COMMENT")
        */    