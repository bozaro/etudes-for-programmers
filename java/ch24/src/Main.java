import ch24.decrypt.Alphabet;
import ch24.decrypt.Vigenere;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        Alphabet alphabet = loadAlphabet(Main.class.getResourceAsStream("alphabet.txt"));

        float[] freq = alphabet.getFrequency();
        for (int shift = 0; shift < freq.length; ++shift) {
            float x = 0;
            for (int a = 0; a < freq.length; ++a) {
                int b = (a + freq.length - shift) % freq.length;
                x += Math.abs(freq[a] - freq[b]) * (freq[a] + freq[b]);
            }
            System.out.println(shift + ": " + x);
        }

        if (true) {
            float hitMin = getFreq(alphabet.getFrequency());
            float hitMax = 1.0F / alphabet.getFrequency().length;
            float hitLine = (hitMax * hitMax + hitMin * hitMin) / 2.0F;
            boolean[] mark = new boolean[20];
            try (InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("crypto.txt"), StandardCharsets.UTF_8)) {
                int[] crypto = loadCrypto(reader, alphabet);
                for (int i = 1; i < mark.length; ++i) {
                    if (mark[i]) continue;
                    float hit = checkKeywordLength(crypto, alphabet, i);
                    if (hit > hitLine) {
                        for (int j = 0; j < mark.length; j += i) {
                            mark[j] = true;
                        }
                        System.out.println(i + ": " + hit);
                        getFreqKeyword(crypto, alphabet, i);
                    }
                }
            }
            /*if (true)
                return;*/
        }

        String text = "ВАРИАНТ RUNNING KEY (БЕГУЩИЙ КЛЮЧ) ШИФРА ВИЖЕНЕРА КОГДА-ТО БЫЛ НЕВЗЛАМЫВАЕМЫМ. ЭТА ВЕРСИЯ ИСПОЛЬЗУЕТ В КАЧЕСТВЕ КЛЮЧА БЛОК ТЕКСТА, РАВНЫЙ ПО ДЛИНЕ ИСХОДНОМУ ТЕКСТУ. ТАК КАК КЛЮЧ РАВЕН ПО ДЛИНЕ СООБЩЕНИЮ, ТО МЕТОДЫ ПРЕДЛОЖЕННЫЕ ФРИДМАНОМ И КАСИСКИ НЕ РАБОТАЮТ (ТАК КАК КЛЮЧ НЕ ПОВТОРЯЕТСЯ). В 1920 ГОДУ ФРИДМАН ПЕРВЫМ ОБНАРУЖИЛ НЕДОСТАТКИ ЭТОГО ВАРИАНТА. ПРОБЛЕМА С RUNNING KEY ШИФРА ВИЖЕНЕРА СОСТОИТ В ТОМ, ЧТО КРИПТОАНАЛИТИК ИМЕЕТ СТАТИСТИЧЕСКУЮ ИНФОРМАЦИЮ О КЛЮЧЕ (УЧИТЫВАЯ, ЧТО БЛОК ТЕКСТА НАПИСАН НА ИЗВЕСТНОМ ЯЗЫКЕ) И ЭТА ИНФОРМАЦИЯ БУДЕТ ОТРАЖАТЬСЯ В ШИФРОВАННОМ ТЕКСТЕ. ЕСЛИ КЛЮЧ ДЕЙСТВИТЕЛЬНО СЛУЧАЙНЫЙ, ЕГО ДЛИНА РАВНА ДЛИНЕ СООБЩЕНИЯ И ОН ИСПОЛЬЗОВАЛСЯ ЕДИНОЖДЫ, ТО ШИФР ВИЖЕНЕРА ТЕОРЕТИЧЕСКИ БУДЕТ НЕВЗЛАМЫВАЕМЫМ."
                + "А ВОТ ГДЕ КОРРЕКТНОСТЬ ДЕЙСТВИТЕЛЬНО ВАЖНА, ТАК ЭТО В ПОЛЯХ С АДРЕСОМ ЭЛЕКТРОННОЙ ПОЧТЫ, НОМЕРОМ БАНКОВСКОЙ КАРТЫ, ZIP КОДОМ И Т.Д." +
                "НЕКОТОРЫЕ ИНТЕРНЕТ-МАГАЗИНЫ НЕ ТОРОПЯТСЯ ОТПРАВЛЯТЬ ПОКУПАТЕЛЯ К ОФОРМЛЕНИЮ ЗАКАЗА СРАЗУ ПОСЛЕ ДОБАВЛЕНИЯ ТОВАРА В КОРЗИНУ И ПРАВИЛЬНО ДЕЛАЮТ. ЭТО ДАЕТ ПОЛЬЗОВАТЕЛЮ СТИМУЛ К ПРОДОЛЖЕНИЮ ВИРТУАЛЬНОГО ШОПИНГА И, В КОНЦЕ КОНЦОВ, УВЕЛИЧИВАЕТ ДОХОДЫ МАГАЗИНА. КАК РАЗ ЗДЕСЬ АНИМИРОВАННАЯ КОРЗИНА И ВСТУПАЕТ В ИГРУ. ЭТО ЭФФЕКТИВНЫЙ СПОСОБ ДАТЬ ПОЛЬЗОВАТЕЛЮ ПОДТВЕРЖДЕНИЕ, ЧТО ВЫБРАННЫЙ ТОВАР УЖЕ ПОМЕЩЕН В ЕГО КОРЗИНУ И ЧТО В ЛЮБОЙ МОМЕНТ ОН МОЖЕТ ЗАВЕРШИТЬ ОФОРМЛЕНИЕ ЗАКАЗА, ПРИ ЭТОМ, НЕ ОТВЛЕКАЯ ЕГО ОТ ДАЛЬНЕЙШЕГО ШОПИНГА.\n" +
                "ИНТЕРНЕТ-МАГАЗИН AMERICAN EAGLE РАСКРЫВАЕТ ПЕРЕД ПОЛЬЗОВАТЕЛЕМ СПЕЦИАЛЬНУЮ ОБЛАСТЬ В НИЖНЕЙ ЧАСТИ ЭКРАНА, В КОТОРОЙ ОТОБРАЖАЕТСЯ ДОБАВЛЕННЫЙ В КОРЗИНУ ТОВАР, ДЕТАЛИ ТРАНЗАКЦИИ И КНОПКА СОВЕРШЕНИЯ ЗАКАЗА." +
                "КАК ТОЛЬКО ДЛИНА КЛЮЧА СТАНОВИТСЯ ИЗВЕСТНОЙ, ЗАШИФРОВАННЫЙ ТЕКСТ МОЖНО ЗАПИСАТЬ ВО МНОЖЕСТВО СТОЛБЦОВ, КАЖДЫЙ ИЗ КОТОРЫХ СООТВЕТСТВУЕТ ОДНОМУ СИМВОЛУ КЛЮЧА. КАЖДЫЙ СТОЛБЕЦ СОСТОИТ ИЗ ИСХОДНОГО ТЕКСТА, КОТОРЫЙ ЗАШИФРОВАН ШИФРОМ ЦЕЗАРЯ; КЛЮЧ К ШИФРУ ЦЕЗАРЯ ЯВЛЯЕТСЯ ВСЕГО-НАВСЕГО ОДНИМ СИМВОЛОМ КЛЮЧА ДЛЯ ШИФРА ВИЖЕНЕРА, КОТОРЫЙ ИСПОЛЬЗУЕТСЯ В ЭТОМ СТОЛБЦЕ. ИСПОЛЬЗУЯ МЕТОДЫ, ПОДОБНЫЕ МЕТОДАМ ВЗЛОМА ШИФРА ЦЕЗАРЯ, МОЖНО РАСШИФРОВАТЬ ЗАШИФРОВАННЫЙ ТЕКСТ. УСОВЕРШЕНСТВОВАНИЕ ТЕСТА КАСИСКИ, ИЗВЕСТНОЕ КАК МЕТОД КИРХГОФА, ЗАКЛЮЧАЕТСЯ В СРАВНЕНИИ ЧАСТОТЫ ПОЯВЛЕНИЯ СИМВОЛОВ В СТОЛБЦАХ С ЧАСТОТОЙ ПОЯВЛЕНИЯ СИМВОЛОВ В ИСХОДНОМ ТЕКСТЕ ДЛЯ НАХОЖДЕНИЯ КЛЮЧЕВОГО СИМВОЛА ДЛЯ ЭТОГО СТОЛБЦА. КОГДА ВСЕ СИМВОЛЫ КЛЮЧА ИЗВЕСТНЫ, КРИПТОАНАЛИТИК МОЖЕТ ЛЕГКО РАСШИФРОВАТЬ ШИФРОВАННЫЙ ТЕКСТ, ПОЛУЧИВ ИСХОДНЫЙ ТЕКСТ. МЕТОД КИРХГОФА НЕ ПРИМЕНИМ, КОГДА ТАБЛИЦА ВИЖЕНЕРА СКРЕМБЛИРОВАНА, ВМЕСТО ИСПОЛЬЗОВАНИЯ ОБЫЧНОЙ АЛФАВИТНОЙ ПОСЛЕДОВАТЕЛЬНОСТИ, ХОТЯ ТЕСТ КАСИСКИ И ТЕСТЫ СОВПАДЕНИЯ ВСЁ ЕЩЁ МОГУТ ИСПОЛЬЗОВАТЬСЯ ДЛЯ ОПРЕДЕЛЕНИЯ ДЛИНЫ КЛЮЧА ДЛЯ ЭТОГО СЛУЧАЯ."
                        .replaceAll("Ё", "Е");
        findWords(Vigenere.encrypt("АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", "А", text).substring(0, 500));
        // ОЕАИТНСВРЛКМДПЗЯЧФУЫЬГБШЙЖЮХЦЭЩЪ
        // ОЕАИНТСРВЛКМДПУЯЫЬГЗБЧЙХЖШЮЦЩЭФЪ
        // ОАЕИНТРСЛМВПКДЯЫБЗУГЬЧЙХЦЖЮЩФЭШЪ

        // ОЕАИНТСРВЛКМДПУЯЫЬГЗБЧЙХЖШЮЦЩЭФЪ
        // оеанитслвркдмумяьыгбчзжйшхюэцщфъ
        String base = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        String crypt = "ЗУШВЬЯЖЩКГЛФМДПЪЫНЮОСИЙТЧБАЭХЦЕР";
        //crypt = base;
        String crypto = Vigenere.encrypt(base, crypt, "ПОДЛЮЕМ", text);
        getFreqKeyword(Vigenere.strToIdx(Vigenere.createAlphabet(base), crypto), alphabet, 7);
        /*
        crypto = Vigenere.encrypt(base, "БВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯА", "А", text);
        getFreqKeyword(Vigenere.strToIdx(Vigenere.createAlphabet(base), crypto), alphabet, 1);*/
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
        final float[] freq = alphabet.getFrequency();
        int[] counts = new int[length];
        int[][] table = new int[length][];
        for (int i = 0; i < length; ++i) {
            table[i] = new int[freq.length];
        }
        for (int i = 0; i < crypto.length; ++i) {
            table[i % length][crypto[i]]++;
            counts[i % length]++;
        }


        double[][][] p = new double[length][][];
        for (int k = 0; k < length; ++k) {
            p[k] = new double[freq.length][];
            for (int i = 0; i < freq.length; ++i) {
                p[k][i] = new double[freq.length];
                for (int j = 0; j < freq.length; ++j) {
                    double sum = 0;
                    for (int m = 0; m < freq.length; ++m) {
                        sum += Math.pow(freq[m], table[k][j]) * Math.pow(1 - freq[m], counts[k] - table[k][j]);
                    }
                    p[k][i][j] = Math.pow(freq[i], table[k][j]) * Math.pow((1 - freq[i]), counts[k] - table[k][j]) / sum;
                }
            }
        }


        double[][][] px = new double[length][][];
        for (int k = 0; k < length; ++k) {
            px[k] = new double[length][];
            for (int l = 0; l < length; ++l) {
                px[k][l] = new double[freq.length];
                for (int r = 0; r < freq.length; ++r) {
                    double mul = 1.0F;
                    for (int j = 0; j < freq.length; ++j) {
                        double sum = 0;
                        for (int i = 0; i < freq.length; ++i) {
                            sum += p[k][i][j] * p[l][(i + r) % freq.length][j];
                        }
                        mul *= sum;
                    }
                    px[k][l][r] = mul;
                }
            }
        }

        double[][][] pp = new double[length][][];
        for (int k = 0; k < length; ++k) {
            pp[k] = new double[length][];
            for (int l = 0; l < length; ++l) {
                pp[k][l] = new double[freq.length];
                for (int r = 0; r < freq.length; ++r) {
                    double sum = 0;
                    for (int s = 0; s < freq.length; ++s) {
                        sum += px[k][l][s];
                    }
                    pp[k][l][r] = px[k][l][r] / sum;
                }
            }
        }

        int[] result = findKeyword(alphabet, pp);
        for (int i = 0; i < freq.length; ++i) {
            StringBuilder key = new StringBuilder();
            float x = 0;
            for (int j = 0; j < length; ++j) {
                int charIndex = (freq.length - result[j] + i) % freq.length;
                key.append(alphabet.getAlphabet().charAt(charIndex));
                x += freq[charIndex] / length;
            }
            //if (x > 0.035) {
            System.out.println(key.toString() + ": " + x);
            //}
        }

        List<Integer> remap = new ArrayList<>();
        final int[] counter = new int[freq.length];
        for (int i = 0; i < freq.length; ++i) {
            for (int j = 0; j < result.length; ++j) {
                counter[i] += table[j][(freq.length + i + result[j]) % freq.length];
            }
            //System.out.println(i + ": " + (counter[i] * 1.0 / crypto.length));
        }
        for (int i = 0; i < freq.length; ++i) {
            remap.add(i);
        }
        Collections.sort(remap, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(counter[b], counter[a]);
            }
        });
        int[] invRemap = new int[freq.length];
        for (int i = 0; i < freq.length; ++i) {
            invRemap[remap.get(i)] = i;
        }

        StringBuilder sabc = new StringBuilder();
        int[] abc = new int[freq.length];
        int[] non = new int[freq.length];
        for (int i = 0; i < freq.length; ++i) {
            abc[i] = alphabet.getSorted()[invRemap[i]];
            non[i] = i;
            sabc.append(alphabet.getAlphabet().charAt(abc[i]));
        }
        System.out.println(sabc.toString());
        int[] text = Vigenere.decrypt(abc, result, crypto);
        //int[] text = Vigenere.decrypt(non, result, crypto);
        String text1 = Vigenere.idxToStr(alphabet.getAlphabet(), text);
        System.out.println(text1);

        findWords(text1);
    }

    private static int[] findKeyword(Alphabet alphabet, final double[][][] pp) {
        boolean[][][] shifts = new boolean[pp.length][][];
        float[] freq = alphabet.getFrequency();
        double alpha = 1.0 / freq.length;
        for (int i = 0; i < pp.length; ++i) {
            shifts[i] = new boolean[pp.length][];
            for (int j = 0; j < pp.length; ++j) {
                if (i == j) {
                    continue;
                }
                shifts[i][j] = new boolean[freq.length];
                for (int k = 0; k < freq.length; ++k) {
                    shifts[i][j][k] = pp[i][j][k] > alpha;
                }
            }
        }
        for (; ; ) {
            System.out.println("========================");
            int changed = 0;
            for (int i = 0; i < pp.length; ++i) {
                for (int j = 0; j < pp.length; ++j) {
                    if (i == j) {
                        continue;
                    }
                    for (int k = 0; k < freq.length; ++k) {
                        if (!shifts[i][j][k]) {
                            continue;
                        }

                        int rate = 0;
                        for (int a = 0; a < pp.length; ++a) {
                            for (int l = 0; l < freq.length; ++l) {
                                if ((a == i) || (a == j)) {
                                    continue;
                                }
                                if (!shifts[a][i][l]) {
                                    continue;
                                }
                                if (!shifts[i][a][(freq.length - l) % freq.length]) {
                                    continue;
                                }
                                if (!shifts[a][j][(l + k) % freq.length]) {
                                    continue;
                                }
                                if (!shifts[j][a][(freq.length * 2 - l - k) % freq.length]) {
                                    continue;
                                }
                                rate++;
                            }
                        }
                        //System.out.println(i + ":" + j + ":" + k + " = " + rate + (rate == 0 ? " !!!" : ""));
                        if (rate == 0) {
                            shifts[i][j][k] = false;
                            changed++;
                            break;
                        }
                    }
                }
            }
            System.out.println("Pass: " + changed);
            if (changed == 0) {
                break;
            }
        }
        int minP = Integer.MAX_VALUE;
        int offsets[][] = new int[pp.length][];

        for (int a = 0; a < pp.length; ++a) {
            int x = 0;
            int p = 1;
            System.out.println("");
            for (int i = 0; i < pp.length; ++i) {
                StringBuilder sb = new StringBuilder("");
                sb.append(i).append(": ");
                if (shifts[a][i] != null) {
                    int y = 0;
                    for (int j = 0; j < freq.length; ++j) {
                        if (shifts[a][i][j]) {
                            sb.append(j).append(",");
                            x++;
                            y++;
                        }
                    }
                    p *= y;
                } else {
                    sb.append("-");
                }
                System.out.println(sb.toString());
            }
            System.out.println(x + " - " + p);

            //p = a; // todo: xxx
            if (p < minP) {
                minP = p;
                for (int i = 0; i < pp.length; ++i) {
                    if (a == i) {
                        offsets[i] = new int[]{0};
                        continue;
                    }
                    int[] offset = new int[freq.length];
                    int len = 0;
                    for (int j = 0; j < freq.length; ++j) {
                        if (shifts[a][i][j]) {
                            offset[len] = j;
                            len++;
                        }
                    }
                    offsets[i] = Arrays.copyOf(offset, len);
                }
            }
        }


        int[] result = new int[pp.length];
        int index[] = new int[pp.length];
        double maxP = 0;
        for (; ; ) {
            double p = 1.0;

            for (int i = 0; i < pp.length; ++i) {
                for (int j = 0; j < pp.length; ++j) {
                    if (i != j) {
                        int a = offsets[i][index[i]];
                        int b = offsets[j][index[j]];
                        int c = (freq.length + b - a) % freq.length;
                        p *= pp[i][j][c];
                    }
                }
            }

            if (maxP < p) {
                maxP = p;
                for (int i = 0; i < index.length; ++i) {
                    result[i] = (freq.length + offsets[i][index[i]] - offsets[0][index[0]]) % freq.length;
                }
                System.out.println(Arrays.toString(result) + ": " + p);
            }

            int i = 0;
            while (i < index.length) {
                index[i]++;
                if (index[i] < offsets[i].length) {
                    break;
                }
                index[i] = 0;
                ++i;
            }
            if (i >= index.length) {
                break;
            }
        }
        return result;
    }

    private static void findWords(String text1) {
        boolean[] mark = new boolean[text1.length()];
        for (int l = 10; l >= 4; --l) {
            for (int i = 0; i < text1.length() - l * 2; ++i) {
                if (mark[i]) continue;
                for (int j = i + l; j < text1.length() - l; ++j) {
                    if (mark[j]) continue;
                    boolean ok = true;
                    for (int k = 0; k < l; ++k) {
                        if (text1.charAt(i + k) != text1.charAt(j + k)) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) {
                        //System.out.println(text1.substring(i, i + l));
                        for (int k = 0; k < l; ++k) {
                            mark[i + k] = true;
                            mark[j + k] = true;
                        }
                    }
                }
            }
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
        final float[] frequencies = new float[frequencyList.size()];
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
