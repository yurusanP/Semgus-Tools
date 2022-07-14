import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

public class BasicTest {
  /**
   * Solved.
   */
  @Test public void testImpvDemo() {
    TestResources.debug("/benchmarks/basic/impv-demo.sl", 4);
  }

  /**
   * Solved.
   */
  @Test public void testMax2Exp() {
    TestResources.debug("/benchmarks/basic/max2-exp.sl", 3);
  }

  /**
   * Solved.
   */
  @Test public void testMax2ExpForall() {
    TestResources.debug("/benchmarks/basic/max2-exp-forall.sl", 3);
  }

  /**
   * Solved.
   */
  @Test public void testMax2Pair() {
    TestResources.debug("/benchmarks/basic/max2-pair.sl", 5);
  }

  /**
   * Solved.
   */
  @Test public void testMax3Exp() {
    TestResources.debug("/benchmarks/basic/max3-exp.sl", 4);
  }


  /**
   * Solved.
   */
  @Test public void testPolynomial() {
    TestResources.debug("/benchmarks/basic/polynomial.sl", 4);
  }
}
