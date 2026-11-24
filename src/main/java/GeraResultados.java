/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.*;
import java_cup.runtime.Symbol;
/**
 *
 * @author marco
 */
public class GeraResultados {
    public static void main(String[] args) throws IOException {
        File inputDir = new File("entrada");   // pasta de entrada
        File outputDir = new File("saida");    // pasta de saída

        // cria a pasta de saída se não existir
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // lista todos os arquivos da pasta de entrada
        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null) {
            System.out.println("Nenhum arquivo encontrado na pasta de entrada.");
            return;
        }
        
        for (File inputFile : files) {

            // Cria arquivo de saída com o mesmo nome
            File outputFile = new File(outputDir, inputFile.getName());

            try (PrintWriter writer = new PrintWriter(
                    new FileWriter(outputFile))) {

                // Escreve o conteúdo original do arquivo
                try (BufferedReader br = new BufferedReader(
                        new FileReader(inputFile))) {

                    String line;

                    while ((line = br.readLine()) != null) {
                        writer.println(line);
                    }
                }

                // -----------------------------
                // ANÁLISE LÉXICA
                // -----------------------------

                writer.println("\n--- Tokens ---");

                try (FileReader fr = new FileReader(inputFile)) {

                    Scanner scanner = new Scanner(fr);

                    Symbol s = scanner.next_token();

                    while (s.sym != Tokens.EOF) {

                        writer.printf(
                            "<%d, %s, linha: %d>%n",
                            s.sym,
                            s.value,
                            s.left
                        );

                        s = scanner.next_token();
                    }

                } catch (Exception e) {
                    writer.println("Erro léxico: " + e.getMessage());
                }

                // -----------------------------
                // ANÁLISE SINTÁTICA
                // -----------------------------

                writer.println("\n--- Análise Sintática ---");

                try (FileReader fr = new FileReader(inputFile)) {

                    Scanner scannerSintatico = new Scanner(fr);

                    Parser parser = new Parser(scannerSintatico, writer);

                    // Executa a análise sintática
                    Symbol resultadoParser = parser.parse();

                    if (!parser.houveErroSintatico()) {

                        writer.println("Código sintaticamente válido.");

                        // Recupera a árvore sintática
                        AstNode arvore = (AstNode) resultadoParser.value;

                        writer.println("\n--- Árvore Sintática ---");
                        writer.println(arvore);
                        
                        // -----------------------------
                        // ANÁLISE SEMÂNTICA
                        // -----------------------------

                        writer.println("\n--- Análise Semântica ---");

                        SemanticAnalyzer semantico = new SemanticAnalyzer();

                        // Executa a análise semântica sobre a AST
                        semantico.analisar(arvore);

                        if (semantico.houveErroSemantico()) {

                            writer.println(
                                "Código semanticamente inválido."
                            );

                            writer.println("\nErros semânticos:");

                            for (String erro : semantico.getErros()) {
                                writer.println("- " + erro);
                            }

                        } else {

                            writer.println(
                                "Código semanticamente válido."
                            );
                        }

                        // -----------------------------
                        // TABELA DE SÍMBOLOS
                        // -----------------------------

                        writer.println("\n--- Tabela de Símbolos ---");

                        for (VariableSymbol simbolo :
                                semantico.getTabela().getSimbolos()) {

                            writer.println(simbolo);
                        }

                    } else {

                        writer.println(
                            "Código sintaticamente inválido."
                        );
                    }

                }catch (Exception e) {

                    writer.println(
                        "Resultado: Código sintaticamente inválido."
                    );

                    writer.println(
                        "Falha na análise: " + e.getMessage()
                    );
                }

                writer.flush();
            }

            System.out.println(
                "Arquivo processado: " + inputFile.getName()
            );
            
        }
        
        
        
    }
}
