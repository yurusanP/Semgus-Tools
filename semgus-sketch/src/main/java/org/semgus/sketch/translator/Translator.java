package org.semgus.sketch.translator;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.*;
import org.semgus.sketch.ir.IR;

import java.util.List;
import java.util.stream.Stream;

public record Translator(@NotNull IR ir, int bnd) {
  public @NotNull Stmt translate() {
    var ss = Stream.concat(mkNTGens(), Stream.of(mkTargetDef(), new HarnessMkr(ir).build())).toList();
    return Stmt.seq(ss);
  }

  /**
   * Makes statements needed for non-terminal generator functions.
   */
  private @NotNull Stream<Stmt> mkNTGens() {
    return ir.nts().keySet().stream()
      .map(relName -> NTGenMkr
        .build(relName, this)
        .mkNTGen());
  }

  private @NotNull Stmt mkTargetDef() {
    var inputs = ir.target().nt().params().get("input");

    return Stmt.fnDef(
      Param.plain(
        Id.id(ir.target().nt().name(), IdAttr.withTmpNTType()),
        ir.target().name()
      ),
      inputs,
      Stmt.seq(
        List.of(
          Stmt.varDef(
            Param.intPlain("bnd"),
            Expr.refPlain(bnd)
          ),
          Stmt.aReturn(
            Expr.appPlain(
              ir.target().nt().name(),
              Expr.bnded0(inputs.stream()
                .map(Param::id)
                .map(Expr::ref)
                .toList())
            )
          )
        )
      )
    );
  }
}
