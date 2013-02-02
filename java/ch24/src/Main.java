import ch24.decrypt.Alphabet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Alphabet alphabet = loadAlphabet(Main.class.getResourceAsStream("alphabet.txt"));
        float hitMin = getFreq(alphabet.getFrequency());
        float hitMax = 1.0F / alphabet.getFrequency().length;
        float hitLine = (hitMax * hitMax + hitMin * hitMin) / 2.0F;
        try (InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("crypto.txt"), StandardCharsets.UTF_8)) {
            int[] crypto = loadCrypto(reader, alphabet);
            for (int i = 1; i <= 20; ++i) {
                float hit = checkKeywordLength(crypto, alphabet, i);
                if (hit > hitLine) {
                    System.out.println(i + ": " + hit);
                    getFreqKeyword(crypto, alphabet, i);
                }
            }
        }
    }

    private static float getFreq(float[] frequency) {
        float hit = 0;
        for (float f : frequency) {
            hit += f * f;
        }
        return hit;
    }

    private static float getFreq(int[] list) {
        int count = 0;
        float result = 0;
        for (int i : list) {
            result += i * (i - 1);
            count += i;
        }
        return result / (count * (count - 1));
    }

    private static void getFreqKeyword(int[] crypto, Alphabet alphabet, int length) {
        float[] freq = alphabet.getFrequency();
        int[] counts = new int[length];
        int[][] table = new int[length][];
        for (int i = 0; i < length; ++i) {
            table[i] = new int[freq.length];
        }
        for (int i = 0; i < crypto.length; ++i) {
            table[i % length][crypto[i]]++;
            counts[i % length]++;
        }
        int[] result = new int[length];
        for (int i = 1; i < length; ++i) {
            long[] freqShift = new long[freq.length];
            for (int testShift = 0; testShift < freq.length; ++testShift) {
                long testFreq = 0;
                for (int j = 0; j < freq.length; j++) {
                    testFreq += table[0][j] * table[i][(j + testShift) % freq.length];
                }
                freqShift[testShift] = testFreq;
            }
            int shift = 0;
            for (int j = 0; j < freqShift.length; ++j) {
                if (freqShift[shift] < freqShift[j]) {
                    shift = j;
                }
            }
            result[i] = shift;
        }
        result[2] -= 3;
        for (int i = 0; i < freq.length; ++i) {
            StringBuilder key = new StringBuilder();
            for (int j = 0; j < length; ++j) {
                key.append(alphabet.getAlphabet().charAt((freq.length - result[j] + i) % freq.length));
            }
            System.out.println(key.toString());
        }
    }

    private static float checkKeywordLength(int[] crypto, Alphabet alphabet, int length) {
        float result = 0;
        for (int i = 0; i < length; ++i) {
            float hit = 0;
            int count = 0;
            int[] freqs = new int[alphabet.getFrequency().length];
            for (int j = i; j < crypto.length; j += length) {
                freqs[crypto[j]]++;
                count++;
            }
            for (int freq : freqs) {
                hit += (freq * (freq - 1)) / (float) (count * (count - 1));
            }
            //hit -= base;
            result += hit * hit;
        }
        return result / length;
    }

    public static Alphabet loadAlphabet(InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return loadAlphabet(reader);
        }
    }

    public static Alphabet loadAlphabet(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        Pattern pattern = Pattern.compile("^(\\w)\\s+(\\d*\\.?\\d+)\\s*$", Pattern.UNICODE_CHARACTER_CLASS);
        StringBuilder builder = new StringBuilder();
        List<Float> frequencyList = new ArrayList<>();
        float sum = 0.0F;
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) break;
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                builder.append(matcher.group(1));
                float frequency = Float.parseFloat(matcher.group(2));
                frequencyList.add(frequency);
                sum += frequency;
            }
        }
        float[] frequencies = new float[frequencyList.size()];
        for (int i = 0; i < frequencies.length; ++i) {
            frequencies[i] = frequencyList.get(i) / sum;
        }
        return new Alphabet(frequencies, builder.toString().toUpperCase());
    }

    public static int[] loadCrypto(Reader reader, Alphabet alphabet) throws IOException {
        String chars = alphabet.getAlphabet();
        List<Integer> list = new ArrayList<>();
        while (true) {
            int c = reader.read();
            if (c < 0) break;
            int index = chars.indexOf((char) c);
            if (index >= 0) {
                list.add(index);
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = list.get(i);
        }
        return result;
    }
}
