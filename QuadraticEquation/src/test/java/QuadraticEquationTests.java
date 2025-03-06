import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.testng.Assert;


public class QuadraticEquationTests extends Assert {
    @Test
    public void testNoRoots() {
        Assertions.assertArrayEquals(new double[]{}, new QuadraticEquation().solve(1, 0, 1));
    }

    @Test
    public void testTwoRoots() {
        Assertions.assertArrayEquals(new double[]{-1, 1}, new QuadraticEquation().solve(1, 0, -1));

    }
    @Test
    public void testOneRoot() {
        Assertions.assertArrayEquals(new double[]{-1}, new QuadraticEquation().solve(1, 2, 1));

    }
    @Test (expected = NullCoeffsException.class)
    public void testCoeffsIsNull() {
        new QuadraticEquation().solve(0, 0, 0);

    }
    @Test
    public void testCoeffASmallerThanEps() {
        Assertions.assertArrayEquals(new double[]{100}, new QuadraticEquation().solve(1e-16, 1, -100));

    }
    @Test
    public void testDIsNull() {
        Assertions.assertArrayEquals(new double[]{-1}, new QuadraticEquation().solve(2, 4, 2));

    }
    @Test
    public void testDSmallerThanEps() {
        Assertions.assertArrayEquals(new double[]{-5.0E-21}, new QuadraticEquation().solve(1, 1e-20, 1e-20));

    }
    @Test (expected = NullCoeffsException.class)
    public void testAllCoefsSmallerThanEps() {
        new QuadraticEquation().solve(1e-12, 1e-20, 1e-20);

    }
}
