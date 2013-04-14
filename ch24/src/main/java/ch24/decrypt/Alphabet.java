package ch24.decrypt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Алфавит и его частотное распределение.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Alphabet {
    private final double[] frequency;
    private final String alphabet;
    private final int[] sorted;

    public Alphabet(final double[] frequency, final String alphabet) {
        this.frequency = frequency;
        this.alphabet = alphabet;

        List<Integer> remap = new ArrayList<>(alphabet.length());
        for (int i = 0; i < alphabet.length(); ++i) {
            remap.add(i);
        }
        Collections.sort(remap, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Double.compare(frequency[b], frequency[a]);
            }
        });
        StringBuilder x = new StringBuilder();
        sorted = new int[alphabet.length()];
        for (int i = 0; i < alphabet.length(); ++i) {
            //sorted[remap.get(i)] = i;
            sorted[i] = remap.get(i);
            x.append(alphabet.charAt(remap.get(i)));
        }
        System.out.println(x.toString());
    }

    public double[] getFrequency() {
        return frequency;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public int[] getSorted() {
        return sorted;
    }

    public int length() {
        return frequency.length;
    }

    public static Alphabet load(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        Pattern pattern = Pattern.compile("^(\\w+)\\s+(\\d*\\.?\\d+)\\s*$", Pattern.UNICODE_CHARACTER_CLASS);
        StringBuilder builder = new StringBuilder();
        List<Double> frequencyList = new ArrayList<>();
        Map<String, Double> words = new HashMap<>();
        float sum = 0.0F;
        // Добавляем частоты букв.
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) break;
            Matcher matcher = pattern.matcher(line.toUpperCase());
            if (matcher.find()) {
                String word = matcher.group(1);
                double frequency = Double.parseDouble(matcher.group(2));
                if (word.length() == 1) {
                    builder.append(word);
                    frequencyList.add(frequency);
                    sum += frequency;
                } else {
                    words.put(word, frequency);
                }
            }
        }
        String charLine = builder.toString();
        // Добавляем частоты для букв из-за типографского стиля.
        for (Map.Entry<String, Double> entry : words.entrySet()) {
            Double wordFreq = entry.getValue();
            String word = entry.getKey();
            for (char c : word.toCharArray()) {
                int charIdx = charLine.indexOf(c);
                if (charIdx < 0) {
                    throw new IOException(String.format("Character not found for word: %s (char: %s)", word, c));
                }
                double frequency = frequencyList.get(charIdx);
                frequencyList.set(charIdx, frequency + sum * wordFreq);
            }
        }
        // Нормализуем, чтобы сумма всех вероятностей была равна единице.
        final double[] frequencies = new double[frequencyList.size()];
        for (int i = 0; i < frequencies.length; ++i) {
            frequencies[i] = frequencyList.get(i);
        }
        return new Alphabet(MathHelper.normalize(frequencies), charLine);
    }

    public static Alphabet load(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return load(reader);
        }
    }
}
