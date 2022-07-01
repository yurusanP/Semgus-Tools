package org.semgus.sketch.util;

import org.jetbrains.annotations.NotNull;

public record Pair<F, S>(@NotNull F first, @NotNull S second) {
  public static <F, S> @NotNull Pair<F, S> of(@NotNull F first, @NotNull S second) {
    return new Pair<>(first, second);
  }
}
