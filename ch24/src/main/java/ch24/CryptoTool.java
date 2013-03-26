package ch24;

import ch24.decrypt.Vigenere;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Утилита для генерации примеров в книге.
 *
 * @author Artem V. Navrotskiy
 */
public class CryptoTool {
    private static final Logger log = Logger.getLogger(CryptoTool.class.getName());

    public static void main(String[] args) throws IOException {
        Args param = new Args();
        CmdLineParser parser = new CmdLineParser(param);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.setUsageWidth(80);
            System.err.println(e.getMessage());
            System.err.println("Usage: " + CryptoTool.class.getName() + parser.printExample(ExampleMode.ALL));
            parser.printUsage(System.err);
            System.err.println();
            return;
        }
        log.info("Input file: " + param.inFile.getAbsoluteFile());
        // Получаем базовый алфавит, если надо.
        if (param.base == null) {
            param.base = Vigenere.sortAlphabet(param.crypt);
        }

        String text = IOUtils.toString(new FileInputStream(param.inFile), StandardCharsets.UTF_8.name());
        final String crypto = Vigenere.encrypt(param.base.toUpperCase(), param.crypt.toUpperCase(), param.keyword.toUpperCase(), text.toUpperCase());

        log.info("Output directory: " + param.outDir.getAbsolutePath());
        try (OutputStream stream = new FileOutputStream(new File(param.outDir, "encrypted.txt"))) {
            stream.write(Vigenere.format(crypto, param.block, param.line).getBytes(StandardCharsets.UTF_8));
        }
        // Слова по длине.
        List<String> words = Vigenere.findWords(crypto, 4);
        for (int i = 0; i < Math.min(words.size(), param.words); ++i) {
            try (OutputStream stream = new FileOutputStream(new File(param.outDir, "word-len-" + i + ".txt"))) {
                stream.write(words.get(i).getBytes(StandardCharsets.UTF_8));
            }
        }
        // Слова по частоте.
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int cmp = Integer.compare(crypto.replaceAll(s2, "").length() * s1.length(), crypto.replaceAll(s1, "").length() * s2.length());
                if (cmp != 0) {
                    return cmp;
                }
                return Integer.compare(s2.length(), s1.length());
            }
        });
        for (int i = 0; i < Math.min(words.size(), param.words); ++i) {
            try (OutputStream stream = new FileOutputStream(new File(param.outDir, "word-cnt-" + i + ".txt"))) {
                stream.write(words.get(i).getBytes(StandardCharsets.UTF_8));
            }
        }
        // Слова случайные.
        List<String> dictWords = findDictWords(crypto, 2);
        Collections.sort(dictWords, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s2.length(), s1.length());
            }
        });
        for (int i = 0; i < Math.min(dictWords.size(), param.words); ++i) {
            try (OutputStream stream = new FileOutputStream(new File(param.outDir, "word-dict-" + i + ".txt"))) {
                stream.write(dictWords.get(i).getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static List<String> findDictWords(String text, int minLength) throws IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(CryptoTool.class.getResourceAsStream("/ch24/word_ru.txt"), StandardCharsets.UTF_8))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String word = line.trim().toUpperCase();
                if (word.length() < minLength) {
                    continue;
                }
                if (text.contains(word)) {
                    result.add(word);
                }
            }
        }
        return result;
    }

    public static class Args {
        @Option(name = "-i", usage = "input file", metaVar = "INPUT", required = true)
        @NotNull
        private File inFile;

        @Option(name = "-o", usage = "output to this directory", metaVar = "OUTPUT")
        @NotNull
        private File outDir = new File("generated");

        @Option(name = "-b", usage = "base alphabet")
        private String base = null;

        @Option(name = "-k", usage = "ecnryption keyword", required = true)
        private String keyword;

        @Option(name = "-a", usage = "key alphabet", required = true)
        private String crypt;

        @Option(name = "--block", usage = "character block size")
        private int block = 5;

        @Option(name = "--line", usage = "maximum line size")
        private int line = 70;

        @Option(name = "--words", usage = "words count")
        private int words = 5;
    }
}
