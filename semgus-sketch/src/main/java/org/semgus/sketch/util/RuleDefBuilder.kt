package org.semgus.sketch.util

import org.semgus.sketch.syntax.*
import org.semgus.sketch.ir.NonTerminal
import org.semgus.sketch.ir.Rule
import org.semgus.sketch.ir.Var

internal data class RuleDefBuilder(
  val ntStmtsBuilder: NTStmtsBuilder,
  val ruleIndex: Int,
  val rule: Rule,
) {
  fun build(): Stmt {
    return fnDef(
      decl = param(
        type = id(ntStmtsBuilder.nt.name) { withTmpNTType(); withGenerator() },
        id = id(ntStmtsBuilder.nt.name) { withRuleIndex(ruleIndex) },
      ),
      ntStmtsBuilder.params,
      body(),
    )
  }

  private fun body(): Stmt {
    val childNTVarsStmts = rule.childNTs
      .flatMap { childNTVarStmts(childNT = it) }

    return seq(childNTVarsStmts + ntVarStmts())
  }

  private fun childNTVarStmts(childNT: NonTerminal): Sequence<Stmt> {
    val childNTVarName = childNT.ntVarName()

    val childNTVarDef = varDef(
      decl = paramPlain(id(childNT.name) { withTmpNTType() }, childNTVarName),
      init = appPlain(fnName = childNT.name, args = argsBnded1(ntStmtsBuilder.inputs.map(::ref))),
    )

    fun childNTSubst(v: Var): Stmt? =
      if (!v.attrs.contains("output")) null
      else varDef(
        decl = paramPlain(type = v.decl.type, name = v.varName),
        init = get(obj = idPlain(childNTVarName), field = idPlain(v.decl.id.name)),
      )

    val childNTSubsts = childNT.ordVars
      .mapNotNull { v -> childNTSubst(v) }

    return sequenceOf(childNTVarDef) + childNTSubsts
  }

  private fun ntVarStmts(): Sequence<Stmt> {
    val ntVarName = ntStmtsBuilder.nt.ntVarName()

    // TODO: args using constraints.
    val ntVarDef = varDef(
      decl = paramPlain(
        type = id(ntStmtsBuilder.nt.name) { withTmpNTType() },
        name = ntVarName,
      ),
      init = app(
        fn = id(ntStmtsBuilder.nt.name) { withTmpNTType() },
        args = emptySequence(),
      ),
    )

    return sequenceOf(ntVarDef, aReturn(refPlain(ntVarName)))
  }
}
