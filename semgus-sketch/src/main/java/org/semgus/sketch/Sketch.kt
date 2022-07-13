package org.semgus.sketch

import org.semgus.java.problem.SemgusProblem
import org.semgus.pretty.stringPretty
import org.semgus.sketch.base.syntax.Syntax
import org.semgus.sketch.codegen.SyntaxGen
import org.semgus.sketch.translator.toSketchProblem

/**
 * Sketch program.
 */
class Sketch {
  internal lateinit var syntax: Syntax

  fun dump() = syntax.codify().stringPretty()
}

fun fromSemgusProblem(semgusProblem: SemgusProblem, bnd: Int) = Sketch().apply {
  val sketchProblem = semgusProblem.toSketchProblem()
  this.syntax = SyntaxGen(sketchProblem, bnd).gen()
}
