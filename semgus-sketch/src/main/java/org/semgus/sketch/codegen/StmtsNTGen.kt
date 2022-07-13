package org.semgus.sketch.codegen

import org.semgus.sketch.base.ir.*
import org.semgus.sketch.base.syntax.*

/**
 * Generator of Sketch statements about non-terminals given a non-terminal.
 */
internal data class StmtsNTGen(
  val syntaxGen: SyntaxGen,
  val semName: String,
) {
  private val nt = syntaxGen.problem.semNameToNT[semName]!!
  private val rules = syntaxGen.problem.semNameToRules[semName]!!

  internal val params = paramsBnded0(nt.inputs.decls())

  /**
   * Sketch statements about the non-terminal.
   */
  fun gen() = with(ntTypeDef()) {
    sequenceOf(this) + ntDef() + ruleDefs()
  }

  /**
   * Non-terminal type definition.
   */
  private fun ntTypeDef() = ntTypeDef(nt.name, nt.outputs.decls())

  /**
   * Non-terminal generator function definition.
   */
  private fun ntDef() = with(nt) {
    val retExpr = choice(
      rules.map { rule ->
        call(
          fn = id(name) { withRuleIndex(rule.index) },
          args = argsBnded0(nt.inputs.varNames().map(::refPlain)),
        )
      },
    )

    fnDef(
      decl = paramPlain(
        type = id(name) { withTmpNTType(); withGenerator() },
        name,
      ),
      params,
      body = seqWith(aBndChk()) { this + aReturn(retExpr) },
    )
  }

  /**
   * Non-terminal rules definitions.
   */
  private fun ruleDefs(): Sequence<Stmt> = rules.map {
    RuleDefGen(stmtsNTGen = this, rule = it).gen()
  }
}
