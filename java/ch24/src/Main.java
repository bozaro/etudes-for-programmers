import ch24.decrypt.Alphabet;
import ch24.decrypt.Vigenere;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        if (true) return;


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


        String text = "ВАРИАНТ RUNNING KEY (БЕГУЩИЙ КЛЮЧ) ШИФРА ВИЖЕНЕРА КОГДА-ТО БЫЛ НЕВЗЛАМЫВАЕМЫМ. ЭТА ВЕРСИЯ ИСПОЛЬЗУЕТ В КАЧЕСТВЕ КЛЮЧА БЛОК ТЕКСТА, РАВНЫЙ ПО ДЛИНЕ ИСХОДНОМУ ТЕКСТУ. ТАК КАК КЛЮЧ РАВЕН ПО ДЛИНЕ СООБЩЕНИЮ, ТО МЕТОДЫ ПРЕДЛОЖЕННЫЕ ФРИДМАНОМ И КАСИСКИ НЕ РАБОТАЮТ (ТАК КАК КЛЮЧ НЕ ПОВТОРЯЕТСЯ). В 1920 ГОДУ ФРИДМАН ПЕРВЫМ ОБНАРУЖИЛ НЕДОСТАТКИ ЭТОГО ВАРИАНТА. ПРОБЛЕМА С RUNNING KEY ШИФРА ВИЖЕНЕРА СОСТОИТ В ТОМ, ЧТО КРИПТОАНАЛИТИК ИМЕЕТ СТАТИСТИЧЕСКУЮ ИНФОРМАЦИЮ О КЛЮЧЕ (УЧИТЫВАЯ, ЧТО БЛОК ТЕКСТА НАПИСАН НА ИЗВЕСТНОМ ЯЗЫКЕ) И ЭТА ИНФОРМАЦИЯ БУДЕТ ОТРАЖАТЬСЯ В ШИФРОВАННОМ ТЕКСТЕ. ЕСЛИ КЛЮЧ ДЕЙСТВИТЕЛЬНО СЛУЧАЙНЫЙ, ЕГО ДЛИНА РАВНА ДЛИНЕ СООБЩЕНИЯ И ОН ИСПОЛЬЗОВАЛСЯ ЕДИНОЖДЫ, ТО ШИФР ВИЖЕНЕРА ТЕОРЕТИЧЕСКИ БУДЕТ НЕВЗЛАМЫВАЕМЫМ."
                + "ВИЖЕНЕР ФАКТИЧЕСКИ ИЗОБРЁЛ БОЛЕЕ СТОЙКИЙ ШИФР — ШИФР С АВТОКЛЮЧОМ. НЕСМОТРЯ НА ЭТО, «ШИФР ВИЖЕНЕРА» АССОЦИИРУЕТСЯ С БОЛЕЕ ПРОСТЫМ МНОГОАЛФАВИТНЫМ ШИФРОМ. ФАКТИЧЕСКИ ЭТИ ДВА ШИФРА ЧАСТО ПУТАЛИ, НАЗЫВАЯ ИХ LE CHIFFRE INDECHIFFRABLE. БЕББИДЖ ФАКТИЧЕСКИ ВЗЛОМАЛ БОЛЕЕ СТОЙКИЙ ШИФР С АВТОКЛЮЧОМ, В ТО ВРЕМЯ КОГДА КАСИСКИ ИЗДАЛ ПЕРВОЕ РЕШЕНИЕ ВЗЛОМА МНОГОАЛФАВИТНОГО ШИФРА С ФИКСИРОВАННЫМ КЛЮЧОМ. МЕТОД ВИЖЕНЕРА ЗАШИФРОВКИ И РАСШИФРОВКИ СООБЩЕНИЙ ИНОГДА ОТНОСИТСЯ К «ВАРИАНТУ БИТФОРДА». ЕГО ОТЛИЧИЕ ОТ ШИФРА БИТФОРДА, ИЗОБРЕТЕННОГО СЭРОМ ФРЕНСИСОМ БИТФОРДОМ, КОТОРЫЙ, ТЕМ НЕ МЕНЕЕ, ПОДОБЕН ШИФРУ ВИЖЕНЕРА, ЗАКЛЮЧАЕТСЯ В ИСПОЛЬЗОВАНИИ НЕМНОГО ИЗМЕНЕННОГО МЕХАНИЗМА ШИФРОВАНИЯ И ТАБЛИЦ."
                + "НЕСМОТРЯ НА ОЧЕВИДНУЮ СТОЙКОСТЬ ШИФРА ВИЖЕНЕРА, ОН ШИРОКО НЕ ИСПОЛЬЗОВАЛСЯ В ЕВРОПЕ. БОЛЬШЕЕ РАСПРОСТРАНЕНИЕ ПОЛУЧИЛ ШИФР ГРОНСФИЛДА, СОЗДАННЫЙ ГРАФОМ ГРОНСФИЛДОМ, ИДЕНТИЧНЫЙ ШИФРУ ВИЖЕНЕРА, ЗА ИСКЛЮЧЕНИЕМ ТОГО, ЧТО ОН ИСПОЛЬЗОВАЛ ТОЛЬКО 10 РАЗЛИЧНЫХ АЛФАВИТОВ (СООТВЕТСТВУЮЩИХ ЦИФРАМ ОТ 0 ДО 9). ПРЕИМУЩЕСТВО ШИФРА ГРОНСФИЛДА СОСТОИТ В ТОМ, ЧТО В КАЧЕСТВЕ КЛЮЧА ИСПОЛЬЗУЕТСЯ НЕ СЛОВО, А НЕДОСТАТОК — В НЕБОЛЬШОМ КОЛИЧЕСТВЕ АЛФАВИТОВ. ШИФР ГРОНСФИЛДА ШИРОКО ИСПОЛЬЗОВАЛСЯ ПО ВСЕЙ ГЕРМАНИИ И ЕВРОПЕ, НЕСМОТРЯ НА ЕГО НЕДОСТАТКИ."
                + "ЗАМИНКИ НА ЭТАПЕ ВВОДА ДАННЫХ ДЛЯ КОРРЕКТНОГО ОФОРМЛЕНИЯ ЗАКАЗА МОГУТ ПРИВЕСТИ К ТОМУ, ЧТО ПОЛЬЗОВАТЕЛЬ ВОВСЕ ОТКАЖЕТСЯ ОТ ПОКУПКИ, ТАК КАК НЕТ НИЧЕГО БОЛЕЕ РАЗДРАЖАЮЩЕГО, ЧЕМ ПОСЛЕ НАЖАТИЯ КНОПКИ О ПОДТВЕРЖДЕНИИ ЗАКАЗА ОБНАРУЖИТЬ, ЧТО КАКИЕ-ТО ПОЛЯ БЫЛИ ЗАПОЛНЕНЫ НЕВЕРНО. ВСТРОЕННАЯ ПРЕДВАРИТЕЛЬНАЯ ПРОВЕРКА ПРАВИЛЬНОСТИ ЗАПОЛНЕНИЯ ПОЛЕЙ ЗНАЧИТЕЛЬНО УВЕЛИЧИТ ВЕРОЯТНОСТЬ ТОГО, ЧТО ВАШ КЛИЕНТ С ПЕРВОГО РАЗА ПРАВИЛЬНО ЗАПОЛНИТ ВСЕ НЕОБХОДИМЫЕ ФОРМЫ И ОСТАНЕТСЯ ДОВОЛЕН СЕРВИСОМ, А ВЫ, В СВОЮ ОЧЕРЕДЬ ПОЛУЧИТЕ КОРРЕКТНО ЗАПОЛНЕННЫЙ ЗАКАЗ И СНИЗИТЕ ВЕРОЯТНОСТЬ ОШИБОК.\n"
                + "ДОПОЛНИТЕЛЬНЫМ ПРЕИМУЩЕСТВОМ ДАННОЙ ФУНКЦИИ ТАКЖЕ ЯВЛЯЕТСЯ ВОЗМОЖНОСТЬ НАЛАДИТЬ ДИАЛОГ С ПОЛЬЗОВАТЕЛЕМ ПОСРЕДСТВОМ КОРОТКИХ СООБЩЕНИЙ РЯДОМ С ПОЛЯМИ (КАК КОРРЕКТНО ЗАПОЛНЕННЫМИ, ТАК И НЕТ), ЧТО УЛУЧШИТ ОБЩЕЕ ВПЕЧАТЛЕНИЕ О ВАШЕМ МАГАЗИНЕ И ЗАСТАВИТ ПОКУПАТЕЛЯ В ОЧЕРЕДНОЙ РАЗ ВОЗВРАЩАТЬСЯ ИМЕННО К ВАМ. СТОИТ, ОДНАКО, ДОБАВИТЬ ВАЖНОЕ ЗАМЕЧАНИЕ: КАК УПОМИНАЛОСЬ В СТАТЬЕ “THE STATE OF E-COMMERCE CHECKOUT DESIGN 2012” В ЖУРНАЛЕ SMASHING MAGAZINE, ПРОВЕРКА КОРРЕКТНОСТИ ПОЛЕЙ С АДРЕСАМИ НЕ ВСЕГДА ЯВЛЯЕТСЯ ПОДХОДЯЩИМ РЕШЕНИЕМ. ПРИЧИНА В ТОМ, ЧТО САЙТЫ, КОТОРЫЕ ОТКАЗЫВАЮТ ПОЛЬЗОВАТЕЛЮ В ОФОРМЛЕНИИ ЗАКАЗА ИЗ-ЗА НЕПОДХОДЯЩЕГО ФОРМАТА АДРЕСА, ОДНОЗНАЧНО БУДУТ ТЕРЯТЬ КЛИЕНТОВ. ИМЕННО ПОЭТОМУ ПРОВЕРКОЙ ПОЛЕЙ С АДРЕСАМИ ЛУЧШЕ ПРЕНЕБРЕЧЬ.\n"
                + "А ВОТ ГДЕ КОРРЕКТНОСТЬ ДЕЙСТВИТЕЛЬНО ВАЖНА, ТАК ЭТО В ПОЛЯХ С АДРЕСОМ ЭЛЕКТРОННОЙ ПОЧТЫ, НОМЕРОМ БАНКОВСКОЙ КАРТЫ, ZIP КОДОМ И Т.Д." +
                "НЕКОТОРЫЕ ИНТЕРНЕТ-МАГАЗИНЫ НЕ ТОРОПЯТСЯ ОТПРАВЛЯТЬ ПОКУПАТЕЛЯ К ОФОРМЛЕНИЮ ЗАКАЗА СРАЗУ ПОСЛЕ ДОБАВЛЕНИЯ ТОВАРА В КОРЗИНУ И ПРАВИЛЬНО ДЕЛАЮТ. ЭТО ДАЕТ ПОЛЬЗОВАТЕЛЮ СТИМУЛ К ПРОДОЛЖЕНИЮ ВИРТУАЛЬНОГО ШОПИНГА И, В КОНЦЕ КОНЦОВ, УВЕЛИЧИВАЕТ ДОХОДЫ МАГАЗИНА. КАК РАЗ ЗДЕСЬ АНИМИРОВАННАЯ КОРЗИНА И ВСТУПАЕТ В ИГРУ. ЭТО ЭФФЕКТИВНЫЙ СПОСОБ ДАТЬ ПОЛЬЗОВАТЕЛЮ ПОДТВЕРЖДЕНИЕ, ЧТО ВЫБРАННЫЙ ТОВАР УЖЕ ПОМЕЩЕН В ЕГО КОРЗИНУ И ЧТО В ЛЮБОЙ МОМЕНТ ОН МОЖЕТ ЗАВЕРШИТЬ ОФОРМЛЕНИЕ ЗАКАЗА, ПРИ ЭТОМ, НЕ ОТВЛЕКАЯ ЕГО ОТ ДАЛЬНЕЙШЕГО ШОПИНГА.\n" +
                "ИНТЕРНЕТ-МАГАЗИН AMERICAN EAGLE РАСКРЫВАЕТ ПЕРЕД ПОЛЬЗОВАТЕЛЕМ СПЕЦИАЛЬНУЮ ОБЛАСТЬ В НИЖНЕЙ ЧАСТИ ЭКРАНА, В КОТОРОЙ ОТОБРАЖАЕТСЯ ДОБАВЛЕННЫЙ В КОРЗИНУ ТОВАР, ДЕТАЛИ ТРАНЗАКЦИИ И КНОПКА СОВЕРШЕНИЯ ЗАКАЗА." +
                "КАК ТОЛЬКО ДЛИНА КЛЮЧА СТАНОВИТСЯ ИЗВЕСТНОЙ, ЗАШИФРОВАННЫЙ ТЕКСТ МОЖНО ЗАПИСАТЬ ВО МНОЖЕСТВО СТОЛБЦОВ, КАЖДЫЙ ИЗ КОТОРЫХ СООТВЕТСТВУЕТ ОДНОМУ СИМВОЛУ КЛЮЧА. КАЖДЫЙ СТОЛБЕЦ СОСТОИТ ИЗ ИСХОДНОГО ТЕКСТА, КОТОРЫЙ ЗАШИФРОВАН ШИФРОМ ЦЕЗАРЯ; КЛЮЧ К ШИФРУ ЦЕЗАРЯ ЯВЛЯЕТСЯ ВСЕГО-НАВСЕГО ОДНИМ СИМВОЛОМ КЛЮЧА ДЛЯ ШИФРА ВИЖЕНЕРА, КОТОРЫЙ ИСПОЛЬЗУЕТСЯ В ЭТОМ СТОЛБЦЕ. ИСПОЛЬЗУЯ МЕТОДЫ, ПОДОБНЫЕ МЕТОДАМ ВЗЛОМА ШИФРА ЦЕЗАРЯ, МОЖНО РАСШИФРОВАТЬ ЗАШИФРОВАННЫЙ ТЕКСТ. УСОВЕРШЕНСТВОВАНИЕ ТЕСТА КАСИСКИ, ИЗВЕСТНОЕ КАК МЕТОД КИРХГОФА, ЗАКЛЮЧАЕТСЯ В СРАВНЕНИИ ЧАСТОТЫ ПОЯВЛЕНИЯ СИМВОЛОВ В СТОЛБЦАХ С ЧАСТОТОЙ ПОЯВЛЕНИЯ СИМВОЛОВ В ИСХОДНОМ ТЕКСТЕ ДЛЯ НАХОЖДЕНИЯ КЛЮЧЕВОГО СИМВОЛА ДЛЯ ЭТОГО СТОЛБЦА. КОГДА ВСЕ СИМВОЛЫ КЛЮЧА ИЗВЕСТНЫ, КРИПТОАНАЛИТИК МОЖЕТ ЛЕГКО РАСШИФРОВАТЬ ШИФРОВАННЫЙ ТЕКСТ, ПОЛУЧИВ ИСХОДНЫЙ ТЕКСТ. МЕТОД КИРХГОФА НЕ ПРИМЕНИМ, КОГДА ТАБЛИЦА ВИЖЕНЕРА СКРЕМБЛИРОВАНА, ВМЕСТО ИСПОЛЬЗОВАНИЯ ОБЫЧНОЙ АЛФАВИТНОЙ ПОСЛЕДОВАТЕЛЬНОСТИ, ХОТЯ ТЕСТ КАСИСКИ И ТЕСТЫ СОВПАДЕНИЯ ВСЁ ЕЩЁ МОГУТ ИСПОЛЬЗОВАТЬСЯ ДЛЯ ОПРЕДЕЛЕНИЯ ДЛИНЫ КЛЮЧА ДЛЯ ЭТОГО СЛУЧАЯ."
                        .replaceAll("Ё", "Е");
        findWords(Vigenere.encrypt("АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", "А", text));
        // ОЕАИТНСВРЛКМДПЗЯЧФУЫЬГБШЙЖЮХЦЭЩЪ
        // ОЕАИНТСРВЛКМДПУЯЫЬГЗБЧЙХЖШЮЦЩЭФЪ
        // ОАЕИНТРСЛМВПКДЯЫБЗУГЬЧЙХЦЖЮЩФЭШЪ

        // ОЕАИНТСРВЛКМДПУЯЫЬГЗБЧЙХЖШЮЦЩЭФЪ
        // оеанитслвркдмумяьыгбчзжйшхюэцщфъ
        String base = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        String crypt = "ЗУШВЬЯЖЩКГЛФМДПЪЫНЮОСИЙТЧБАЭХЦЕР";
        //crypt = base;
        String crypto = Vigenere.encrypt(base, crypt, "ЛИСП", text);
        getFreqKeyword(Vigenere.strToIdx(Vigenere.createAlphabet(base), crypto), alphabet, 4);
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
        // todo: Хак
        if (result.length > 3) {
            //result[2] -= 3;
        }
        for (int i = 0; i < freq.length; ++i) {
            StringBuilder key = new StringBuilder();
            float x = 0;
            for (int j = 0; j < length; ++j) {
                int charIndex = (freq.length - result[j] + i) % freq.length;
                key.append(alphabet.getAlphabet().charAt(charIndex));
                x += freq[charIndex] / length;
            }
            if (x > 0.035) {
                System.out.println(key.toString() + ": " + x);
            }
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
                        System.out.println(text1.substring(i, i + l));
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
