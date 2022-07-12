package org.semgus.sketch.util

import org.semgus.sketch.syntax.*
import org.semgus.sketch.ir.NonTerminal
import org.semgus.sketch.ir.Rule

internal data class NTStmtsBuilder(
  val syntaxBuilder: SyntaxBuilder,
  val relName: String,
  val nt: NonTerminal,
  val inputs: Sequence<Param>,
  val outputs: Sequence<Param>,
  val params: Sequence<Param>,
  val rules: Sequence<Rule>,
) {
  fun build(): Sequence<Stmt> {
    val typeDef = ntTypeDef(nt.name, outputs)

    val ruleDefs = rules.mapIndexed { i, rules ->
      RuleDefBuilder(ntStmtsBuilder = this, ruleIndex = i, rules).build()
    }

    return sequenceOf(typeDef, ntDef()) + ruleDefs
  }

  private fun ntDef(): Stmt {
    val choice = choice(
      (0 until rules.count()).asSequence()
        .map { id(nt.name) { withRuleIndex(it) } }
        .map { call(fn = it, args = params.map(::ref)) },
    )

    return fnDef(
      decl = paramPlain(
        type = id(nt.name) { withTmpNTType(); withGenerator() },
        name = nt.name,
      ),
      params,
      body = sBndChked(aReturn(choice)),
    )
  }
}

internal fun ntStmtsBuilder(syntaxBuilder: SyntaxBuilder, relName: String): NTStmtsBuilder {
  val nt = syntaxBuilder.problem.nts[relName]
    ?: throw NoSuchElementException("Cannot find the non-terminal to build statements.")
  val inputs = nt.inputs()
  val outputs = nt.outputs()

  return NTStmtsBuilder(
    syntaxBuilder,
    relName,
    nt,
    inputs,
    outputs,
    params = paramsBnded0(inputs),
    rules = syntaxBuilder.problem.rules[relName]
      ?: throw NoSuchElementException("Cannot find the non-terminal rules to build statements."),
  )
}
