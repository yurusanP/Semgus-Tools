package org.semgus.sketch.ir;

import org.jetbrains.annotations.NotNull;

public record Target(
  @NotNull String name,
  @NotNull NonTerminal nt
) {
}
