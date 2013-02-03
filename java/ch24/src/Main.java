import ch24.decrypt.Alphabet;
import ch24.decrypt.Vigenere;

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
        /*try (InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("crypto.txt"), StandardCharsets.UTF_8)) {
            int[] crypto = loadCrypto(reader, alphabet);
            for (int i = 1; i <= 20; ++i) {
                float hit = checkKeywordLength(crypto, alphabet, i);
                if (hit > hitLine) {
                    System.out.println(i + ": " + hit);
                    getFreqKeyword(crypto, alphabet, i);
                }
            }
        }*/


        String text = "ВАРИАНТ RUNNING KEY (БЕГУЩИЙ КЛЮЧ) ШИФРА ВИЖЕНЕРА КОГДА-ТО БЫЛ НЕВЗЛАМЫВАЕМЫМ. ЭТА ВЕРСИЯ ИСПОЛЬЗУЕТ В КАЧЕСТВЕ КЛЮЧА БЛОК ТЕКСТА, РАВНЫЙ ПО ДЛИНЕ ИСХОДНОМУ ТЕКСТУ. ТАК КАК КЛЮЧ РАВЕН ПО ДЛИНЕ СООБЩЕНИЮ, ТО МЕТОДЫ ПРЕДЛОЖЕННЫЕ ФРИДМАНОМ И КАСИСКИ НЕ РАБОТАЮТ (ТАК КАК КЛЮЧ НЕ ПОВТОРЯЕТСЯ). В 1920 ГОДУ ФРИДМАН ПЕРВЫМ ОБНАРУЖИЛ НЕДОСТАТКИ ЭТОГО ВАРИАНТА. ПРОБЛЕМА С RUNNING KEY ШИФРА ВИЖЕНЕРА СОСТОИТ В ТОМ, ЧТО КРИПТОАНАЛИТИК ИМЕЕТ СТАТИСТИЧЕСКУЮ ИНФОРМАЦИЮ О КЛЮЧЕ (УЧИТЫВАЯ, ЧТО БЛОК ТЕКСТА НАПИСАН НА ИЗВЕСТНОМ ЯЗЫКЕ) И ЭТА ИНФОРМАЦИЯ БУДЕТ ОТРАЖАТЬСЯ В ШИФРОВАННОМ ТЕКСТЕ. ЕСЛИ КЛЮЧ ДЕЙСТВИТЕЛЬНО СЛУЧАЙНЫЙ, ЕГО ДЛИНА РАВНА ДЛИНЕ СООБЩЕНИЯ И ОН ИСПОЛЬЗОВАЛСЯ ЕДИНОЖДЫ, ТО ШИФР ВИЖЕНЕРА ТЕОРЕТИЧЕСКИ БУДЕТ НЕВЗЛАМЫВАЕМЫМ."
                + "ВИЖЕНЕР ФАКТИЧЕСКИ ИЗОБРЁЛ БОЛЕЕ СТОЙКИЙ ШИФР — ШИФР С АВТОКЛЮЧОМ. НЕСМОТРЯ НА ЭТО, «ШИФР ВИЖЕНЕРА» АССОЦИИРУЕТСЯ С БОЛЕЕ ПРОСТЫМ МНОГОАЛФАВИТНЫМ ШИФРОМ. ФАКТИЧЕСКИ ЭТИ ДВА ШИФРА ЧАСТО ПУТАЛИ, НАЗЫВАЯ ИХ LE CHIFFRE INDECHIFFRABLE. БЕББИДЖ ФАКТИЧЕСКИ ВЗЛОМАЛ БОЛЕЕ СТОЙКИЙ ШИФР С АВТОКЛЮЧОМ, В ТО ВРЕМЯ КОГДА КАСИСКИ ИЗДАЛ ПЕРВОЕ РЕШЕНИЕ ВЗЛОМА МНОГОАЛФАВИТНОГО ШИФРА С ФИКСИРОВАННЫМ КЛЮЧОМ. МЕТОД ВИЖЕНЕРА ЗАШИФРОВКИ И РАСШИФРОВКИ СООБЩЕНИЙ ИНОГДА ОТНОСИТСЯ К «ВАРИАНТУ БИТФОРДА». ЕГО ОТЛИЧИЕ ОТ ШИФРА БИТФОРДА, ИЗОБРЕТЕННОГО СЭРОМ ФРЕНСИСОМ БИТФОРДОМ, КОТОРЫЙ, ТЕМ НЕ МЕНЕЕ, ПОДОБЕН ШИФРУ ВИЖЕНЕРА, ЗАКЛЮЧАЕТСЯ В ИСПОЛЬЗОВАНИИ НЕМНОГО ИЗМЕНЕННОГО МЕХАНИЗМА ШИФРОВАНИЯ И ТАБЛИЦ."
                + "НЕСМОТРЯ НА ОЧЕВИДНУЮ СТОЙКОСТЬ ШИФРА ВИЖЕНЕРА, ОН ШИРОКО НЕ ИСПОЛЬЗОВАЛСЯ В ЕВРОПЕ. БОЛЬШЕЕ РАСПРОСТРАНЕНИЕ ПОЛУЧИЛ ШИФР ГРОНСФИЛДА, СОЗДАННЫЙ ГРАФОМ ГРОНСФИЛДОМ, ИДЕНТИЧНЫЙ ШИФРУ ВИЖЕНЕРА, ЗА ИСКЛЮЧЕНИЕМ ТОГО, ЧТО ОН ИСПОЛЬЗОВАЛ ТОЛЬКО 10 РАЗЛИЧНЫХ АЛФАВИТОВ (СООТВЕТСТВУЮЩИХ ЦИФРАМ ОТ 0 ДО 9). ПРЕИМУЩЕСТВО ШИФРА ГРОНСФИЛДА СОСТОИТ В ТОМ, ЧТО В КАЧЕСТВЕ КЛЮЧА ИСПОЛЬЗУЕТСЯ НЕ СЛОВО, А НЕДОСТАТОК — В НЕБОЛЬШОМ КОЛИЧЕСТВЕ АЛФАВИТОВ. ШИФР ГРОНСФИЛДА ШИРОКО ИСПОЛЬЗОВАЛСЯ ПО ВСЕЙ ГЕРМАНИИ И ЕВРОПЕ, НЕСМОТРЯ НА ЕГО НЕДОСТАТКИ.";
        String base = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
        String crypt = "ЗУШВЬЯЖЩКГЛФМДПЪЫНЮОСИЙТЧБАЭХЦЕР";
        String crypto = Vigenere.encrypt(base, crypt, "А", text);
        getFreqKeyword(Vigenere.strToIdx(Vigenere.createAlphabet(base), crypto), alphabet, 1);
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
        //result[2] -= 3;
        for (int i = 0; i < freq.length; ++i) {
            StringBuilder key = new StringBuilder();
            for (int j = 0; j < length; ++j) {
                key.append(alphabet.getAlphabet().charAt((freq.length - result[j] + i) % freq.length));
            }
            System.out.println(key.toString());
        }
        /*int[] abc = ;
        int[] text = Vigenere.decrypt(abc, result, crypto);
        String text1 = Vigenere.idxToStr(alphabet.getAlphabet(), text);
        System.out.println(text1);*/
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
