package org.semgus.sketch.translator;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.*;
import org.semgus.sketch.ir.NonTerminal;
import org.semgus.sketch.ir.Rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record RuleDefMkr(
  int ruleIndex,
  NTGenMkr ntGenMkr,
  Rule rule
) {

  static @NotNull RuleDefMkr build(int ruleIndex, @NotNull NTGenMkr ntGenMkr) {
    var rule = ntGenMkr.rules().get(ruleIndex);

    return new RuleDefMkr(ruleIndex, ntGenMkr, rule);
  }

  @NotNull Stmt mkRuleDef() {
    return Stmt.fnDef(
      Param.param(
        Id.id(ntGenMkr.nt().name(), IdAttr.withTmpNTType().andThen(IdAttr.withGenerator())),
        Id.id(ntGenMkr.nt().name(), IdAttr.withRuleIndex(ruleIndex))
      ),
      ntGenMkr.params(),
      mkRuleDefBody()
    );
  }

  private @NotNull Stmt mkRuleDefBody() {
    var childNTsStmts = rule.childNTs().stream()
      .flatMap(this::mkChildNTStmts);

    return Stmt.seq(Stream.concat(childNTsStmts, mkNTVarStmts()).toList());
  }

  private @NotNull Stream<Stmt> mkChildNTStmts(@NotNull NonTerminal childNT) {
    return Stream.concat(
        Stream.of(mkChildNTVarDef(childNT)),
        mkChildNTSubsts(childNT));
  }

  private @NotNull Stmt mkChildNTVarDef(@NotNull NonTerminal childNT) {
    return Stmt.varDef(
      Param.plain(
        Id.id(childNT.name(), IdAttr.withTmpNTType()),
        childNT.getNTVarName()
      ),
      mkChildNTCall(childNT)
    );
  }

  private @NotNull Expr mkChildNTCall(@NotNull NonTerminal childNT) {
    // TODO: Why can't we have an expression in the childNT call?

    var inputExprs = childNT.params().get("input").stream()
      .map(Param::id)
      .map(Expr::ref)
      .toList();

    return Expr.appPlain(
      childNT.name(),
      Expr.bnded1(inputExprs)
    );
  }

  private @NotNull Stream<Stmt> mkChildNTSubsts(@NotNull NonTerminal childNT) {
    return IntStream.range(0, childNT.ordVars().size())
      .mapToObj(i -> mkChildNTSubst(childNT, i))
      .filter(Optional::isPresent)
      .map(Optional::get);
  }

  private @NotNull Optional<Stmt> mkChildNTSubst(@NotNull NonTerminal childNT, int varIndex) {
    var v = childNT.ordVars().get(varIndex);
    if (!v.attrs().contains("output")) return Optional.empty();

    var res = Stmt.varDef(
      Param.plain(v.decl().type(), v.varName()),
      Expr.get(Id.plain(childNT.getNTVarName()), Id.plain(v.decl().id().name()))
    );

    return Optional.of(res);
  }

  private @NotNull Stream<Stmt> mkNTVarStmts() {
    return Stream.of(
      mkNTVarDef(),
      Stmt.aReturn(Expr.refPlain(ntGenMkr.nt().getNTVarName()))
    );
  }

  private @NotNull Stmt mkNTVarDef() {
    return Stmt.varDef(
      Param.plain(
        Id.id(ntGenMkr.nt().name(), IdAttr.withTmpNTType()),
        ntGenMkr.nt().getNTVarName()
      ),
      mkNTCall()
    );
  }

  private @NotNull Expr mkNTCall() {
    return Expr.app(
      Id.id(ntGenMkr.nt().name(), IdAttr.withTmpNTType()),
      mkNTCallArgs()
    );
  }

  private @NotNull List<Expr> mkNTCallArgs() {
    var outputNames = ntGenMkr.nt().params().get("output").stream()
      .map(Param::id)
      .map(Id::name)
      .toList();

    return outputNames.stream()
      .map(name -> Expr.assignPlain(name, rule.bindings().get(name)))
      .toList();
  }
}
