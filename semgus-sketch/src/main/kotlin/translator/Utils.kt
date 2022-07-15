package org.semgus.sketch.translator

import org.semgus.java.`object`.Identifier
import org.semgus.java.`object`.Identifier.Index.NInt
import org.semgus.java.`object`.TypedVar
import org.semgus.java.problem.SemanticRule
import org.semgus.java.problem.SemgusNonTerminal
import org.semgus.sketch.base.syntax.*

// TODO: Should investigate later.
internal fun id(identifier: Identifier) = when(identifier.name()) {
  "Int" -> idPlain("int")
  "Bool" -> idPlain("bit")
  "BitVec" -> id("bit") {
    withBVSize((identifier.indices()[0] as NInt).value())
  }
  else -> idPlain(identifier.name())
}

internal fun param(typedVar: TypedVar) = paramPlain(
  type = id(identifier = typedVar.type()),
  name = typedVar.name(),
)

/**
 * First semantic rule of a SemGuS non-terminal.
 */
internal fun SemgusNonTerminal.firstSemRule(): SemanticRule =
  this.productions().values.first().semanticRules().first()
