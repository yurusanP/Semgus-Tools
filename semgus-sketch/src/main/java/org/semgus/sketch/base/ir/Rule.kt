package org.semgus.sketch.base.ir

import org.semgus.sketch.base.syntax.Expr

/**
 * IR rule.
 */
internal data class Rule(
  val index: Int,
  val headNT: NonTerminal,
  val childNTs: Sequence<NonTerminal>,
  val varRanks: Map<String, Int>,
  val varBinds: Map<String, Expr>,
)

internal fun Sequence<Rule>.semName() = this.first().headNT.semName
