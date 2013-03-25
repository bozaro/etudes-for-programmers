package ch24;

import ch24.decrypt.Vigenere;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Утилита для генерации примеров в книге.
 *
 * @author Artem V. Navrotskiy
 */
public class CryptoTool {
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
        System.out.println(param.inFile.getAbsoluteFile());
        // Получаем базовый алфавит, если надо.
        if (param.base == null) {
            param.base = Vigenere.sortAlphabet(param.crypt);
        }

        String text = IOUtils.toString(new FileInputStream(param.inFile), StandardCharsets.UTF_8.name());
        String crypto = Vigenere.encrypt(param.base.toUpperCase(), param.crypt.toUpperCase(), param.keyword.toUpperCase(), text.toUpperCase());
        System.out.println(crypto);
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
    }
}
