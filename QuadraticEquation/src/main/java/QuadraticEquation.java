import java.util.Arrays;

public class QuadraticEquation {
    public static final double EPS = 1e-10;

    public double[] solve(double a, double b, double c) {
        double[] roots = Math.abs(a) >= EPS ? calculateRoots(a, b, c) : examABCcoef(b, c);

        System.out.println(Arrays.toString(roots));
        return roots;
    }

    private double calculateDiscriminant (double a, double b, double c) {
        return Math.pow(b, 2) - 4 * a * c;
    }

    private double[] calculateRoots (double a, double b, double c) {
        if (Math.abs(calculateDiscriminant(a,b,c)) < EPS) {
            System.out.println("The given equation has one root");
            return new double[]{-b / (2 * a)};
        }
        if (calculateDiscriminant(a,b,c) < 0) {
            System.out.println("The given equation has no roots");
            return new double[]{};
        }
        System.out.println("The given equation has two roots");
        return new double[]{(-b - Math.sqrt(calculateDiscriminant(a,b,c))) / (2 * a), (-b + Math.sqrt(calculateDiscriminant(a,b,c))) / (2 * a)};
    }

    private double[] examABCcoef (double b, double c) {
        if (Math.abs(b) < EPS) {
            if (Math.abs(c) < EPS) {
                throw new NullCoeffsException();
            }
            return new double[]{};
        }
            return new double[]{-c / b};
    }
}