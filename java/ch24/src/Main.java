import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static String CHARS = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";

    public static void main(String[] args) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(Main.class.getResourceAsStream("crypto.txt"), StandardCharsets.UTF_8)) {
            int[] crypto = loadCrypto(reader);
            System.out.println(crypto.length);
        }
    }

    public static int[] loadCrypto(Reader reader) throws IOException {
        List<Integer> list = new ArrayList<>();
        while (true) {
            int c = reader.read();
            if (c < 0) break;
            int index = CHARS.indexOf((char) c);
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
