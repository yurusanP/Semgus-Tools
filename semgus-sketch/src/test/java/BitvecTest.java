import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

public class BitvecTest {
  /**
   * Solved.
   */
  @Test public void testOr2Bitvec() {
    TestResources.debug("/benchmarks/bitvec/or2-bitvec.sl", 3);
  }
}
