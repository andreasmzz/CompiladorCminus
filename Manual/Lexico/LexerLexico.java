// Java 25.0.2

import java.io.IOException;
import java.io.Reader;

public class LexerLexico {

    public static class Token {
        public final int type;
        public final String lexeme;
        public final int line;

        public Token(int type, String lexeme, int line) {
            this.type = type;
            this.lexeme = lexeme;
            this.line = line;
        }

        @Override
        public String toString() {
            return "<" + type + ", " + lexeme + ", linha: " + line + ">";
        }
    }

    private final Reader reader;
    private boolean finished;
    private int currentChar;
    private int nextChar;
    private int line;

    public LexerLexico(Reader reader) throws IOException {
        this.reader = reader;
        this.finished = false;
        this.line = 1;
        this.currentChar = reader.read();
        this.nextChar = reader.read();
    }

    private void advance() throws IOException {
        if (currentChar == '\n') {
            line++;
        }
        currentChar = nextChar;
        nextChar = reader.read();
    }

    private boolean isLetter(int c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    private Token skipIgnored() throws IOException {

        while (true) {

            // White space
            if (currentChar == ' ' || currentChar == '\t' || 
                currentChar == '\r' || currentChar == '\n') {
                advance();
            }

            // Block comments
            else if (currentChar == '/' && nextChar == '*') {
                int commentLine = line;
                boolean closed = false;

                advance();
                advance();

                while (currentChar != -1) {
                    if (currentChar == '*' && nextChar == '/') {
                        advance();
                        advance();
                        closed = true;
                        break;
                    }

                    advance();
                }

                if (!closed) {
                    finished = true;
                    return new Token(TokensLexico.ERROR, "Comentário não terminado", commentLine);
                }
            }

            else {
                return null;
            }
        }
    }


    private Token getSymbolToken() throws IOException {
        int tokenLine = line;
        char symbol = (char) currentChar;

        switch (symbol) {
            case '+':
                advance();
                return new Token(TokensLexico.PLUS, "+", tokenLine);

            case '-':
                advance();
                return new Token(TokensLexico.MINUS, "-", tokenLine);

            case '*':
                advance();
                return new Token(TokensLexico.TIMES, "*", tokenLine);

            case '/':
                advance();
                return new Token(TokensLexico.DIVIDE, "/", tokenLine);

            case '<':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokensLexico.LE, "<=", tokenLine);
                }
                return new Token(TokensLexico.LT, "<", tokenLine);

            case '>':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokensLexico.GE, ">=", tokenLine);
                }
                return new Token(TokensLexico.GT, ">", tokenLine);

            case '=':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokensLexico.EQ, "==", tokenLine);
                }
                return new Token(TokensLexico.ASSIGN, "=", tokenLine);

            case '!': // no ! alone is accepted
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokensLexico.NE, "!=", tokenLine);
                }
                return new Token(TokensLexico.ERROR, "Caractere inválido: !", tokenLine);

            case ';':
                advance();
                return new Token(TokensLexico.SC, ";", tokenLine);

            case ',':
                advance();
                return new Token(TokensLexico.CM, ",", tokenLine);

            case '(':
                advance();
                return new Token(TokensLexico.LP, "(", tokenLine);

            case ')':
                advance();
                return new Token(TokensLexico.RP, ")", tokenLine);

            case '[':
                advance();
                return new Token(TokensLexico.LBK, "[", tokenLine);

            case ']':
                advance();
                return new Token(TokensLexico.RBK, "]", tokenLine);

            case '{':
                advance();
                return new Token(TokensLexico.LBR, "{", tokenLine);

            case '}':
                advance();
                return new Token(TokensLexico.RBR, "}", tokenLine);

            default: // character not in grammar
                return null;
        }
    }

    private int getKeywordType(String lexeme) { // get if a string is id or keyword
        switch (lexeme) {
            case "else":
                return TokensLexico.ELSE;
            case "if":
                return TokensLexico.IF;
            case "int":
                return TokensLexico.INT;
            case "return":
                return TokensLexico.RETURN;
            case "while":
                return TokensLexico.WHILE;
            default:
                return TokensLexico.IDENTIFIER;
        }
    }

    public Token nextToken() throws IOException {

        if (finished) {
            return new Token(TokensLexico.EOF, "EOF", line);
        }

        Token ignoredError = skipIgnored(); // before any other so /* are not captured as DIVIDE and TIMES

        if (ignoredError != null) {
            return ignoredError;
        }

        if (currentChar == -1) {
            return new Token(0, "EOF", line);
        }

        int tokenLine = line;

        // Identifiers and keywords
        if (isLetter(currentChar)) {
            StringBuilder lexeme = new StringBuilder();
            while (isLetter(currentChar)) {
                lexeme.append((char) currentChar);
                advance();
            }
            String text = lexeme.toString();
            int type = getKeywordType(text);
            return new Token(type, text, tokenLine);
        }

        // Integers
        if (isDigit(currentChar)) {
            StringBuilder lexeme = new StringBuilder();
            while (isDigit(currentChar)) {
                lexeme.append((char) currentChar);
                advance();
            }
            return new Token(TokensLexico.INTCON, lexeme.toString(), tokenLine);
        }
        
        // Operators and delimiters
        Token symbolToken = getSymbolToken();
        if (symbolToken != null) {
            return symbolToken;
        }

        // Invalid character
        char invalidChar = (char) currentChar;
        advance();

        return new Token(TokensLexico.ERROR, "Caractere inválido: " + invalidChar, tokenLine);
    }
}

