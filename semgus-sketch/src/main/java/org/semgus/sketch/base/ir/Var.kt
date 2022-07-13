package org.semgus.sketch.base.ir

import org.semgus.sketch.base.syntax.Id
import org.semgus.sketch.base.syntax.Param

/**
 * IR variable.
 */
internal data class Var(
  val decl: Param,
  val varName: String,
  val attrs: Set<String>,
)

internal fun Var.subst(newVarName: String) = Var(
  decl,
  newVarName,
  attrs,
)

internal fun Sequence<Var>.decls() = this.map(Var::decl)
internal fun Sequence<Var>.declIds() = this.decls().map(Param::id)
internal fun Sequence<Var>.declNames() = this.declIds().map(Id::name)

internal fun Sequence<Var>.varNames() = this.map(Var::varName)
