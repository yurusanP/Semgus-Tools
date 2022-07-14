package org.semgus.sketch.codegen

import org.semgus.sketch.base.ir.*
import org.semgus.sketch.base.syntax.*

/**
 * Generator of a rule generator function definition given a rule.
 */
internal data class RuleDefGen(
  val stmtsNTGen: StmtsNTGen,
  val rule: Rule,
) {
  /**
   * Rule generator function definition.
   */
  fun gen() = with(rule) {
    fnDef(
      decl = param(
        type = id(headNT.name) { withTmpNTType(); withGenerator() },
        id = id(headNT.name) { withRuleIndex(rule.index) },
      ),
      params = stmtsNTGen.params,
      body = seq(stmtsChildNTs() + stmtsHeadNT()),
    )
  }

  /**
   * Statements about child non-terminals.
   */
  private fun stmtsChildNTs() = rule.childNTs.flatMap {
    StmtsChildNTGen(ruleDefGen = this, childNT = it).gen()
  }

  /**
   * Statements about a head non-terminal.
   */
  private fun stmtsHeadNT() = with(rule.headNT) {
    val ntType = id(name) { withTmpNTType() }

    val auxDef = varDef(
      decl = paramPlain(
        type = ntType,
        name = aux.varName
      ),
      init = callWithFieldNames(
        fn = ntType,
        args = outputs.varNames().map { rule.varBinds[it] ?: refPlain(it) } ,
        fieldNames = outputs.declNames()
      )
    )

    sequenceOf(auxDef, aReturn(refPlain(aux.varName)))
  }
}
