package ch24.decrypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Алфавит и его частотное распределение.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Alphabet {
    private final float[] frequency;
    private final String alphabet;
    private final String sorted;

    public Alphabet(final float[] frequency, final String alphabet) {
        this.frequency = frequency;
        this.alphabet = alphabet;

        List<Character> chars = new ArrayList<>(alphabet.length());
        for (char c : alphabet.toCharArray()) {
            chars.add(c);
        }
        Collections.sort(chars, new Comparator<Character>() {
            @Override
            public int compare(Character a, Character b) {
                return Float.compare(frequency[alphabet.indexOf(b)], frequency[alphabet.indexOf(a)]);
            }
        });
        StringBuilder sortedBuilder = new StringBuilder();
        for (Character c : chars) {
            sortedBuilder.append(c);
        }
        sorted = sortedBuilder.toString();
    }

    public float[] getFrequency() {
        return frequency;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public String getSorted() {
        return sorted;
    }
}
