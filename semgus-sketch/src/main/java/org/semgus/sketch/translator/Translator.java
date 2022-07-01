package org.semgus.sketch.translator;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.*;
import org.semgus.sketch.ir.IR;
import org.semgus.sketch.util.Pair;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Translator(@NotNull IR ir, int bnd) {
  public @NotNull Stmt translate() {
    var ss = Stream.concat(mkNTGens(), Stream.of(mkTargetDef(), mkHarnessDef())).toList();
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

  private @NotNull Stmt mkHarnessDef() {
    // TODO: What about more than 1 forall... Or hybrid ones...?
    if (ir.constraints().size() == 1) {
      if (ir.constraints().get(0) instanceof Expr.Forall eForall) {
        return Stmt.fnDef(
          Param.plain(
            Id.id("void", IdAttr.withHarness()),
            "sketch"
          ),
          ir.target().nt().params().get("input"),
          mkConstraintStmt(eForall.e())
        );
      }
    }

    return Stmt.fnDef(
      Param.plain(
        Id.id("void", IdAttr.withHarness()),
        "sketch"
      ),
      List.of(),
      Stmt.seq(mkConstraintStmts(ir.constraints()))
    );
  }

  private @NotNull List<Stmt> mkConstraintStmts(@NotNull List<Expr> es) {
    return es.stream()
      .map(this::mkConstraintStmt)
      .toList();
  }

  private @NotNull Stmt mkConstraintStmt(Expr e) {
    if (e instanceof Expr.App eApp && eApp.fn().name().equals(ir.target().nt().relName())) {
      var inputExprs = ir.target().nt().params().get("input").stream()
        .map(Param::id)
        .map(Expr::ref)
        .toList();
      var outputAssigns = ir.target().nt().params().get("output").stream()
        .map(Param::id)
        .map(output -> Expr.assignPlain(output.name(), Expr.ref(output)))
        .toList();

      var args = eApp.args();
      var ntVarName = ir.target().nt().getNTVarName();
      var varDefs = IntStream.range(0, args.size())
        .mapToObj(i -> Pair.of(ir.target().nt().ordVars().get(i), args.get(i)))
        .filter(p -> !p.first().varName().equals(ntVarName))
        .map(p -> Stmt.varDef(p.first().decl(), p.second()));
      var assertion = Stmt.aAssert(
        Expr.binaryPlain(
          "==",
          Expr.appPlain(
            ir.target().name(),
            inputExprs
          ),
          Expr.app(
            Id.id(ir.target().nt().name(), IdAttr.withTmpNTType()),
            outputAssigns
          )
        )
      );

      return Stmt.seq(Stream.concat(varDefs, Stream.of(assertion)).toList());
    } else {
      throw new RuntimeException("The target is not valid.");
    }
  }
}
