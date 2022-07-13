package org.semgus.test.util;

import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.semgus.java.problem.ProblemGenerator;
import org.semgus.java.problem.SemgusProblem;
import org.semgus.java.util.DeserializationException;
import org.semgus.parser.util.Runner;
import org.semgus.sketch.SketchKt;

import java.io.IOException;
import java.util.Objects;

/**
 * Test utilities about resources.
 */
public interface TestResources {
  /**
   * Gets the json representation of a SemGuS problem specification by running the parser.
   *
   * @param inputName the input file name
   * @return the resulting json string
   */
  static @NotNull String getJson(@NotNull String inputName) {
    try (var is = TestResources.class.getResourceAsStream(inputName)) {
      Objects.requireNonNull(is, "Invalid resource " + inputName);
      return Runner.run(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the SemGuS problem given the input json representation.
   *
   * @param json the input json representation
   * @return the resulting SemGuS problem
   */
  static @NotNull SemgusProblem getSemgusProblem(@NotNull String json) {
    try {
      return ProblemGenerator.parse(json);
    } catch (DeserializationException | ParseException e) {
      throw new RuntimeException(e);
    }
  }

  static void debug(@NotNull String inputName, int bnd) {
    var json = TestResources.getJson(inputName);
    var problem = TestResources.getSemgusProblem(json);
    var sketch = SketchKt.fromSemgusProblem(problem, bnd);
    System.out.println(sketch.dump());
  }
}
