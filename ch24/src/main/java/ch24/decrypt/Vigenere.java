package ch24.decrypt;

import java.util.*;

/**
 * Шифр Вижнера.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Vigenere {
    private final static char SPACE = ' ';
    private final static String SEPARATOR = "  ";

    /**
     * Получение базового алфавита из переставленного сортировкой.
     *
     * @param crypt Алфавит с перестановкой.
     * @return Исходный алфавит.
     */
    public static String sortAlphabet(String crypt) {
        final char[] chars = Arrays.copyOf(crypt.toCharArray(), crypt.length());
        Arrays.sort(chars);
        return new String(chars);
    }

    /**
     * Форматирование текстового блока.
     *
     * @param text  Текст.
     * @param block Длина блока символов.
     * @param line  Длина строки.
     * @return
     */
    public static String format(String text, int block, int line) {
        StringBuilder builder = new StringBuilder();
        int length = 0;
        for (int i = 0; i < text.length(); i += block) {
            if (length + block > line) {
                length = 0;
                builder.append('\n');
            } else if (length > 0) {
                builder.append(' ');
            }
            builder.append(text.substring(i, Math.min(i + block, text.length())));
            length += block + 1;
        }
        builder.append('\n');
        return builder.toString();
    }

    /**
     * Зашифрование текста.
     *
     * @param base  Исходный алфавит.
     * @param crypt Алфавит с перестановкой.
     * @param key   Ключевое слово.
     * @param text  Текст.
     * @return Возвращает шифрованный текст.
     */
    public static String encrypt(String base, String crypt, String key, String text) {
        Map<Character, Integer> alphabet = checkAlphabet(base, crypt);
        return idxToStr(base, encrypt(
                strToIdx(alphabet, crypt),
                strToIdx(alphabet, key),
                strToIdx(alphabet, text)
        ));
    }

    /**
     * Расшифрование текста.
     *
     * @param base  Исходный алфавит.
     * @param crypt Алфавит с перестановкой.
     * @param key   Ключевое слово.
     * @param data  Шифрованный тТекст.
     * @return Возвращает расшифрованный текст.
     */
    public static String decrypt(String base, String crypt, String key, String data) {
        Map<Character, Integer> alphabet = checkAlphabet(base, crypt);
        Map<Character, Integer> invert = checkAlphabet(crypt, base);
        return idxToStr(base, decrypt(
                strToIdx(invert, crypt),
                strToIdx(alphabet, key),
                strToIdx(invert, data)
        ));
    }

    public static List<String> findWords(String text, int minLength) {
        final List<String> result = new ArrayList<>();
        boolean[] mark = new boolean[text.length()];
        for (int l = 10; l >= minLength; --l) {
            for (int i = 0; i < text.length() - l * 2; ++i) {
                if (mark[i]) continue;
                for (int j = i + l; j < text.length() - l; ++j) {
                    if (mark[j]) continue;
                    boolean ok = true;
                    for (int k = 0; k < l; ++k) {
                        if (text.charAt(i + k) != text.charAt(j + k)) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        result.add(text.substring(i, i + l));
                        for (int k = 0; k < l; ++k) {
                            mark[i + k] = true;
                            mark[j + k] = true;
                        }
                    }
                }
            }
        }
        return result;
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

    public static String normalize(String base, String str) {
        return idxToStr(base, strToIdx(base, str));
    }

    public static String idxToStr(String base, int[] encrypt) {
        StringBuilder builder = new StringBuilder();
        for (int i : encrypt) {
            builder.append(base.charAt(i));
        }
        return builder.toString();
    }

    public static int[] strToIdx(String alphabet, String str) {
        return strToIdx(createAlphabet(alphabet), str);
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

    public static String formatSpaced(String text, String base, String spaced) {
        StringBuilder result = new StringBuilder();
        char lastChar = SPACE;
        int index = 0;
        for (char spacedChar : spaced.toCharArray()) {
            final char insertChar;
            if (base.contains(String.valueOf(spacedChar))) {
                insertChar = text.charAt(index);
                index++;
                if (index >= text.length()) {
                    index = 0;
                }
            } else {
                if (lastChar == SPACE) {
                    continue;
                }
                insertChar = SPACE;
            }
            lastChar = insertChar;
            result.append(insertChar);
        }
        return result.toString();
    }

    public static String square(String base, String crypt) {
        StringBuilder result = new StringBuilder();
        result
                .append(SPACE)
                .append(SEPARATOR)
                .append(base)
                .append('\n');
        for (int i = 0; i < crypt.length(); ++i) {
            result
                    .append(base.charAt(i))
                    .append(SEPARATOR)
                    .append(crypt.substring(i))
                    .append(crypt.substring(0, i))
                    .append('\n');
        }
        return result.toString();
    }
}
