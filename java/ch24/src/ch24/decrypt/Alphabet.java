package ch24.decrypt;

/**
 * Алфавит и его частотное распределение.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class Alphabet {
    private final float[] frequency;
    private final String alphabet;

    public Alphabet(float[] frequency, String alphabet) {
        this.frequency = frequency;
        this.alphabet = alphabet;
    }

    public float[] getFrequency() {
        return frequency;
    }

    public String getAlphabet() {
        return alphabet;
    }
}
