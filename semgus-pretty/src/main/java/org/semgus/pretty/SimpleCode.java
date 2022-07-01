package org.semgus.pretty;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Pretty simple code.
 * Learned from org.aya.pretty.
 */
public sealed interface SimpleCode extends Prettiable {
  @Override default @NotNull SimpleCode codify() {
    return this;
  }

  record Empty() implements SimpleCode {}

  record Char(char c) implements SimpleCode {}

  record Text(@NotNull String s) implements SimpleCode {}

  record Line(int i, @NotNull Stream<SimpleCode> xs) implements SimpleCode {}
}
