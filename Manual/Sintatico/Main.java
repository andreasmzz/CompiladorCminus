// Java 25.0.2

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Path INPUT_FILE = Path.of("Sintatico", "input.txt");
    private static final Path OUTPUT_FILE = Path.of("Sintatico", "output.txt");

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===================================== COMPILADOR DE EXPRESSOES =====================================");

        System.out.println("\nOrigem das entradas:");
        System.out.println("1 - Arquivo input.txt");
        System.out.println("2 - Terminal");

        System.out.print("Opcao: ");
        String inputMode = scanner.nextLine();

        System.out.println("\nDestino das saidas:");
        System.out.println("1 - Arquivo output.txt");
        System.out.println("2 - Terminal");
        System.out.println("3 - Ambos");

        System.out.print("Opcao: ");
        String outputMode = scanner.nextLine();

        StringBuilder report = new StringBuilder();

        if (inputMode.equals("1")) { // Aqruivo input.txt

            try {
                List<String> expressions = Files.readAllLines(
                    INPUT_FILE,
                    StandardCharsets.UTF_8
                );

                for (String expression : expressions) {
                    report.append(processExpression(expression));
                }

            } catch (IOException e) {
                report.append("Erro ao ler input.txt: ")
                      .append(e.getMessage())
                      .append("\n");
            }

        } else if (inputMode.equals("2")) { // Terminal

            System.out.println("\nDigite as expressoes.");
            System.out.println("Digite s ou q para encerrar.\n");

            while (true) {

                System.out.print("Expressao: ");
                String expression = scanner.nextLine();

                if (expression.equalsIgnoreCase("s") || expression.equalsIgnoreCase("q")) {
                    break;
                }

                String result = processExpression(expression);

                report.append(result);

                if (outputMode.equals("1") ||
                    outputMode.equals("3")) {

                    System.out.println(result);
                }
            }

        } else {
            System.out.println("Origem invalida.");
            scanner.close();
            return;
        }

        if (inputMode.equals("1") && (outputMode.equals("2") || outputMode.equals("3"))) {

            System.out.println(report);
        }

        if (outputMode.equals("1") || outputMode.equals("3")) { // Arquivo output.txt or both

            try {
                Files.writeString(
                    OUTPUT_FILE,
                    report.toString(),
                    StandardCharsets.UTF_8
                );

                System.out.println("Relatorio salvo em output.txt.");

            } catch (IOException e) {
                System.out.println(
                    "Erro ao salvar output.txt: " + e.getMessage()
                );
            }
        }

        scanner.close();

        System.out.println("Processamento finalizado.");
    }

    private static String processExpression(String expression) {

        StringBuilder result = new StringBuilder();

        result.append("\n====================================================================================================\n");
        result.append("Entrada: ").append(expression).append("\n");
        result.append("====================================================================================================\n\n");

        try {

            Lexer lexer = new Lexer(expression);

            List<Token> tokens = lexer.tokenize();

            result.append("TOKENS RECONHECIDOS\n");

            for (Token token : tokens) {
                result.append(token).append("\n");
            }

            result.append("\n");

            Parser parser = new Parser();

            result.append(parser.parse(tokens));

        } catch (LexicalException e) {

            result.append("\nERRO LEXICO: ")
                  .append(e.getMessage())
                  .append("\n");

            result.append("EXPRESSAO REJEITADA\n");
        }

        return result.toString();
    }
}
