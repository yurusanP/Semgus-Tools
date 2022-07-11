package org.semgus.sketch.ir

import org.semgus.sketch.syntax.Expr

/**
 * IR rule.
 */
internal data class Rule(
  val nt: NonTerminal,
  val childNTs: Sequence<NonTerminal>,
  val binds: Map<String, Expr>,
  val ranks: Map<String, Int>
)
