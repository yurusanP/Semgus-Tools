package org.semgus.sketch.base.ir

/**
 * IR non-terminal.
 */
internal data class NonTerminal(
  val name: String,
  val semName: String,
  val vars: Sequence<Var>,
  val aux: Var,
  val inputs: Sequence<Var>,
  val outputs: Sequence<Var>,
)

internal fun NonTerminal.subst(newVarNames: Sequence<String>) = with(this) {
  val newVars = vars.zip(newVarNames).map { (v, newVarName) -> v.subst(newVarName) }

  NonTerminal(
    name,
    semName,
    vars = newVars,
    aux = newVars.single { it.decl.type.name == name },
    inputs = newVars.filter { it.attrs.contains("input") },
    outputs = newVars.filter { it.attrs.contains("output") },
  )
}
