package org.semgus.sketch.translator;

import org.jetbrains.annotations.NotNull;
import org.semgus.sketch.base.*;
import org.semgus.sketch.ir.NonTerminal;
import org.semgus.sketch.ir.Rule;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record NTGenMkr(
  @NotNull String relName,
  @NotNull Translator translator,
  @NotNull NonTerminal nt,
  @NotNull List<Rule> rules,
  @NotNull List<Param> params  // the parameter list for the generator function
) {
  static @NotNull NTGenMkr build(@NotNull String relName, @NotNull Translator translator) {
    var nt = translator.ir().nts().get(relName);
    var rules = translator.ir().rules().get(relName);
    var inputs = nt.params().get("input");
    var params = Param.bnded0(inputs);

    return new NTGenMkr(relName, translator, nt, rules, params);
  }

  @NotNull Stmt mkNTGen() {
    var ss = Stream.concat(
        Stream.of(mkTypeDef(), mkNTFnDef()),
        mkRuleDefs().stream())
      .toList();
    return Stmt.seq(ss);
  }

  /**
   * Makes a generator function return type struct definition.
   */
  private @NotNull Stmt mkTypeDef() {
    var outputs = nt.params().get("output");
    return Stmt.ntTypeDef(nt.name(), outputs);
  }

  /**
   * Makes a non-terminal generator function definition.
   */
  private @NotNull Stmt mkNTFnDef() {
    return Stmt.fnDef(mkNTFnDefDecl(), params, mkNTFnDefBody());
  }

  private @NotNull Param mkNTFnDefDecl() {
    return Param.plain(Id.id(nt.name(), IdAttr.withTmpNTType().andThen(IdAttr.withGenerator())), nt.name());
  }

  private @NotNull Stmt mkNTFnDefBody() {
    return Stmt.bndChked(Stmt.aReturn(mkChoice()));
  }

  private @NotNull Expr mkChoice() {
    var es = IntStream.range(0, rules.size())
      .mapToObj(i -> Id.id(nt().name(), IdAttr.withRuleIndex(i)))
      .map(fn -> Expr.app(
        fn,
        params.stream().map(param -> Expr.ref(param.id())).toList()
      ))
      .toList();
    return Expr.choice(es);
  }

  /**
   * Makes the non-terminal semantic rule generator function definitions.
   */
  private @NotNull List<Stmt> mkRuleDefs() {
    return IntStream.range(0, rules.size())
      .mapToObj(i -> RuleDefMkr
        .build(i, this)
        .mkRuleDef())
      .toList();
  }
}
