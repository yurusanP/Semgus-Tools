package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;
import org.semgus.pretty.Code;
import org.semgus.pretty.Prettiable;
import org.semgus.sketch.frontend.Distiller;

/**
 * Sketch syntax.
 */
public sealed interface Syntax extends Prettiable permits Expr, Stmt, Id, Param {
  @Override default @NotNull Code codify() {
    return Distiller.distill(this);
  }
}
