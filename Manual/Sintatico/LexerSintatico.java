// Java 25.0.2

import java.util.ArrayList;
import java.util.List;

enum TokenType {
    PLUS,
    MINUS,
    MULT,
    DIV,
    POW,
    LPAREN,
    RPAREN,
    NUM,
    EOF
}

class TokenSintatico {
    private final TokenType type;
    private final String lexeme;

    public TokenSintatico(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    @Override
    public String toString() { // To be used by println
        return "(" + type + ", " + lexeme + ")";
    }
}

class LexicalException extends RuntimeException {
    public LexicalException(String message) {
        super(message);
    }
}

public class LexerSintatico { // Avoiding 'Scanner' as Java already has a class named 'Scanner'

    private final String input;
    private final List<TokenSintatico> tokens;

    private int position;

    public LexerSintatico(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.position = 0;
    }

    public List<TokenSintatico> tokenize() {
        while (position < input.length()) {
            char current = input.charAt(position);

            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            if (Character.isDigit(current)) {
                tokenizeNumber();
                continue;
            }

            switch (current) {
                case '+':
                    addToken(TokenType.PLUS);
                    break;

                case '-':
                    addToken(TokenType.MINUS);
                    break;

                case '*':
                    addToken(TokenType.MULT);
                    break;

                case '/':
                    addToken(TokenType.DIV);
                    break;

                case '^':
                    addToken(TokenType.POW);
                    break;

                case '(':
                    addToken(TokenType.LPAREN);
                    break;

                case ')':
                    addToken(TokenType.RPAREN);
                    break;

                default:
                    throw new LexicalException(
                        "Caractere invalido '" + current +
                        "' na posicao " + position
                    );
            }
        }

        tokens.add(new TokenSintatico(TokenType.EOF, "$"));

        return tokens;
    }

    private void tokenizeNumber() {
        int start = position;

        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }

        if (position < input.length() &&
            (input.charAt(position) == '.' || input.charAt(position) == ',')) {

            position++;

            if (position >= input.length() || !Character.isDigit(input.charAt(position))) {

                throw new LexicalException(
                    "Numero decimal invalido na posicao " + start
                );
            }

            while (position < input.length() && Character.isDigit(input.charAt(position))) {
                position++;
            }
        }

        String lexeme = input.substring(start, position);

        tokens.add(new TokenSintatico(TokenType.NUM, lexeme));
    }

    private void addToken(TokenType type) {
        String lexeme = String.valueOf(input.charAt(position));

        tokens.add(new TokenSintatico(type, lexeme));

        position++;
    }
}