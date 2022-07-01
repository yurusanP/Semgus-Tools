package org.semgus.sketch.ir;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.Param;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public record NonTerminal(
  @NotNull String name,
  @NotNull String relName,
  @NotNull List<Var> ordVars,
  @NotNull Map<String, List<Param>> params  // Attributes -> Parameters
) {

  public @NotNull String getNTVarName() {
    return ordVars.stream()
      .filter(v -> v.decl().type().name().equals(name))
      .findFirst()
      .orElseThrow(() -> new NoSuchElementException("Cannot find non-terminal variable name."))
      .varName();
  }
}
