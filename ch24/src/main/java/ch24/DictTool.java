package ch24;

import ch24.decrypt.Alphabet;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Генерация данных об алфавите.
 *
 * @author Artem V. Navrotskiy
 */
public class DictTool {
    private static final Logger log = Logger.getLogger(DictTool.class.getName());

    public static void main(String[] args) throws IOException {
        Args param = new Args();
        CmdLineParser parser = new CmdLineParser(param);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.setUsageWidth(80);
            System.err.println(e.getMessage());
            System.err.println("Usage: " + DictTool.class.getName() + parser.printExample(ExampleMode.ALL));
            parser.printUsage(System.err);
            System.err.println();
            return;
        }
        param.outDir.mkdirs();
        log.info("Output directory: " + param.outDir.getAbsolutePath());
        try (InputStream stream = DictTool.class.getResourceAsStream("/ch24/alphabet_ru.txt")) {
            Alphabet alphabet = Alphabet.load(stream);
            double rate = 0;
            for (double freq : alphabet.getFrequency()) {
                rate += (freq * freq);
            }

            Locale locale = new Locale(param.locale);
            log.info("Locale: " + locale.toString());
            DecimalFormat format = new DecimalFormat(param.format, DecimalFormatSymbols.getInstance(locale));
            writeFile(param.outDir, "freq-min.txt", format.format(rate - param.delta));
            writeFile(param.outDir, "freq-norm.txt", format.format(rate));
            writeFile(param.outDir, "freq-max.txt", format.format(rate + param.delta));

            StringBuilder table = new StringBuilder();
            table.append("<tbody xmlns=\"http://docbook.org/ns/docbook\">\n");
            int size = alphabet.getSorted().length;
            int totalRows = (size + param.rows - 1) / param.rows;
            for (int i = 0; i < totalRows; ++i) {
                table.append("<row>\n");
                for (int j = 0; j < param.rows; ++j) {
                    int cell = j * totalRows + i;
                    if (cell < size) {
                        int idx = alphabet.getSorted()[cell];
                        table.append("  <entry>")
                                .append(alphabet.getAlphabet().charAt(idx))
                                .append("</entry><entry>")
                                .append(format.format(alphabet.getFrequency()[idx]))
                                .append("</entry>\n");
                    } else {
                        table.append("  <entry /><entry />\n");
                    }
                }
                table.append("</row>\n");
            }
            table.append("</tbody>\n");
            writeFile(param.outDir, "table.xml", table.toString());
        }
    }

    private static void writeFile(File basePath, String fileName, String content) throws IOException {
        try (OutputStream stream = new FileOutputStream(new File(basePath, fileName))) {
            stream.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static class Args {
        @Option(name = "-o", usage = "output to this directory", metaVar = "OUTPUT")
        @NotNull
        private File outDir = new File("build/generated-test");

        @Option(name = "--rows", usage = "rows count")
        private int rows = 1;

        @Option(name = "--delta", usage = "freq delta")
        private double delta = 0.01;

        @Option(name = "--locale", usage = "number locale")
        private String locale = "ru_RU";

        @Option(name = "--format", usage = "number format")
        private String format = "0.000";
    }

}
