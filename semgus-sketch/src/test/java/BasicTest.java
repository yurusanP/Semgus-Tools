import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

public class BasicTest {
  @Test public void testMax2Exp() {
    TestResources.debug("/benchmarks/basic/max2-exp.sl", 3);
  }

  @Test public void testMax2ExpForall() {
    TestResources.debug("/benchmarks/basic/max2-exp-forall.sl", 3);
  }

  @Test public void testMax2Pair() {
    TestResources.debug("/benchmarks/basic/max2-pair.sl", 5);
  }

  // @Test public void testSumByWhile() {
  //   TestResources.debug("/benchmarks/basic/sum-by-while.sl", 3);
  // }
}
