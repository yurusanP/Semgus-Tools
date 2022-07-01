import org.junit.jupiter.api.Test;
import org.semgus.test.util.TestResources;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeout;

public class RunTest {
  @Test public void testRunOnMax2Exp() {
    assertTimeout(Duration.ofSeconds(10), () -> TestResources.getJson("/benchmarks/basic/max2-exp.sl"));
  }
}
