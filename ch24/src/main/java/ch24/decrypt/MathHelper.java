package ch24.decrypt;

/**
 * Общие математические функции.
 *
 * @author Artem V. Navrotskiy (bozaro at buzzsoft.ru)
 */
public class MathHelper {
    private MathHelper() {
    }

    public static double[] normalize(double[] p) {
        double r[] = new double[p.length];
        double sum = 0;
        for (double a : p) {
            sum += a;
        }
        for (int i = 0; i < p.length; ++i) {
            r[i] = p[i] / sum;
        }
        return r;
    }
}
