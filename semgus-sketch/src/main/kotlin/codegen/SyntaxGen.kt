package org.semgus.sketch.codegen

import org.semgus.sketch.base.ir.*
import org.semgus.sketch.base.syntax.*

/**
 * Sketch syntax generator.
 */
internal data class SyntaxGen(
  val problem: Problem,
  val bnd: Int,
) {
  /**
   * Sketch syntax.
   */
  fun gen() = seq(stmtsNTs() + targetDef() + targetSemDef() + harnessDef())

  /**
   * Sketch statements about non-terminals.
   */
  private fun stmtsNTs() = problem.semNameToNT.keys.asSequence().flatMap {
    StmtsNTGen(syntaxGen = this, semName = it).gen()
  }

  /**
   * Target function definition.
   */
  private fun targetDef() = with(problem.target) {
    val retExpr = callPlain(
      fnName = nt.name,
      args = argsBnded0(nt.inputs.varNames().map(::refPlain)),
    )

    fnDef(
      decl = paramPlain(
        type = id(nt.name) { withTmpNTType() },
        name,
      ),
      params = nt.inputs.decls(),
      body = seqWith(bndDef(bnd)) { this + aReturn(retExpr) },
    )
  }

  /**
   * Target semantic relation definition.
   */
  private fun targetSemDef() = with(problem.target) {
    val targetAuxDef = varDef(
      decl = paramPlain(
        type = id(nt.name) { withTmpNTType() },
        name = nt.aux.varName,
      ),
      init = callPlain(
        fnName = name,
        args = nt.inputs.varNames().map(::refPlain),
      ),
    )

    val retExpr = nary(
      op = Op.AND,
      es = nt.outputs.map { (decl, varName) ->
        val l = get(objName = nt.aux.varName, fieldName = decl.id.name)
        val r = refPlain(varName)
        binary(Op.EQ, l, r)
      },
    )

    fnDef(
      decl = bitPlain("Target_Sem"),
      params = nt.vars.filterNot { it == nt.aux }.decls(),
      body = seqWith(targetAuxDef) { this + aReturn(retExpr) },
    )
  }

  /**
   * Harness function definition.
   */
  private fun harnessDef() = with(problem.constraints) {
    // TODO: What about more than 1 forall... Or hybrid ones...?
    val forall = this.singleOrNull().let {
      if (it is Expr.Forall) it else null
    }

    fnDef(
      decl = paramPlain(
        type = id("void") { withHarness() },
        name = "sketch",
      ),
      params = forall?.binds ?: emptySequence(),
      body = seq(this.map { aAssert(it) }),
    )
  }
}
