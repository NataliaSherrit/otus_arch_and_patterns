import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class QuadraticEquationTests {
    private static void call() {
        new QuadraticEquation().solve(0, 0, 0);
    }

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
    @Test
    public void testCoeffsIsNull() {
        assertThatThrownBy(() -> {new QuadraticEquation().solve(0, 0, 0);}).isInstanceOf(NullCoeffsException.class);

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
    @Test
    public void testAllCoefsSmallerThanEps() {
        assertThatThrownBy(() -> {new QuadraticEquation().solve(1e-12, 1e-20, 1e-20);}).isInstanceOf(NullCoeffsException.class);

    }
}
