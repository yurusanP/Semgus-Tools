package org.semgus.sketch.ir

import org.semgus.sketch.syntax.Param

/**
 * IR non-terminal.
 */
internal data class NonTerminal(
  val name: String,
  val relName: String,
  val ordVars: Sequence<Var>,
  val params: Map<String, Sequence<Param>>,
) {
  fun ntVarName() = ordVars
    .find { it.decl.type.name == name }
    ?.varName ?: throw NoSuchElementException("Cannot find non-terminal variable name.")

  fun inputs() = params["input"]
    ?: throw NoSuchElementException("Cannot find variables with the input attribute.")

  fun outputs() = params["output"]
    ?: throw NoSuchElementException("Cannot find variables with the output attribute.")
}
