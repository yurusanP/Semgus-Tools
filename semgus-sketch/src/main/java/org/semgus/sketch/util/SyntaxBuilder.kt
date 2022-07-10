package org.semgus.sketch.util

import org.semgus.sketch.syntax.*
import org.semgus.sketch.ir.Problem

internal data class SyntaxBuilder(val problem: Problem, val bnd: Long) {
  fun build(): Stmt {
    val ntsStmts = problem.nts.keys.asSequence()
      .flatMap { ntStmtsBuilder(syntaxBuilder = this, relName = it).build() }

    fun targetDef(): Stmt.FnDef {
      val inputs = problem.target.nt.inputs()
      return fnDef(
        decl = paramPlain(
          type = id(problem.target.nt.name) { withTmpNTType() },
          problem.target.name,
        ),
        params = inputs,
        body = sBndDefed(
          aReturn(
            appPlain(
              fnName = problem.target.nt.name,
              args = argsBnded0(inputs.map(::ref)),
            ),
          ),
          bnd
        )
      )
    }

    // TODO: Harness
    val harnessDef = skip()

    return seq(ntsStmts + targetDef() + harnessDef)
  }
}
