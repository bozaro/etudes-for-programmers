package ch24.decrypt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Расшифровка шифра Виженера.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Decrypter {
    private final static Logger log = Logger.getLogger(Decrypter.class.getName());

    private Decrypter() {
    }

    /**
     * Поиск длины ключевого слова.
     *
     * @param alphabet Данные алфавита.
     * @param text     Шифрованный текст.
     * @return Возвращает длину ключевого слова или -1, если не нашли.
     */
    public static int findKeywordLength(Alphabet alphabet, String text) {
        // Вычисление идиального коэфициента попадания.
        double hitMin = 0.0;
        for (double f : alphabet.getFrequency()) {
            hitMin += f * f;
        }
        double hitMax = 1.0 / alphabet.getFrequency().length;
        // Вычисление граничного коэфициента.
        double hitLine = (hitMax * hitMax + hitMin * hitMin) / 2.0;
        // Поиск подходящей длины слова.
        int[] crypto = Vigenere.strToIdx(alphabet.getAlphabet(), text);
        log.info(String.format("Find keyword length:"));
        for (int i = 1; i < (int) Math.sqrt(crypto.length); ++i) {
            double hit = checkKeywordLength(alphabet, crypto, i);
            log.info(String.format("  hit rate for length %d: %f", i, hit));
            if (hit > hitLine) {
                log.info(String.format("Found keyword length: %d", i));
                return i;
            }
        }
        log.info(String.format("Keyword length not found :("));
        return -1;
    }

    /**
     * Поиск алфавита для расшифровки.
     *
     * @param alphabet Данные алфавита.
     * @param text     Шифрованный текст.
     * @param keyword  Ключевое слово.
     * @return Предполагаемый алфавит.
     */
    public static String findAlphabet(Alphabet alphabet, String text, String keyword) {
        return findAlphabet(alphabet, text, keyword, "");
    }

    /**
     * Поиск алфавита для расшифровки.
     *
     * @param alphabet Данные алфавита.
     * @param text     Шифрованный текст.
     * @param keyword  Ключевое слово.
     * @param hint     Найденные части алфавита (для уточнения алфавита).
     * @return Предполагаемый алфавит.
     */
    public static String findAlphabet(Alphabet alphabet, String text, String keyword, String hint) {
        int alphabetLength = alphabet.length();
        // Получение сдвигов для ключевого слова.
        final int[] keywordIdx = Vigenere.strToIdx(alphabet.getAlphabet(), keyword);
        final int[] shifts = new int[keywordIdx.length];
        for (int i = 0; i < keywordIdx.length; ++i) {
            shifts[i] = (alphabetLength - keywordIdx[i]) % alphabetLength;
        }
        // Проставляем в массиве для перестановочного алфавита индексты букв из подсказки.
        int abc[] = new int[alphabetLength];
        for (int i = 0; i < alphabetLength; ++i) {
            abc[i] = -1;
        }
        for (int i = 0; i < alphabetLength; ++i) {
            int idx = hint.indexOf(alphabet.getAlphabet().charAt(i));
            if (idx >= 0) {
                abc[idx] = i;
            }
        }
        // Подбор оставшейся части алфавита.
        double[][][] p = getMatrix(alphabet, text, keyword.length());
        for (int c = 0; c < alphabetLength; ++c) {
            int i = alphabet.getSorted()[c];

            if (hint.indexOf(alphabet.getAlphabet().charAt(i)) >= 0) {
                continue;
            }

            double pMax = 0;
            int found = 0;
            for (int j = 0; j < alphabetLength; ++j) {
                double pShift = 1.0;
                for (int k = 0; k < shifts.length; ++k) {
                    pShift *= p[k][i][(alphabetLength + j + shifts[k]) % alphabetLength];
                }
                if (abc[j] >= 0) {
                    continue;
                }
                if (pMax < pShift) {
                    found = j;
                    pMax = pShift;
                }
            }
            abc[found] = i;
            log.info(String.format("%s -> %s: %f", alphabet.getAlphabet().charAt(i), alphabet.getAlphabet().charAt(found), pMax));
        }
        return Vigenere.idxToStr(alphabet.getAlphabet(), abc);
    }

    /**
     * Получение матрицы соответствия букв.
     *
     * @param alphabet      Данные алфавита.
     * @param text          Шифрованный текст.
     * @param keywordLength Длина ключевого слова.
     * @return Массив p[смещение][y][x] с вероятностью соответствия буквы x букве y.
     */
    public static double[][][] getMatrix(Alphabet alphabet, String text, int keywordLength) {
        final double[][][] p = new double[keywordLength][][];
        final double[] freq = alphabet.getFrequency();
        final int[] crypto = Vigenere.strToIdx(alphabet.getAlphabet(), text);
        // Общее кол-во букв для каждой буквы ключевого слова.
        int[] counts = new int[keywordLength];
        int[][] table = new int[keywordLength][];
        for (int i = 0; i < keywordLength; ++i) {
            table[i] = new int[freq.length];
        }
        for (int i = 0; i < crypto.length; ++i) {
            table[i % keywordLength][crypto[i]]++;
            counts[i % keywordLength]++;
        }
        // Рассчет матрицы соответствия.
        for (int k = 0; k < keywordLength; ++k) {
            p[k] = new double[freq.length][];
            for (int i = 0; i < freq.length; ++i) {
                double r[] = new double[freq.length];
                for (int j = 0; j < freq.length; ++j) {
                    r[j] = Math.pow(freq[j], table[k][i]) * Math.pow((1 - freq[j]), counts[k] - table[k][i]);
                }
                p[k][i] = MathHelper.normalize(r);
            }
        }
        return p;
    }

    /**
     * Получение матрицы соответствия сдвигов в ключевом слове.
     *
     * @param alphabet      Данные алфавита.
     * @param text          Шифрованный текст.
     * @param keywordLength Длина ключевого слова.
     * @return Массив p[a][b][x] с вероятностью сдвига в позиции ключевого слова a относительно позиции b на x.
     */
    public static double[][][] getShiftMatrix(Alphabet alphabet, String text, int keywordLength) {
        double[][][] p = getMatrix(alphabet, text, keywordLength);
        int alphabetLength = alphabet.length();
        double[][][] pp = new double[keywordLength][][];
        for (int k = 0; k < keywordLength; ++k) {
            pp[k] = new double[keywordLength][];
            for (int l = 0; l < keywordLength; ++l) {
                double[] x = new double[alphabetLength];
                for (int r = 0; r < alphabetLength; ++r) {
                    x[r] = 1.0F;
                    for (int j = 0; j < alphabetLength; ++j) {
                        double sum = 0;
                        for (int i = 0; i < alphabetLength; ++i) {
                            sum += p[k][j][i] * p[l][j][(i + r) % alphabetLength];
                        }
                        x[r] *= sum;
                    }
                }
                pp[k][l] = MathHelper.normalize(x);
            }
        }
        return pp;
    }

    /**
     * Поиск ключевого слова по словарю.
     *
     * @param alphabet      Алфавит.
     * @param text          Шифрованный текст.
     * @param keywordLength Длина ключевого слова.
     * @param stream        Поток для чтения списка слов.
     * @return Возвращает наиболее вероятное ключевое слово. Или null, если слов данной длины в словаре нет.
     * @throws IOException
     */
    public static String findKeywordDictionary(Alphabet alphabet, String text, int keywordLength, InputStream stream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return findKeywordDictionary(alphabet, text, keywordLength, reader);
        }
    }

    /**
     * Поиск ключевого слова по словарю.
     *
     * @param alphabet      Алфавит.
     * @param text          Шифрованный текст.
     * @param keywordLength Длина ключевого слова.
     * @param reader        Поток для чтения списка слов.
     * @return Возвращает наиболее вероятное ключевое слово. Или null, если слов данной длины в словаре нет.
     * @throws IOException
     */
    public static String findKeywordDictionary(Alphabet alphabet, String text, int keywordLength, Reader reader) throws IOException {
        double[][][] pp = getShiftMatrix(alphabet, text, keywordLength);

        BufferedReader bufferedReader = new BufferedReader(reader);
        Map<Character, Integer> charMapping = Vigenere.createAlphabet(alphabet.getAlphabet());
        double resultRate = 0;
        String resultWord = null;
        log.info("Search keyword...");
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            }
            String word = line.toUpperCase();
            int[] wordIdx = Vigenere.strToIdx(charMapping, word);
            if (wordIdx.length != keywordLength) {
                continue;
            }
            double rate = getKeywordRate(pp, wordIdx);
            if (resultRate < rate) {
                resultRate = rate;
                resultWord = word;
                log.info(String.format("  %s: %e", word, rate));
            }
        }
        log.info(String.format("Found keyword: %s (rate: %e)", resultWord, resultRate));
        return resultWord;
    }

    /**
     * Поиск ключевого слова математическим методом.
     *
     * @param alphabet      Алфавит.
     * @param text          Шифрованный текст.
     * @param keywordLength Длина ключевого слова.
     * @return Возвращает наиболее вероятное ключевое слово.
     */
    public static String findKeywordMath(Alphabet alphabet, String text, int keywordLength) {
        double[][][] pp = getShiftMatrix(alphabet, text, keywordLength);

        double resultRate = 0;
        int[] checkIdx = new int[keywordLength];
        int[] resultWord = null;
        for (int pass = 0; ; ++pass) {
            int n = pass;
            for (int i = 0; i < keywordLength; ++i) {
                checkIdx[i] = -1;
            }
            for (int i = 0; i < keywordLength; ++i) {
                int idx = n % (keywordLength - i);
                n = n / (keywordLength - i);
                for (int j = 0; ; ++j) {
                    if (checkIdx[j] < 0) {
                        if (idx == 0) {
                            checkIdx[j] = i;
                            break;
                        }
                        --idx;
                    }
                }
            }
            if (n > 0) {
                break;
            }
            int[] word = findKeywordMath(pp, checkIdx);
            double rate = Decrypter.getKeywordRate(pp, word);
            if (resultRate < rate) {
                resultRate = rate;
                resultWord = word;
            }
        }
        return Vigenere.idxToStr(alphabet.getAlphabet(), resultWord);
    }

    private static int[] findKeywordMath(final double[][][] pp, int[] checkIdx) {
        int[] result = new int[checkIdx.length];
        int alphabetLength = pp[0][0].length;
        for (int l = 1; l < checkIdx.length; ++l) {
            double resultRate = 0;
            int resultShift = 0;
            int pl = checkIdx[l];
            for (int shift = 0; shift < alphabetLength; ++shift) {
                double rate = 1.0;
                for (int i = 0; i < l; ++i) {
                    int pi = checkIdx[i];
                    rate *= pp[pi][pl][(alphabetLength + result[pi] - shift) % alphabetLength];
                }
                if (resultRate < rate) {
                    resultRate = rate;
                    resultShift = shift;
                }
            }
            result[pl] = resultShift;
        }
        for (int i = 1; i < result.length; ++i) {
            result[i] = (alphabetLength + result[i] - result[0]) % alphabetLength;
        }
        result[0] = 0;
        return result;
    }

    public static double getKeywordRate(double[][][] pp, int[] word) {
        final int alphabetLength = pp[0][0].length;
        double rate = 1.0F;
        for (int i = 0; i < word.length; ++i) {
            for (int j = 0; j < word.length; ++j) {
                if (i != j) {
                    rate *= pp[i][j][(alphabetLength + word[i] - word[j]) % alphabetLength];
                }
            }
        }
        return rate;
    }

    /**
     * Получение коэфициента соответствия того, что ключевое слово имеет указанную длину.
     *
     * @param crypto   Шифрованный текст.
     * @param alphabet Данные алфавита.
     * @param length   Предполагаемая длина ключевого слова.
     * @return Коэфициент соответствия.
     */
    private static double checkKeywordLength(Alphabet alphabet, int[] crypto, int length) {
        float result = 0;
        for (int i = 0; i < length; ++i) {
            double hit = 0;
            int count = 0;
            int[] freqs = new int[alphabet.getFrequency().length];
            for (int j = i; j < crypto.length; j += length) {
                freqs[crypto[j]]++;
                count++;
            }
            for (int freq : freqs) {
                hit += (freq * (freq - 1)) / (double) (count * (count - 1));
            }
            result += hit * hit;
        }
        return result / length;
    }
}
