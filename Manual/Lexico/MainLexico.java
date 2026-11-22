// Java 25.0.2

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MainLexico {

    public static void main(String[] args) {

        Path inputDirectory = Paths.get("Manual", "Lexico", "entrada");
        Path outputDirectory = Paths.get("Manual", "Lexico", "saida");
        Path referenceDirectory = Paths.get("saida");

        boolean inLexico = false;

        // Also allow execution with Lexico as the working directory but there will be no comparison
        if (!Files.isDirectory(inputDirectory)) {
            inLexico = true;
            inputDirectory = Paths.get("entrada");
            outputDirectory = Paths.get("saida");
        }

        List<Path> inputFiles = new ArrayList<Path>();

        try {

            Files.createDirectories(outputDirectory);

            try (Stream<Path> files = Files.list(inputDirectory)) {
                files.filter(Files::isRegularFile)
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .forEach(inputFiles::add);
            }

            if (inputFiles.isEmpty()) {
                System.out.println("Nenhum arquivo encontrado em: " + inputDirectory.toAbsolutePath());
                return;
            }

            for (Path inputFile : inputFiles) {
                processFile(inputFile, outputDirectory);
            }

            System.out.println("Processamento concluído: " + inputFiles.size() + " arquivo(s).");

        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }

        if (!inLexico) {

            int matches = 0;
            int differences = 0;

            for (Path inputFile : inputFiles) {
                Path manualFile = outputDirectory.resolve(inputFile.getFileName());
                Path referenceFile = referenceDirectory.resolve(inputFile.getFileName());

                if (!Files.isRegularFile(referenceFile)) {
                    System.out.println(inputFile.getFileName()
                                        + ": arquivo de referência não encontrado.");
                    continue;
                }

                try {
                    if (FileComparator.compare(manualFile, referenceFile)) {
                        matches++;
                    } else {
                        differences++;
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao comparar: " + inputFile.getFileName() + ": " +
                                        e.getMessage());
                }
            }

            System.out.println("\n========== COMPARISON SUMMARY ==========");
            System.out.println("Matching files: " + matches);
            System.out.println("Files with differences: " + differences);
        } else {
            System.out.println("\nRunning with Lexico as working directory, no reference files to compare.");
        }
    }

    private static void processFile(Path inputFile, Path outputDirectory) throws IOException {

        String source = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);

        LexerLexico lexer = new LexerLexico(new StringReader(source));
        StringBuilder report = new StringBuilder();

        report.append(source);
        report.append("\n");
        report.append("--- Tokens ---\n");

        LexerLexico.Token token;

        while (true) {

            token = lexer.nextToken();

            if (token.type == Tokens.EOF) {
                break;
            }

            report.append(token);
            report.append("\n");
        }

        Path outputFile = outputDirectory.resolve(inputFile.getFileName());

        Files.write(outputFile, report.toString().getBytes(StandardCharsets.UTF_8));

        System.out.println("Gerado: " + outputFile);
    }
}
