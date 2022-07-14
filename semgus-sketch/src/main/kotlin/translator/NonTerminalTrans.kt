package org.semgus.sketch.translator

import org.semgus.java.problem.SemgusNonTerminal
import org.semgus.sketch.base.ir.NonTerminal
import org.semgus.sketch.base.ir.Var

internal data class NonTerminalTrans(val semgusNT: SemgusNonTerminal) {
  private val firstSemRule = semgusNT.firstSemRule()

  private val semgusVars = firstSemRule.head().arguments().asSequence().map { typedVar ->
    typedVar to firstSemRule.variables()[typedVar.name()]!!
  }

  private val vars = semgusVars.map { (typedVar, annVar) ->
    Var(
      decl = param(typedVar),
      varName = typedVar.name(),
      attrs = annVar.attributes().keys,
    )
  }

  private val aux = vars.single { it.decl.type.name == semgusNT.name() }
  private val inputs = vars.filter { it.attrs.contains("input") }
  private val outputs = vars.filter { it.attrs.contains("output") }

  fun trans() = NonTerminal(
    name = semgusNT.name(),
    semName = firstSemRule.head().name(),
    vars,
    aux,
    inputs,
    outputs,
  )
}

internal fun SemgusNonTerminal.toSketchNonTerminal() = NonTerminalTrans(this).trans()
