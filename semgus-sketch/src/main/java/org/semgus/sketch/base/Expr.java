package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Sketch expression.
 */
public sealed interface Expr extends Syntax {
  record Ref(@NotNull Id id) implements Expr {}

  record App(@NotNull Id fn, @NotNull List<Expr> args) implements Expr {}

  record Get(@NotNull Id id, @NotNull Id field) implements Expr {}

  record Assign(@NotNull Id id, @NotNull Expr e) implements Expr {}

  record Unary(@NotNull Id op, @NotNull Expr e) implements Expr {}

  record Binary(@NotNull Id op, @NotNull Expr l, @NotNull Expr r) implements Expr {}

  record Cond(@NotNull Expr i, @NotNull Expr t, @NotNull Expr e) implements Expr {}

  record Choice(@NotNull List<Expr> es) implements Expr {}

  record Forall(@NotNull List<Param> binds, @NotNull Expr e) implements Expr {}

  static @NotNull Expr ref(@NotNull Id id) {
    return new Ref(id);
  }

  static @NotNull Expr refPlain(@NotNull String s) {
    return ref(Id.plain(s));
  }

  static @NotNull Expr refPlain(long s) {
    return ref(Id.plain(s));
  }

  static @NotNull Expr app(@NotNull Id fn, @NotNull List<Expr> args) {
    return new App(fn, args);
  }

  static @NotNull Expr get(@NotNull Id id, @NotNull Id field) {
    return new Get(id, field);
  }

  static @NotNull Expr assign(@NotNull Id id, @NotNull Expr e) {
    return new Assign(id, e);
  }

  static @NotNull Expr assignPlain(@NotNull String idName, @NotNull Expr e) {
    return assign(Id.plain(idName), e);
  }

  static @NotNull Expr appPlain(@NotNull String fnName, @NotNull List<Expr> args) {
    return app(Id.plain(fnName), args);
  }

  static @NotNull List<Expr> bnded0(@NotNull List<Expr> args) {
    var bndExpr = Expr.refPlain("bnd");
    return Stream.concat(args.stream(), Stream.of(bndExpr)).toList();
  }

  static @NotNull List<Expr> bnded1(@NotNull List<Expr> args) {
    var bndExpr = Expr.binaryPlain("-", refPlain("bnd"), refPlain(1));
    return Stream.concat(args.stream(), Stream.of(bndExpr)).toList();
  }

  static @NotNull Expr unary(@NotNull Id op, @NotNull Expr e) {
    return new Unary(op, e);
  }

  static @NotNull Expr unaryPlain(@NotNull String opName, @NotNull Expr e) {
    return unary(Id.plain(opName), e);
  }

  static @NotNull Expr binary(@NotNull Id op, @NotNull Expr l, @NotNull Expr r) {
    return new Binary(op, l, r);
  }

  static @NotNull Expr binaryPlain(@NotNull String opName, @NotNull Expr l, @NotNull Expr r) {
    return binary(Id.plain(opName), l, r);
  }

  static @NotNull Expr cond(@NotNull Expr i, @NotNull Expr t, @NotNull Expr e) {
    return new Cond(i, t, e);
  }

  static @NotNull Expr choice(@NotNull List<Expr> es) {
    return new Choice(es);
  }

  static @NotNull Expr forall(@NotNull List<Param> binds, @NotNull Expr e) {
    return new Forall(binds, e);
  }

  static private Expr subHelper(Expr expr, Map<String, Param> oldToNew) {
    if (expr instanceof Expr.Ref && oldToNew.containsKey(((Ref) expr).id.name()))
      return Expr.ref(Id.plain(oldToNew.get(((Ref) expr).id.name()).id().name()));
    if (expr instanceof Expr.Ref || expr instanceof Expr.Get)
      return expr;
    if (expr instanceof Expr.App)
      return Expr.app(
        ((App) expr).fn,
        ((App) expr).args
          .stream()
          .map(x->subHelper(x, oldToNew))
          .toList()
      );
    if (expr instanceof Expr.Assign)
      return Expr.assign(((Assign) expr).id, subHelper(((Assign) expr).e, oldToNew));
    if (expr instanceof Expr.Unary)
      return Expr.unary(((Unary) expr).op, subHelper(((Unary) expr).e, oldToNew));
    if (expr instanceof Expr.Binary)
      return Expr.binary(((Binary) expr).op, subHelper(((Binary) expr).l, oldToNew), subHelper(((Binary) expr).r, oldToNew));
    if (expr instanceof  Expr.Cond)
      return Expr.cond(
        subHelper(((Cond) expr).i, oldToNew),
        subHelper(((Cond) expr).t, oldToNew),
        subHelper(((Cond) expr).e, oldToNew)
      );
    if (expr instanceof  Expr.Choice)
      return Expr.choice(((Choice) expr).es.stream().map(x->subHelper(x, oldToNew)).toList());
    if (expr instanceof Expr.Forall)
      return Expr.forall(((Forall) expr).binds, subHelper(((Forall) expr).e, oldToNew));
    throw new RuntimeException("This is unreachable");
  }
  static @NotNull Expr subWithNewVar(Expr e, Map<String, Param> oldToNew) {
    return subHelper(e, oldToNew);
  }
}
