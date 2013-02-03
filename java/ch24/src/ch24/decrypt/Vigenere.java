package ch24.decrypt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Шифр Вижнера.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Vigenere {
    public static String encrypt(String base, String crypt, String key, String text) {
        Map<Character, Integer> alphabet = checkAlphabet(base, crypt);
        return idxToStr(base, encrypt(
                strToIdx(alphabet, crypt),
                strToIdx(alphabet, key),
                strToIdx(alphabet, text)
        ));
    }

    public static String decrypt(String base, String crypt, String key, String data) {
        Map<Character, Integer> alphabet = checkAlphabet(base, crypt);
        Map<Character, Integer> invert = checkAlphabet(crypt, base);
        return idxToStr(base, decrypt(
                strToIdx(invert, crypt),
                strToIdx(alphabet, key),
                strToIdx(invert, data)
        ));
    }

    public static int[] encrypt(int[] crypt, int[] key, int[] text) {
        int[] data = new int[text.length];
        for (int i = 0; i < text.length; ++i) {
            int shift = key[i % key.length];
            data[i] = crypt[(text[i] + shift) % crypt.length];
        }
        return data;
    }

    public static int[] decrypt(int[] crypt, int[] key, int[] data) {
        int[] text = new int[data.length];
        for (int i = 0; i < data.length; ++i) {
            int shift = key[i % key.length];
            text[i] = crypt[(crypt.length + data[i] - shift) % crypt.length];
        }
        return text;
    }

    public static String idxToStr(String base, int[] encrypt) {
        StringBuilder builder = new StringBuilder();
        for (int i : encrypt) {
            builder.append(base.charAt(i));
        }
        return builder.toString();
    }

    public static int[] strToIdx(Map<Character, Integer> alphabet, String str) {
        int idx[] = new int[str.length()];
        int size = 0;
        for (char c : str.toCharArray()) {
            Integer i = alphabet.get(c);
            if (i != null) {
                idx[size] = i;
                size++;
            }
        }
        return Arrays.copyOf(idx, size);
    }

    public static Map<Character, Integer> createAlphabet(String base) {
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < base.length(); ++i) {
            char c = base.charAt(i);
            if (map.containsKey(c)) {
                throw new IllegalArgumentException("Duplicate characted: " + c);
            }
            map.put(c, i);
        }
        return map;
    }

    private static Map<Character, Integer> checkAlphabet(String base, String crypt) {
        Map<Character, Integer> map = createAlphabet(base);
        Map<Character, Integer> check = new HashMap<>(map);
        for (char c : crypt.toCharArray()) {
            if (check.remove(c) == null) {
                throw new IllegalArgumentException("Cann't found char in base alphabed: " + c);
            }
        }
        if (!check.isEmpty()) {
            throw new IllegalArgumentException("Differ alphabet length.");
        }
        return map;
    }
}
