import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

public class ImperativeTest {
  /**
   * TODO: Not solved.
   */
  @Test public void testImperative() {
    TestResources.debug("/benchmarks/imperative/sum-by-while.sl", 5);
  }
}
