package ch24.decrypt;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Проверка расшибфровки текста.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class DecrypterTest {
    private static final String CRYPT = "ЗУШВЬЯЖЩКГЛФМДПЪЫНЮОСИЙТЧБАЭХЦЕР";
    private static final String TEXT = (
            "ВАРИАНТ RUNNING KEY (БЕГУЩИЙ КЛЮЧ) ШИФРА ВИЖЕНЕРА КОГДА-ТО БЫЛ НЕВЗЛАМЫВАЕМЫМ. ЭТА ВЕРСИЯ ИСПОЛЬЗУЕТ В " +
                    "КАЧЕСТВЕ КЛЮЧА БЛОК ТЕКСТА, РАВНЫЙ ПО ДЛИНЕ ИСХОДНОМУ ТЕКСТУ. ТАК КАК КЛЮЧ РАВЕН ПО ДЛИНЕ СООБЩЕНИЮ, ТО " +
                    "МЕТОДЫ ПРЕДЛОЖЕННЫЕ ФРИДМАНОМ И КАСИСКИ НЕ РАБОТАЮТ (ТАК КАК КЛЮЧ НЕ ПОВТОРЯЕТСЯ). В 1920 ГОДУ ФРИДМАН ПЕРВЫМ " +
                    "ОБНАРУЖИЛ НЕДОСТАТКИ ЭТОГО ВАРИАНТА. ПРОБЛЕМА С RUNNING KEY ШИФРА ВИЖЕНЕРА СОСТОИТ В ТОМ, ЧТО КРИПТОАНАЛИТИК " +
                    "ИМЕЕТ СТАТИСТИЧЕСКУЮ ИНФОРМАЦИЮ О КЛЮЧЕ (УЧИТЫВАЯ, ЧТО БЛОК ТЕКСТА НАПИСАН НА ИЗВЕСТНОМ ЯЗЫКЕ) И ЭТА ИНФОРМАЦИЯ " +
                    "БУДЕТ ОТРАЖАТЬСЯ В ШИФРОВАННОМ ТЕКСТЕ. ЕСЛИ КЛЮЧ ДЕЙСТВИТЕЛЬНО СЛУЧАЙНЫЙ, ЕГО ДЛИНА РАВНА ДЛИНЕ СООБЩЕНИЯ И ОН " +
                    "ИСПОЛЬЗОВАЛСЯ ЕДИНОЖДЫ, ТО ШИФР ВИЖЕНЕРА ТЕОРЕТИЧЕСКИ БУДЕТ НЕВЗЛАМЫВАЕМЫМ." +
                    "А ВОТ ГДЕ КОРРЕКТНОСТЬ ДЕЙСТВИТЕЛЬНО ВАЖНА, ТАК ЭТО В ПОЛЯХ С АДРЕСОМ ЭЛЕКТРОННОЙ ПОЧТЫ, НОМЕРОМ БАНКОВСКОЙ КАРТЫ," +
                    " ZIP КОДОМ И Т.Д. НЕКОТОРЫЕ ИНТЕРНЕТ-МАГАЗИНЫ НЕ ТОРОПЯТСЯ ОТПРАВЛЯТЬ ПОКУПАТЕЛЯ К ОФОРМЛЕНИЮ ЗАКАЗА СРАЗУ ПОСЛЕ " +
                    "ДОБАВЛЕНИЯ ТОВАРА В КОРЗИНУ И ПРАВИЛЬНО ДЕЛАЮТ. ЭТО ДАЕТ ПОЛЬЗОВАТЕЛЮ СТИМУЛ К ПРОДОЛЖЕНИЮ ВИРТУАЛЬНОГО ШОПИНГА И, " +
                    "В КОНЦЕ КОНЦОВ, УВЕЛИЧИВАЕТ ДОХОДЫ МАГАЗИНА. КАК РАЗ ЗДЕСЬ АНИМИРОВАННАЯ КОРЗИНА И ВСТУПАЕТ В ИГРУ. ЭТО ЭФФЕКТИВНЫЙ " +
                    "СПОСОБ ДАТЬ ПОЛЬЗОВАТЕЛЮ ПОДТВЕРЖДЕНИЕ, ЧТО ВЫБРАННЫЙ ТОВАР УЖЕ ПОМЕЩЕН В ЕГО КОРЗИНУ И ЧТО В ЛЮБОЙ МОМЕНТ ОН МОЖЕТ " +
                    "ЗАВЕРШИТЬ ОФОРМЛЕНИЕ ЗАКАЗА, ПРИ ЭТОМ, НЕ ОТВЛЕКАЯ ЕГО ОТ ДАЛЬНЕЙШЕГО ШОПИНГА.\n" +
                    "ИНТЕРНЕТ-МАГАЗИН AMERICAN EAGLE РАСКРЫВАЕТ ПЕРЕД ПОЛЬЗОВАТЕЛЕМ СПЕЦИАЛЬНУЮ ОБЛАСТЬ В НИЖНЕЙ ЧАСТИ ЭКРАНА, В КОТОРОЙ " +
                    "ОТОБРАЖАЕТСЯ ДОБАВЛЕННЫЙ В КОРЗИНУ ТОВАР, ДЕТАЛИ ТРАНЗАКЦИИ И КНОПКА СОВЕРШЕНИЯ ЗАКАЗА. КАК ТОЛЬКО ДЛИНА КЛЮЧА " +
                    "СТАНОВИТСЯ ИЗВЕСТНОЙ, ЗАШИФРОВАННЫЙ ТЕКСТ МОЖНО ЗАПИСАТЬ ВО МНОЖЕСТВО СТОЛБЦОВ, КАЖДЫЙ ИЗ КОТОРЫХ СООТВЕТСТВУЕТ ОДНОМУ " +
                    "СИМВОЛУ КЛЮЧА. КАЖДЫЙ СТОЛБЕЦ СОСТОИТ ИЗ ИСХОДНОГО ТЕКСТА, КОТОРЫЙ ЗАШИФРОВАН ШИФРОМ ЦЕЗАРЯ; КЛЮЧ К ШИФРУ ЦЕЗАРЯ " +
                    "ЯВЛЯЕТСЯ ВСЕГО-НАВСЕГО ОДНИМ СИМВОЛОМ КЛЮЧА ДЛЯ ШИФРА ВИЖЕНЕРА, КОТОРЫЙ ИСПОЛЬЗУЕТСЯ В ЭТОМ СТОЛБЦЕ. ИСПОЛЬЗУЯ МЕТОДЫ, " +
                    "ПОДОБНЫЕ МЕТОДАМ ВЗЛОМА ШИФРА ЦЕЗАРЯ, МОЖНО РАСШИФРОВАТЬ ЗАШИФРОВАННЫЙ ТЕКСТ. УСОВЕРШЕНСТВОВАНИЕ ТЕСТА КАСИСКИ, " +
                    "ИЗВЕСТНОЕ КАК МЕТОД КИРХГОФА, ЗАКЛЮЧАЕТСЯ В СРАВНЕНИИ ЧАСТОТЫ ПОЯВЛЕНИЯ СИМВОЛОВ В СТОЛБЦАХ С ЧАСТОТОЙ ПОЯВЛЕНИЯ " +
                    "СИМВОЛОВ В ИСХОДНОМ ТЕКСТЕ ДЛЯ НАХОЖДЕНИЯ КЛЮЧЕВОГО СИМВОЛА ДЛЯ ЭТОГО СТОЛБЦА. КОГДА ВСЕ СИМВОЛЫ КЛЮЧА ИЗВЕСТНЫ, " +
                    "КРИПТОАНАЛИТИК МОЖЕТ ЛЕГКО РАСШИФРОВАТЬ ШИФРОВАННЫЙ ТЕКСТ, ПОЛУЧИВ ИСХОДНЫЙ ТЕКСТ. МЕТОД КИРХГОФА НЕ ПРИМЕНИМ, КОГДА " +
                    "ТАБЛИЦА ВИЖЕНЕРА СКРЕМБЛИРОВАНА, ВМЕСТО ИСПОЛЬЗОВАНИЯ ОБЫЧНОЙ АЛФАВИТНОЙ ПОСЛЕДОВАТЕЛЬНОСТИ, ХОТЯ ТЕСТ КАСИСКИ И ТЕСТЫ " +
                    "СОВПАДЕНИЯ ВСЁ ЕЩЁ МОГУТ ИСПОЛЬЗОВАТЬСЯ ДЛЯ ОПРЕДЕЛЕНИЯ ДЛИНЫ КЛЮЧА ДЛЯ ЭТОГО СЛУЧАЯ."
    ).replaceAll("Ё", "Е");

    @Test
    public void testKeywordLength() throws IOException {
        Alphabet alphabet = Alphabet.load(getClass().getResourceAsStream("/alphabet.txt"));
        String key = "АЛЬФА";
        String crypto = Vigenere.encrypt(alphabet.getAlphabet(), CRYPT, key, TEXT);
        Assert.assertEquals(key.length(), Decrypter.findKeywordLength(alphabet, crypto));
    }

    @Test
    public void testKeywordDictionary() throws IOException {
        Alphabet alphabet = Alphabet.load(getClass().getResourceAsStream("/alphabet.txt"));
        String key = "АЛЬФА";
        String crypto = Vigenere.encrypt(alphabet.getAlphabet(), CRYPT, key, TEXT);
        Assert.assertEquals(key, Decrypter.findKeywordDictionary(alphabet, crypto, key.length(), getClass().getResourceAsStream("/dict/ru.txt")));
    }

    @Test
    public void testKeywordMath() throws IOException {
        Alphabet alphabet = Alphabet.load(getClass().getResourceAsStream("/alphabet.txt"));
        String key = "АЛЬФА";
        String crypto = Vigenere.encrypt(alphabet.getAlphabet(), CRYPT, key, TEXT);
        Assert.assertEquals(key, Decrypter.findKeywordMath(alphabet, crypto, key.length()));
    }

    @Test
    public void testFindAlphabet() throws IOException {
        Alphabet alphabet = Alphabet.load(getClass().getResourceAsStream("/alphabet.txt"));
        String key = "АЛЬФА";
        String crypto = Vigenere.encrypt(alphabet.getAlphabet(), CRYPT, key, TEXT);

        String result = Decrypter.findAlphabet(alphabet, crypto, key);
        Assert.assertEquals(CRYPT.length(), result.length());
        int miss = 0;
        StringBuilder hint = new StringBuilder();
        for (int i = 0; i < result.length(); ++i) {
            if (CRYPT.charAt(i) != result.charAt(i)) {
                miss++;
                hint.append(CRYPT.charAt(i));
            } else {
                hint.append(' ');
            }
        }
        Assert.assertTrue(miss <= 6);

        Assert.assertEquals(CRYPT, Decrypter.findAlphabet(alphabet, crypto, key, hint.toString()));
    }
}
