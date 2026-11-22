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


//        for (File inputFile : files) {
//            // abre arquivo de entrada
//            FileReader fr = new FileReader(inputFile);
//            Scanner scanner = new Scanner(fr);
//
//            // cria arquivo de saída com o mesmo nome
//            File outputFile = new File(outputDir, inputFile.getName());
//            PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
//
//            // escreve o conteúdo original do arquivo
//            BufferedReader br = new BufferedReader(new FileReader(inputFile));
//            String line;
//            while ((line = br.readLine()) != null) {
//                writer.println(line);
//            }
//            writer.println("\n--- Tokens ---");
//            try{
//                // análise léxica
//                Symbol s = scanner.next_token();
//                while (s.sym != Tokens.EOF) {   // verifique se sym.EOF está correto
//                    writer.printf("<%d, %s, linha: %d>%n", s.sym, s.value, s.left);
//                    s = scanner.next_token();
//                }
//            }catch(Exception e){
//                writer.println(e.getMessage());
//            }
//            br.close();
//            writer.close();
//            fr.close();
//
//            System.out.println("Arquivo processado: " + inputFile.getName());
//        }
    }
}
