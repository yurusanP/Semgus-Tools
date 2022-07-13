package org.semgus.sketch.codegen

import org.semgus.sketch.base.ir.*
import org.semgus.sketch.base.syntax.*

/**
 * Generator of Sketch statements about a child non-terminal
 * in a rule generator definition given a child non-terminal.
 */
internal data class StmtsChildNTGen(
  val ruleDefGen: RuleDefGen,
  val childNT: NonTerminal,
) {
  /**
   * Sketch statements about a child non-terminal in a rule generator definition.
   */
  fun gen() = with(childNTAuxDef()) {
    sequenceOf(this) + childNTOutputDefs()
  }

  /**
   * Child non-terminal auxiliary variable definition.
   */
  private fun childNTAuxDef(): Stmt = with(childNT) {
    // TODO: Fix normal function calls to the head non-terminal.
    varDef(
      decl = paramPlain(
        type = id(childNT.name) { withTmpNTType() },
        name = aux.varName,
      ),
      init = callPlain(
        fnName = childNT.name,
        args = argsBnded1(inputs.varNames().map(::refPlain)),
      ),
    )
  }

  /**
   * Child non-terminal output variable redefinition.
   */
  private fun childNTOutputDefs() = with(childNT) {
    outputs.map {
      varDef(
        decl = paramPlain(
          type = it.decl.type,
          name = it.varName,
        ),
        init = get(
          objName = aux.varName,
          fieldName = it.decl.id.name,
        ),
      )
    }
  }
}
