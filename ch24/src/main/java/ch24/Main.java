package ch24;

import ch24.decrypt.Alphabet;
import ch24.decrypt.Decrypter;
import ch24.decrypt.Vigenere;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        Alphabet alphabet = Alphabet.load(Main.class.getResourceAsStream("alphabet.txt"));

        try (InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("ch24/crypto_ru.txt"), StandardCharsets.UTF_8)) {
            String cryptoText = loadText(reader);
            int keywordLength = Decrypter.findKeywordLength(alphabet, cryptoText);
            if (keywordLength > 0) {
                try (InputStreamReader dict = new InputStreamReader(Main.class.getResourceAsStream("dict/ru.txt"), StandardCharsets.UTF_8)) {
                    String keyword = Decrypter.findKeywordDictionary(alphabet, cryptoText, keywordLength, dict);
                    String abc = Decrypter.findAlphabet(alphabet, cryptoText, keyword, "");
                    String text = Vigenere.decrypt(alphabet.getAlphabet(), abc, keyword, cryptoText);
                    System.out.println(text);
                }

                String keyword = Decrypter.findKeywordMath(alphabet, cryptoText, keywordLength);
                String abc = Decrypter.findAlphabet(alphabet, cryptoText, keyword, "");
                String text = Vigenere.decrypt(alphabet.getAlphabet(), abc, keyword, cryptoText);
                System.out.println(text);
            }
        }
    }

    private static String loadText(InputStreamReader reader) throws IOException {
        final StringBuilder text = new StringBuilder();
        final char[] buffer = new char[1024];
        while (true) {
            int readed = reader.read(buffer);
            if (readed < 0) {
                break;
            }
            text.append(buffer, 0, readed);
        }
        return text.toString();
    }

    private static void getFreqKeyword(Alphabet alphabet, String cryptoText, int length) {
        // ЕДЧНИЦАКОМЗИЛГЦИЧЯООЧЕПОЫЕАДАМКНЫТЦЧПРПГРАММНЧХМЗГМЕНООВТОТКАКАЗДЯЙПРОГРИММЕЦЙТЕГМЫНТЕСТЬЛИБОАЛАСНАЗПРТГРАММАЛИБОЯНЕШЕГХПРПУЫМПРИТТЧКАЯСЕСЫЯМЕНТКЫМИНЧЦКАОМПЧЛЯЦИИТВЬЗЭВАЕУУРУЯСДРОГОЫЯВАРЫЗПИАОУНАКОНЕПБЯДАКЕЛЬЕОФКОБКССЕТЕГМЫНТЫНОЮЕЦЕДБЬПОЛНОЙЭАГРПЦКИЕОЫУИБИРОВАЛЧСЬСЫЕТУЕООПКВПРИЗАГРЛЭЕЫМОЛЖНАПРИТЛТСТВОЯИТЬРОВНОПДЕАГЛИВНАГУРОГРАЫМВИБЗАЧКВЖУАЯЯЖВВНАЯЕРОАРАНЫАИМЕНОВАНИИЗАКРЫВАФХЗЕИМЬДОЛЗНОСТСЕАДИТЬМОТАРЧВАЮЩИМТТЧКАКЕЛТПРОГРАМНЦУОДПБЕОБОБДШИНСКВОДРУГИХСГРУУПЧРТСАННЯХИЕСТРЛКУИЙЕТУДОЫЛОТЕГМЫНТВЧМТЖЗТТОУЖРЖАКЬВСЫПТЙОБКЧНОТВПЙМТВЕННТПРОГРАМНАМКОЫЕАДАМЕТИНТААЖЕЧТОЯВРЕЗЫРВИРПВАННЫЕСЛОВАИДЕНТИЬИКАООРЫИКПНМТАНУЧНЫМОЛЖНКРАЯРЯВАТЬСЗНАГРАНИЦАХЗАУИСЕЙТОФАИИХМЛЕДЛЕООТДЕБЬОЬМРУАПОДРПГИЗРОБЕЛИМИЗНААИМИОЕЕРИЦИЙКОММЕНТАРИЫМИИЛИГРАНИЧАМЧЭАПИСЕЙВСЫХАРЧ
        // ЕДЧНИЦАКОМЗИЛГЦИЧЯООЧЕПОЫЕАДАМКНЫТЦЧПРПГРАММНЧХМЗГМЕНООВТОТКАКАЗДЯЙПРОГРИММЕЦЙТЕГМЫНТЕСТЬЛИБОАЛАСНАЗПРТГРАММАЛИБОЯНЕШЕГХПРПУЫМПРИТТЧКАЯСЕСЫЯМЕНТКЫМИНЧЦКАОМПЧЛЯЦИИТВЬЗЭВАЕУУРУЯСДРОГОЫЯВАРЫЗПИАОУНАКОНЕПБЯДАКЕЛЬЕОФКОБКССЕТЕГМЫНТЫНОЮЕЦЕДБЬПОЛНОЙЭАГРПЦКИЕОЫУИБИРОВАЛЧСЬСЫЕТУЕООПКВПРИЗАГРЛЭЕЫМОЛЖНАПРИТЛТСТВОЯИТЬРОВНОПДЕАГЛИВНАГУРОГРАЫМВИБЗАЧКВЖУАЯЯЖВВНАЯЕРОАРАНЫАИМЕНОВАНИИЗАКРЫВАФХЗЕИМЬДОЛЗНОСТСЕАДИТЬМОТАРЧВАЮЩИМТТЧКАКЕЛТПРОГРАМНЦУОДПБЕОБОБДШИНСКВОДРУГИХСГРУУПЧРТСАННЯХИЕСТРЛКУИЙЕТУДОЫЛОТЕГМЫНТВЧМТЖЗТТОУЖРЖАКЬВСЫПТЙОБКЧНОТВПЙМТВЕННТПРОГРАМНАМКОЫЕАДАМЕТИНТААЖЕЧТОЯВРЕЗЫРВИРПВАННЫЕСЛОВАИДЕНТИЬИКАООРЫИКПНМТАНУЧНЫМОЛЖНКРАЯРЯВАТЬСЗНАГРАНИЦАХЗАУИСЕЙТОФАИИХМЛЕДЛЕООТДЕБЬОЬМРУАПОДРПГИЗРОБЕЛИМИЗНААИМИОЕЕРИЦИЙКОММЕНТАРИЫМИИЛИГРАНИЧАМЧЭАПИСЕЙВСЫХАРЧ
        double[] freq = alphabet.getFrequency();

        int[] resultWord = Vigenere.strToIdx(alphabet.getAlphabet(), Decrypter.findKeywordMath(alphabet, cryptoText, length));
        System.out.println();
        int[][] words = new int[][]{
                resultWord
        };
        for (int[] word : words) {
            System.out.println("===========");
            System.out.println(Arrays.toString(word));
            for (int i = 0; i < freq.length; ++i) {
                StringBuilder key = new StringBuilder();
                float x = 0;
                for (int j = 0; j < word.length; ++j) {
                    int charIndex = (freq.length - word[j] + i) % freq.length;
                    key.append(alphabet.getAlphabet().charAt(charIndex));
                    x += freq[charIndex] / word.length;
                }
                //if (x > 0.035) {
                System.out.println(key.toString() + ": " + x);
                //}
            }
        }


        int[] keyword = Vigenere.strToIdx(Vigenere.createAlphabet(alphabet.getAlphabet()), "РЕДИСКА");
        for (int i = 0; i < resultWord.length; ++i) {
            resultWord[i] = (freq.length - keyword[i]) % freq.length;
        }

        // ЕХЭЦРБТИОНЪДФГЩЯВУЗШЬЖКЛМПЫЮСЙЧА
        String abc = Decrypter.findAlphabet(alphabet, cryptoText, "РЕДИСКА", "ЕХЭЦРБТИОНЪДФГЩЯВУЗШЬЖКЛМПЫЮСЙЧА");
        String text1 = Vigenere.decrypt(alphabet.getAlphabet(),
                abc,
                Vigenere.idxToStr(alphabet.getAlphabet(), keyword),
                cryptoText
        );
        for (String word : Vigenere.findWords(text1, 4)) {
            System.out.println(word);
        }
        System.out.println(Vigenere.idxToStr(alphabet.getAlphabet(), keyword));
        System.out.println(abc);
        System.out.println(Vigenere.normalize(alphabet.getAlphabet(), cryptoText));
        System.out.println(
                text1
        );
    }
}
