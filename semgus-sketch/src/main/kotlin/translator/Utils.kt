package org.semgus.sketch.translator

import org.semgus.java.`object`.TypedVar
import org.semgus.java.problem.SemanticRule
import org.semgus.java.problem.SemgusNonTerminal
import org.semgus.sketch.base.syntax.id
import org.semgus.sketch.base.syntax.paramPlain

internal fun param(typedVar: TypedVar) = paramPlain(
  type = id(identifier = typedVar.type()),
  name = typedVar.name(),
)

/**
 * First semantic rule of a SemGuS non-terminal.
 */
internal fun SemgusNonTerminal.firstSemRule(): SemanticRule =
  this.productions().values.first().semanticRules().first()
