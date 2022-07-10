package org.semgus.sketch.ir

import org.semgus.sketch.syntax.Expr

/**
 * Representation of Sketch problems.
 */
internal data class Problem(
  val nts: Map<String, NonTerminal>,
  val rules: Map<String, Sequence<Rule>>,
  val target: Target,
  val constraints: Sequence<Expr>,
)
