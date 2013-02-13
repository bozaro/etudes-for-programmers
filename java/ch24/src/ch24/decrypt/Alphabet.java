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
    private final int[] sorted;

    public Alphabet(final float[] frequency, final String alphabet) {
        this.frequency = frequency;
        this.alphabet = alphabet;

        List<Integer> remap = new ArrayList<>(alphabet.length());
        for (int i = 0; i < alphabet.length(); ++i) {
            remap.add(i);
        }
        Collections.sort(remap, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Float.compare(frequency[b], frequency[a]);
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

    public float[] getFrequency() {
        return frequency;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public int[] getSorted() {
        return sorted;
    }
}
