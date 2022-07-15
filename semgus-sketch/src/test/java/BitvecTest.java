import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

public class BitvecTest {
  /**
   * Solved.
   */
  @Test public void testOr2Bitvec() {
    TestResources.debug("/benchmarks/bitvec/or2-bitvec.sl", 4);
  }

  /**
   * Solved.
   */
  @Test public void testXor2Bitvec() {
    TestResources.debug("/benchmarks/bitvec/xor2-bitvec.sl", 4);
  }
}
