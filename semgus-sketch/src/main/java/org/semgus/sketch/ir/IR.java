package org.semgus.sketch.ir;

import org.jetbrains.annotations.NotNull;
import org.semgus.java.object.SmtTerm;
import org.semgus.java.problem.SemanticRule;
import org.semgus.java.problem.SemgusNonTerminal;
import org.semgus.java.problem.SemgusProblem;
import org.semgus.sketch.base.Expr;
import org.semgus.sketch.base.Param;
import org.semgus.sketch.util.Evaluator;

import java.util.*;

/**
 * Intermediate representation.
 */
public record IR(
  @NotNull Map<String, NonTerminal> nts,  // relation names -> non-terminals
  @NotNull Map<String, List<Rule>> rules,  // relation names -> rules
  @NotNull Target target,
  @NotNull List<Expr> constraints
) {
  public static @NotNull IR fromSemgusProblem(@NotNull SemgusProblem problem) {
    var ntsEntries = problem.nonTerminals().values().stream()
      .map(semgusNT -> NonTerminalMkr
        .build(semgusNT)
        .mkNT())
      .map(nt -> Map.entry(nt.relName(), nt))
      .toArray(Map.Entry[]::new);

    @SuppressWarnings("unchecked")
    var nts = (Map<String, NonTerminal>) Map.ofEntries(ntsEntries);

    var rulesEntries = problem.nonTerminals().values().stream()
      .map(semgusNT -> semgusNT.productions().values().stream()
        .flatMap(semgusProd -> semgusProd.semanticRules().stream())
        .map(semRule -> RuleMkr
          .build(semRule, nts)
          .mkRule())
        .toList())
      .map(rs -> Map.entry(rs.iterator().next().nt().relName(), rs))
      .toArray(Map.Entry[]::new);

    @SuppressWarnings("unchecked")
    var rules = (Map<String, List<Rule>>) Map.ofEntries(rulesEntries);

    var targetRelName = getSemRule0(problem.targetNonTerminal()).head().name();
    var target = new Target(problem.targetName(), nts.get(targetRelName));

    var constraints = problem.constraints().stream()
      .map(Evaluator::eval)
      .toList();

    return new IR(nts, rules, target, constraints);
  }

  /**
   * Gets the first semantic rule of a non-terminal.
   */
  static @NotNull SemanticRule getSemRule0(@NotNull SemgusNonTerminal semgusNT) {
    return semgusNT.productions().values().iterator().next().semanticRules().iterator().next();
  }
}
