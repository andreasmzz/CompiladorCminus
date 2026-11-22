// Java 25.0.2

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java_cup.Lexer;

import java.util.ArrayList;

public class LexerReport {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java LexerReport <arquivo>");
            return;
        }

        try {
            String source = new String(
                    Files.readAllBytes(Paths.get(args[0])),
                    StandardCharsets.UTF_8
            );

            LexerLexico lexer = new LexerLexico(new StringReader(source));
            List<LexerLexico.Token> tokens = new ArrayList<LexerLexico.Token>();

            LexerLexico.Token token;

            while (true) {
                token = lexer.nextToken();

                if (token.type == TokensLexico.EOF) {
                    break;
                }

                tokens.add(token);
            }

            System.out.println(source);
            System.out.println("--- Tokens ---");

            for (LexerLexico.Token currentToken : tokens) {
                System.out.println(currentToken);
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }
}
