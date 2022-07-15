package org.semgus.sketch.translator

import org.semgus.java.problem.SemgusNonTerminal
import org.semgus.java.problem.SemgusProblem
import org.semgus.java.problem.SemgusProduction
import org.semgus.sketch.base.ir.NonTerminal
import org.semgus.sketch.base.ir.Problem
import org.semgus.sketch.base.ir.Rule
import org.semgus.sketch.base.ir.semName
import org.semgus.sketch.base.ir.Target

/**
 * Sketch problem translator.
 */
data class ProblemTrans(val semgusProblem: SemgusProblem) {
  private val semNameToNT = semgusProblem.nonTerminals().values.asSequence()
    .map(SemgusNonTerminal::toSketchNonTerminal)
    .associateBy(NonTerminal::semName)

  private val semNameToRules = semgusProblem.nonTerminals().values.asSequence()
    .map { sketchRules(semgusNT = it, semNameToNT) }
    .associateBy(Sequence<Rule>::semName)

  private val target = with(semgusProblem.targetNonTerminal().firstSemRule().head().name()) {
    val targetNT = semNameToNT[this]!!
    Target(semgusProblem.targetName(), targetNT)
  }

  private val constraints = semgusProblem.constraints().asSequence()
    .map { it.toSketchExpr(target) }

  internal fun trans() = Problem(
    semNameToNT,
    semNameToRules,
    target,
    constraints,
  )

  // TODO: fix multiple semantic rules in one production.
  /**
   * Translates a SemGuS non-terminals into Sketch rules.
   */
  private fun sketchRules(semgusNT: SemgusNonTerminal, semNameToNT: Map<String, NonTerminal>) =
    semgusNT.productions().values.asSequence()
      .flatMap(SemgusProduction::semanticRules)
      .mapIndexed { index, semRule -> semRule.toSketchRule(index, semNameToNT) }
}

/**
 * Translates a SemGuS problem into a Sketch problem.
 */
internal fun SemgusProblem.toSketchProblem() = ProblemTrans(this).trans()
