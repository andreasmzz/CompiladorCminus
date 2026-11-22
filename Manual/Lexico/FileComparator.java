// Java 25.0.2

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileComparator {

    public static boolean compare(Path manualFile, Path referenceFile) throws IOException {

        List<String> manualTokens = extractTokens(manualFile);
        List<String> referenceTokens = extractTokens(referenceFile);

        if (manualTokens.equals(referenceTokens)) {
            System.out.println(manualFile.getFileName() + ": OK");
            return true;
        }

        System.out.println(manualFile.getFileName() + ": DIVERGÊNCIA");

        int maxSize = Math.max(manualTokens.size(), referenceTokens.size());

        for (int i = 0; i < maxSize; i++) {

            String manual = i < manualTokens.size() ? manualTokens.get(i) : "<ausente>";

            String reference = i < referenceTokens.size() ? referenceTokens.get(i) : "<ausente>";

            if (!manual.equals(reference)) {
                System.out.println("  Token " + (i + 1));
                System.out.println("    Manual: " + manual);
                System.out.println("    JFlex:  " + reference);
            }
        }

        return false;
    }

    private static List<String> extractTokens(Path file) throws IOException {

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<String> tokens = new ArrayList<String>();
        boolean readingTokens = false;

        for (String line : lines) {
            if (line.trim().equals("--- Tokens ---")) {
                readingTokens = true;
                continue;
            }
            if (line.trim().equals("--- Análise Sintática ---")) {
                break;
            }
            if (readingTokens && !line.trim().isEmpty()) {
                tokens.add(line.trim());
            }
        }

        if (!readingTokens) {
            throw new IOException("Cabeçalho --- Tokens --- não encontrado em " + file);
        }

        return tokens;
    }
}
