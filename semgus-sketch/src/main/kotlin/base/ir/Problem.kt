package org.semgus.sketch.base.ir

import org.semgus.sketch.base.syntax.Expr

/**
 * Representation of Sketch problems.
 */
internal data class Problem(
  val semNameToNT: Map<String, NonTerminal>,
  val semNameToRules: Map<String, Sequence<Rule>>,
  val target: Target,
  val constraints: Sequence<Expr>,
)
