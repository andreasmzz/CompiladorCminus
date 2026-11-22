// Java 25.0.2

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {

    private final Map<String, Map<TokenType, String>> table;
    private final Set<String> nonTerminals;

    public Parser() {
        this.table = new HashMap<>();
        this.nonTerminals = new HashSet<>();

        initializeNonTerminals();
        initializeTable();
    }

    private void initializeNonTerminals() {
        nonTerminals.add("E");
        nonTerminals.add("E'");
        nonTerminals.add("T");
        nonTerminals.add("T'");
        nonTerminals.add("F");
        nonTerminals.add("F'");
        nonTerminals.add("P");
    }

    private void addProduction(String nonTerminal, TokenType lookahead, String production) {
        table.computeIfAbsent(
            nonTerminal,
            key -> new EnumMap<>(TokenType.class)
        ).put(lookahead, production);
    }

    private void initializeTable() {

        /*
        | NT |  +  |  -  |  *  |  /  |  ^  |  (  |  )  | num |  $  |
        | E  | err | err | err | err | err |  1  | err |  1  | err |
        | E' |  2  |  3  | err | err | err | err |  4  | err |  4  |
        | T  | err | err | err | err | err |  5  | err |  5  | err |
        | T' |  8  |  8  |  6  |  7  | err | err |  8  | err |  8  |
        | F  | err | err | err | err | err |  9  | err |  9  | err |
        | F' | 11  | 11  | 11  | 11  | 10  | err | 11  | err | 11  |
        | P  | err | err | err | err | err | 12  | err | 13  | err |
        */

        // E -> T E'
        addProduction("E", TokenType.LPAREN, "T E'");
        addProduction("E", TokenType.NUM, "T E'");

        // E' -> + T E' | - T E' | epsilon
        addProduction("E'", TokenType.PLUS, "PLUS T E'");
        addProduction("E'", TokenType.MINUS, "MINUS T E'");
        addProduction("E'", TokenType.RPAREN, "");
        addProduction("E'", TokenType.EOF, "");

        // T -> F T'
        addProduction("T", TokenType.LPAREN, "F T'");
        addProduction("T", TokenType.NUM, "F T'");

        // T' -> * F T' | / F T' | epsilon
        addProduction("T'", TokenType.MULT, "MULT F T'");
        addProduction("T'", TokenType.DIV, "DIV F T'");
        addProduction("T'", TokenType.PLUS, "");
        addProduction("T'", TokenType.MINUS, "");
        addProduction("T'", TokenType.RPAREN, "");
        addProduction("T'", TokenType.EOF, "");

        // F -> P F'
        addProduction("F", TokenType.LPAREN, "P F'");
        addProduction("F", TokenType.NUM, "P F'");

        // F' -> ^ F | epsilon
        addProduction("F'", TokenType.POW, "POW F");
        addProduction("F'", TokenType.PLUS, "");
        addProduction("F'", TokenType.MINUS, "");
        addProduction("F'", TokenType.MULT, "");
        addProduction("F'", TokenType.DIV, "");
        addProduction("F'", TokenType.RPAREN, "");
        addProduction("F'", TokenType.EOF, "");

        // P -> ( E ) | num
        addProduction("P", TokenType.LPAREN, "LPAREN E RPAREN");
        addProduction("P", TokenType.NUM, "NUM");
    }

    public String parse(List<Token> tokens) {

        StringBuilder result = new StringBuilder();

        Deque<String> stack = new ArrayDeque<>();

        stack.push("$");
        stack.push("E");

        int position = 0;
        boolean accepted = false;

        List<String[]> rows = new ArrayList<>(); // for exibition

        while (!stack.isEmpty()) {

            Token lookahead = tokens.get(position);
            String top = stack.peek();

            String stackState = getStackState(stack);
            String inputState = getInputState(tokens, position);

            // Acceptance condition
            if (top.equals("$") && lookahead.getType() == TokenType.EOF) {

                stack.pop();

                rows.add(new String[] {
                    stackState,
                    inputState,
                    "ACEITA"
                });

                accepted = true;
                break;
            }

            // Top of stack is a terminal
            if (!nonTerminals.contains(top)) {

                if (top.equals(lookahead.getType().name())) {

                    stack.pop();
                    position++;

                    rows.add(new String[] {
                        stackState,
                        inputState,
                        "Consome " + lookahead.getLexeme()
                    });

                } else {

                    rows.add(new String[] {
                        stackState,
                        inputState,
                        "ERRO: esperado " + top
                    });

                    break;
                }

                continue;
            }

            // Top of stack is a non-terminal
            Map<TokenType, String> row = table.get(top); // all produtions derived from this NT

            String production = null;

            if (row != null) { // table line for top NT is not empty -> at least one production
                production = row.get(lookahead.getType());
            }

            if (production == null) { // all row or table[top, TokenType] is empty

                rows.add(new String[] {
                    stackState,
                    inputState,
                    "ERRO: tabela[" + top + ", " +
                    lookahead.getType() + "]"
                });

                break;
            }

            // Apply production
            stack.pop(); // remove current NT

            if (!production.isEmpty()) {

                String[] symbols = production.split(" ");

                for (int i = symbols.length - 1; i >= 0; i--) { // insert reverted
                    stack.push(symbols[i]); // NT or ""
                }
            }

            rows.add(new String[] {
                stackState,
                inputState,
                top + " -> " + formatProduction(production)
            });
        }

        result.append("PASSOS DA ANALISE SINTATICA\n\n");

        result.append(formatTable(
            new String[] {"Pilha", "Entrada", "Ação"},
            rows
        ));

        result.append("\nResultado: ");

        if (accepted) {
            result.append("EXPRESSÃO ACEITA\n");
        } else {
            result.append("EXPRESSÃO REJEITADA\n");
        }

        return result.toString();
    }

    private String getStackState(Deque<String> stack) {

        StringBuilder state = new StringBuilder();

        Iterator<String> iterator = stack.descendingIterator();

        while (iterator.hasNext()) {
            state.append(iterator.next()).append(" ");
        }

        return state.toString().trim();
    }

    private String getInputState(List<Token> tokens, int position) {

        StringBuilder state = new StringBuilder();

        for (int i = position; i < tokens.size(); i++) {

            state.append(tokens.get(i).getLexeme());

            if (i < tokens.size() - 1) {
                state.append(" ");
            }
        }

        return state.toString();
    }

    private String formatProduction(String production) {

        if (production.isEmpty()) {
            return "e";
        }

        return production
            .replace("PLUS", "+")
            .replace("MINUS", "-")
            .replace("MULT", "*")
            .replace("DIV", "/")
            .replace("POW", "^")
            .replace("LPAREN", "(")
            .replace("RPAREN", ")")
            .replace("NUM", "num");
    }

    private String formatTable(String[] headers, List<String[]> rows) {

        int[] widths = new int[headers.length];

        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        StringBuilder output = new StringBuilder();

        appendSeparator(output, widths);
        appendRow(output, headers, widths);
        appendSeparator(output, widths);

        for (String[] row : rows) {
            appendRow(output, row, widths);
        }

        appendSeparator(output, widths);

        return output.toString();
    }

    private void appendRow(StringBuilder output, String[] values, int[] widths) {

        output.append("|");

        for (int i = 0; i < values.length; i++) {
            output.append(" ")
                .append(String.format("%-" + widths[i] + "s", values[i]))
                .append(" |");
        }

        output.append("\n");
    }

    private void appendSeparator(StringBuilder output, int[] widths) {

        output.append("+");

        for (int width : widths) {
            output.append("-".repeat(width + 2)).append("+");
        }

        output.append("\n");
    }
}
