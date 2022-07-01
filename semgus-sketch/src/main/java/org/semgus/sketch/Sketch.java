package org.semgus.sketch;

import org.jetbrains.annotations.NotNull;
import org.semgus.java.problem.SemgusProblem;
import org.semgus.pretty.Prettier;
import org.semgus.sketch.base.Stmt;
import org.semgus.sketch.ir.IR;
import org.semgus.sketch.translator.Translator;

public record Sketch(@NotNull Stmt inner) {
  public static @NotNull Sketch fromSemgusProblem(@NotNull SemgusProblem problem, int bnd) {
    var ir = IR.fromSemgusProblem(problem);
    return new Sketch(new Translator(ir, bnd).translate());
  }

  public @NotNull String dump() {
    return Prettier.pretty(inner.codify());
  }
}
