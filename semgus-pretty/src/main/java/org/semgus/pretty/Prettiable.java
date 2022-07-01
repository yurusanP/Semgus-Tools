package org.semgus.pretty;

import org.jetbrains.annotations.NotNull;

/**
 * Learned from org.aya.pretty.
 */
public interface Prettiable {
  @NotNull Prettiable codify();
}
