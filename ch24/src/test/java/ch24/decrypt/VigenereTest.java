package ch24.decrypt;

import org.junit.Assert;
import org.junit.Test;

/**
 * Проверка шифра Вижнера.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class VigenereTest {
    private final static String ABC_BASE = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private final static String ABC_CRYPT = "ЗУШВЬЯЖЩКГЛФМДПЪЫНЮОСИЙТЧБАЭХЦЕР";
    private final static String ABC_KEY = "ЛИСП";

    @Test
    public void encrypt() {
        String crypto = Vigenere.encrypt(ABC_BASE, ABC_CRYPT, ABC_KEY, "ПОПРОБУЙТЕ ПРОЧИТАТЬ КРИПТОГРАММУ ТОЧКА");
        Assert.assertEquals("АЙЗРБ ГЬЧЦД ЗРБРБ УФАДБ ЭЫЗУБ ФУЪТС ЬУБРЭ Ъ".replaceAll(" ", ""), crypto);
    }

    @Test
    public void decrypt() {
        String test = Vigenere.decrypt(ABC_BASE, ABC_CRYPT, ABC_KEY, "АЙЗРБ ГЬЧЦД ЗРБРБ УФАДБ ЭЫЗУБ ФУЪТС ЬУБРЭ Ъ");
        Assert.assertEquals("ПОПРОБУЙТЕ ПРОЧИТАТЬ КРИПТОГРАММУ ТОЧКА".replaceAll(" ", ""), test);
    }

    @Test
    public void encryptSimple() {
        String crypto = Vigenere.encrypt(ABC_BASE, ABC_CRYPT, "А", "ПОПРОБУЙТЕ ПРОЧИТАТЬ КРИПТОГРАММУ ТОЧКА");
        Assert.assertEquals("ЪПЪЫП УОГЮЯ ЪЫПТК ЮЗЮХЛ ЫКЪЮП ВЫЗММ ОЮПТЛ З".replaceAll(" ", ""), crypto);
    }

    @Test
    public void decryptSimple() {
        String test = Vigenere.decrypt(ABC_BASE, ABC_CRYPT, "А", "ЪПЪЫП УОГЮЯ ЪЫПТК ЮЗЮХЛ ЫКЪЮП ВЫЗММ ОЮПТЛ З");
        Assert.assertEquals("ПОПРОБУЙТЕ ПРОЧИТАТЬ КРИПТОГРАММУ ТОЧКА".replaceAll(" ", ""), test);
    }
}
