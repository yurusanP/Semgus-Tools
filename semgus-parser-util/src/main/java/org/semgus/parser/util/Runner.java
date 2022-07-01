package org.semgus.parser.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Parser utilities about the runner.
 */
public interface Runner {
  /**
   * Runs the SemGuS parser executable.
   *
   * @param inputIs the input stream of the input file
   * @return the resulting json string
   */
  static @NotNull String run(@NotNull InputStream inputIs) {
    try {
      var tmp = File.createTempFile("semgus-parser", null);
      tmp.deleteOnExit();
      if (!tmp.setExecutable(true)) throw new RuntimeException("Cannot set the permission.");

      var osName = System.getProperty("os.name");
      var parserName = osName.startsWith("Windows") ? "/bin/semgus-parser-win.exe"
        : osName.startsWith("Mac") ? "/bin/semgus-parser-osx"
        : "/bin/semgus-parser-linux";

      var parserIs = Runner.class.getResourceAsStream(parserName);
      Objects.requireNonNull(parserIs, "Invalid resource " + parserName);
      FileUtils.copyInputStreamToFile(parserIs, tmp);

      var parserProcess =
        new ProcessBuilder(tmp.getAbsolutePath(), "--format", "json", "--mode", "batch").start();
      IOUtils.copy(inputIs, parserProcess.getOutputStream());
      parserProcess.getOutputStream().close();

      var res = IOUtils.toString(parserProcess.getInputStream(), StandardCharsets.UTF_8);
      parserProcess.destroy();

      return res;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
