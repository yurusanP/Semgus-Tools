package org.semgus.sketch.ir

import org.semgus.sketch.syntax.Param

/**
 * IR variable.
 */
internal data class Var(
  val decl: Param,
  val varName: String,
  val attrs: Set<String>,
)
