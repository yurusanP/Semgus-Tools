package org.semgus.sketch.translator;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.*;
import org.semgus.sketch.ir.IR;
import org.semgus.sketch.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

record HarnessMkr(IR ir) {
  @NotNull Stmt build() {
    // Step0: Change order to make stmt with forall first
    // Step1: Get all variables needed (e.g. x y z in (forall x y) (forall x z))
    // Step2: Put them in arguments in name (v_0, v_1, \dots) and build the map
    // Step3: evaluate in common way
    // TODO: What about more than 1 forall... Or hybrid ones...?

    Stream<Expr.Forall> forallConstraints = ir.constraints().stream().filter(x -> x instanceof Expr.Forall).map(x -> (Expr.Forall) x);
    var trivialConstraints = ir.constraints().stream().filter(x -> !(x instanceof Expr.Forall));

    List<Map.Entry<Expr, Map<String, Param>>> forallParams = mkParams(forallConstraints).toList();
    var b = forallParams.stream().map(x -> Expr.subWithNewVar((Expr)x.getKey(), x.getValue()));
    var a = forallParams.stream().flatMap(x -> x.getValue().values().stream());

    var trivialParams = ir.target().nt().params().values().stream().flatMap(Collection::stream);

    return Stmt.fnDef(
      Param.plain(
        Id.id("void", IdAttr.withHarness()),
        "sketch"
      ),
      Stream.concat(
        a,
        trivialParams
      ).collect(Collectors.toList()),
      Stmt.seq(Stream.concat(
        Stream.of(Stmt.atomic(Id.id(ir.target().nt().name(), IdAttr.withTmpNTType()), Expr.refPlain("_t"))),
        Stream.concat(
        mkConstraintStmts(b),
        mkConstraintStmts(trivialConstraints)))
        .toList())
    );
  }

  private Stream<Map.Entry<Expr, Map<String, Param>>> mkParams(Stream<Expr.Forall> constraints) {
    AtomicInteger ai = new AtomicInteger();
    return constraints.map(x ->
      Map.entry(
        x.e(),
        x.binds()
          .stream()
          .collect(Collectors
            .toMap(
              o -> o.id().name(),
              o -> Param.param(
                o.type(),
                Id.plain("var" + ai.getAndIncrement())))))
    );
  }

  private @NotNull Stream<Stmt> mkConstraintStmts(@NotNull Stream<Expr> es) {
    AtomicInteger ai = new AtomicInteger();
    return es
      .map(e -> mkConstraintStmt(e));
  }

  private @NotNull Stmt mkConstraintStmt(Expr e) {
    return
      getTarget(e).findAny().isEmpty() ?
        mkAssertStmt(e) :
        Stmt.seq(Stream.concat(mkVarDef(e), Stream.of(mkAssertStmt(e))).toList());
  }

  private boolean isTargetExpr(Expr e) {
    return (e instanceof Expr.App eApp && eApp.fn().name().equals(ir.target().nt().relName()));
  }
  private @NotNull Stream<Stmt> mkVarDef(Expr e) {
    return getTarget(e).flatMap(this::mkTargetVarDef);
  }

  private @NotNull Stream<Stmt> mkTargetVarDef(Expr.App eApp) {
    var args = eApp.args();
    var ntVarName = ir.target().nt().getNTVarName();

    return IntStream.range(0, args.size())
      .mapToObj(i -> Pair.of(ir.target().nt().ordVars().get(i), args.get(i)))
      .filter(p -> !p.first().varName().equals(ntVarName))
      .map(p -> Stmt.varDef(Param.param(Id.plain(""), Id.plain(p.first().decl().id().name())), p.second()));
  }

  private @NotNull Stmt mkAssertStmt(Expr e) {
    return Stmt.seq(List.of(Stmt.varDef(Param.plain(Id.plain(""), "_t"), mkTargetOutputValue(e)), Stmt.aAssert(mkAssertExpr(e))));
  }

  private @NotNull Expr mkAssertExpr(Expr e) {
    if (isTargetExpr(e))
      return mkTargetAssertExpr(e);
    else if (e instanceof Expr.App eApp)
      return Expr.app(
        eApp.fn(),
        eApp.args().stream().map(this::mkAssertExpr).toList()
        );
    else if (e instanceof Expr.Get || e instanceof Expr.Ref)
      return e;
    else if (e instanceof Expr.Unary eUnary)
      return Expr.unary(eUnary.op(), mkAssertExpr(eUnary.e()));
    else if (e instanceof Expr.Binary eBinary)
      return Expr.binary(eBinary.op(), mkAssertExpr(eBinary.l()), mkAssertExpr(eBinary.r()));
    else if (e instanceof Expr.Cond eCond)
      return Expr.cond(mkAssertExpr(eCond.i()), mkAssertExpr(eCond.t()), mkAssertExpr(eCond.e()));
    else
      throw new IllegalStateException(e.toString());
  }

  private Stream<Expr.App> getTarget(Expr e) {
    if (isTargetExpr(e))
      return Stream.of((Expr.App)e);
    else if (e instanceof Expr.Ref || e instanceof Expr.Get)
      return Stream.empty();
    else if (e instanceof Expr.App eApp)
      return eApp.args().stream().flatMap(this::getTarget);
    else if (e instanceof Expr.Unary eUnary)
      return getTarget(eUnary.e());
    else if (e instanceof Expr.Binary eBinary)
      return Stream.concat(getTarget(eBinary.l()), getTarget(eBinary.r()));
    else if (e instanceof Expr.Cond eCond)
      return Stream.concat(getTarget(eCond.i()), Stream.concat(getTarget(eCond.t()), getTarget(eCond.e())));
    else
      throw new IllegalStateException();
  }

  private @NotNull Expr mkTargetOutputValue(Expr e) {
    var outputAssigns = ir.target().nt().params().get("output").stream()
      .map(Param::id)
      .map(output -> Expr.assignPlain(output.name(), Expr.ref(output)))
      .toList();
    return Expr.app(
      Id.id(ir.target().nt().name(), IdAttr.withTmpNTType()),
      outputAssigns
    );
  }

  private @NotNull Expr mkTargetAssertExpr(Expr e) {
    if (isTargetExpr(e)) {
      var inputExprs = ir.target().nt().params().get("input").stream()
        .map(Param::id)
        .map(Expr::ref)
        .toList();

      return
        Expr.binaryPlain(
          "==",
          Expr.appPlain(
            ir.target().name(),
            inputExprs
          ),
          Expr.ref(Id.plain("_t"))
        );
    } else {
      throw new RuntimeException(e + "The target is not valid.");
    }
  }
}
