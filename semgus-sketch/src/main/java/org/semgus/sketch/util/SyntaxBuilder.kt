package org.semgus.sketch.util

import org.semgus.sketch.syntax.*
import org.semgus.sketch.ir.Problem

internal data class SyntaxBuilder(val problem: Problem, val bnd: Long) {
  fun build(): Stmt {
    val ntsStmts = problem.nts.keys.asSequence()
      .flatMap { ntStmtsBuilder(syntaxBuilder = this, relName = it).build() }

    fun targetDef(): Stmt {
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
          bnd,
        ),
      )
    }

    fun targetRelDef(): Stmt {
      val ntVarName = problem.target.nt.ntVarName()
      val inputs = problem.target.nt.inputs()
      val outputs = problem.target.nt.outputs()

      val targetVarDef = varDef(
        decl = paramPlain(
          type = id(problem.target.nt.name) { withTmpNTType() },
          name = ntVarName,
        ),
        init = appPlain(problem.target.name, inputs.map(::ref)),
      )

      return fnDef(
        decl = param(
          type = idPlain("bit"),
          id = id(problem.target.nt.name) { withSem() },
        ),
        params = inputs + outputs,
        body = seq(
          sequenceOf(
            targetVarDef,
            aReturn(constraint(ntVarName, outputs)),
          ),
        ),
      )
    }

    fun harnessDef(): Stmt {
      // TODO: What about more than 1 forall... Or hybrid ones...?
      val forall = problem.constraints.singleOrNull().let {
        if (it is Expr.Forall) it else null
      }

      return fnDef(
        decl = paramPlain(
          type = id("void") { withHarness() },
          name = "sketch",
        ),
        params = forall?.binds ?: emptySequence(),
        body = seq(
          problem.constraints.map { constraint ->
            aAssert(constraint)
          },
        ),
      )
    }

    return seq(ntsStmts + targetDef() + targetRelDef() + harnessDef())
  }
}
