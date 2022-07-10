package org.semgus.sketch

import org.semgus.java.problem.SemgusProblem
import org.semgus.pretty.stringPretty
import org.semgus.sketch.syntax.Syntax
import org.semgus.sketch.util.toSketchProblem
import org.semgus.sketch.util.SyntaxBuilder

/**
 * Sketch program.
 */
class Sketch {
  private lateinit var syntax: Syntax

  fun dump() = syntax.codify().stringPretty()

  companion object {
    fun fromSemgusProblem(semgusProblem: SemgusProblem, bnd: Int): Sketch {
      val sketchProblem = semgusProblem.toSketchProblem()
      return Sketch().apply {
        this.syntax = SyntaxBuilder(sketchProblem, bnd).build()
      }
    }
  }
}
