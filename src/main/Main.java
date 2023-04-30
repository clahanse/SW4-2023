package main;
import main.lexer.Lexer;
import main.lexer.Token;
import main.lexer.Lexer.LexerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;



public class Main {
    public static void main(String[] args) throws FileNotFoundException {

/* 
        File myObj = new File("C:/Coding/SMLA/SW4-2023/SMLA_Language/src/test.smla");
        FileReader input = new FileReader(myObj);
      

        System.out.println(input);*/
        String input = "SETUP SIMULATION DETTE ER EN PROVE\n WITH GROUPS";
        //Lexer lexer = new Lexer(input);        
        Lexer lexer = new Lexer(new StringReader(input));
        List<Token> tokens;
        try {
            tokens = lexer.tokenizeInput();
            int i = 1;
            for (Token token : tokens) {
                System.out.println("Token nummer: " + i + " " + token.getType() + " " + token.getValue() + " " + token.getCommand() + " " + token.getLineNumber());
                i++;
            }
        } catch (LexerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Lexer done for now!");
        }
    }
}

