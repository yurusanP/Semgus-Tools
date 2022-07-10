package org.semgus.sketch.util

import org.semgus.sketch.syntax.*
import org.semgus.sketch.ir.Problem

internal data class SyntaxBuilder(val problem: Problem, val bnd: Int) {
  fun build(): Stmt {
    val ntsStmts = problem.nts.keys.asSequence()
      .flatMap { ntStmtsBuilder(syntaxBuilder = this, relName = it).build() }

    // TODO: Target
    val targetDef = skip()

    // TODO: Harness
    val harnessDef = skip()

    return seq(ntsStmts + targetDef + harnessDef)
  }
}
