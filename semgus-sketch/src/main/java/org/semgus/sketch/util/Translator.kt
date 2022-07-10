package org.semgus.sketch.util

import org.semgus.java.`object`.RelationApp
import org.semgus.java.`object`.TypedVar
import org.semgus.java.problem.SemanticRule
import org.semgus.java.problem.SemgusNonTerminal
import org.semgus.java.problem.SemgusProblem
import org.semgus.java.problem.SemgusProduction
import org.semgus.sketch.ir.*
import org.semgus.sketch.ir.Target
import org.semgus.sketch.syntax.Expr
import org.semgus.sketch.syntax.Param
import org.semgus.sketch.syntax.id
import org.semgus.sketch.syntax.paramPlain

/**
 * Translates a SemGuS problem into a sketch problem.
 */
internal fun SemgusProblem.toSketchProblem(): Problem {
  val nts = this.nonTerminals.values.asSequence()
    .map { it.toSketchNonTerminal() }
    .associateBy(NonTerminal::relName)

  val rules = this.nonTerminals.values.asSequence()
    .map { semgusNT ->
      semgusNT.productions.values.asSequence()
        .flatMap(SemgusProduction::semanticRules)
        .map { it.toSketchRule(nts) }
    }
    .associateBy { rules -> rules.first().nt.relName }

  val targetRelName = this.targetNonTerminal.firstSemRule().head.name
  val target = nts[targetRelName]
    ?.let { Target(this.targetName, it) }
    ?: throw NoSuchElementException("Cannot find target in the non-terminal map.")

  val constraints = this.constraints.asSequence()
    .map { it.toExpr() }

  return Problem(
    nts,
    rules,
    target,
    constraints,
  )
}

/**
 * Translates a SemGuS non-terminal into a sketch non-terminal.
 */
internal fun SemgusNonTerminal.toSketchNonTerminal(): NonTerminal {
  val firstSemRule = this.firstSemRule()

  val fullVars = firstSemRule.head.arguments.asSequence()
    .map { typedVar ->
      firstSemRule.variables[typedVar.name]
        ?.let { typedVar to it }
        ?: throw NoSuchElementException("Cannot find matching annotated variables")
    }

  val ordVars = fullVars
    .map { (typedVar, annVar) ->
      Var(
        param(typedVar),
        typedVar.name,
        annVar.attributes.keys,
      )
    }

  val params = fullVars
    .map { (typedVar, annVar) -> param(typedVar = typedVar) to annVar }
    .flatMap { (param, annVar) ->
      annVar.attributes.keys.asSequence()
        .map { attr -> attr to param }
    }
    .groupingBy { (attr, _) -> attr }
    .fold(mutableListOf<Param>()) { acc, (_, param) ->
      acc.apply { this += param }
    }
    .mapValues { (_, mutable) -> mutable.asSequence() }

  return NonTerminal(
    this.name,
    firstSemRule.head.name,
    ordVars,
    params,
  )
}

/**
 * Translates a SemGuS rule into a sketch rule.
 */
internal fun SemanticRule.toSketchRule(nts: Map<String, NonTerminal>): Rule {
  // TODO: Why can't we have an expression in the childNT call?

  val nt = nts[this.head.name]
    ?: throw NoSuchElementException("Cannot find the head non-terminal")

  fun childNTs(bodyRel: RelationApp): NonTerminal {
    val childNT = nts[bodyRel.name]
      ?: throw NoSuchElementException("Cannot find the child non-terminal")

    val ordVars = childNT.ordVars
      .mapIndexed { i, v ->
        Var(
          v.decl,
          bodyRel.arguments[i].name,
          v.attrs,
        )
      }

    return NonTerminal(
      childNT.name,
      childNT.relName,
      ordVars,
      childNT.params,
    )
  }

  val childNTs = this.bodyRelations.asSequence()
    .map { childNTs(bodyRel = it) }

  // TODO: Use topological sort to resolve the bindings from the CHC constraint.
  val binds = emptyMap<String, Expr>()

  return Rule(
    nt,
    childNTs,
    binds,
  )
}

internal fun SemgusNonTerminal.firstSemRule(): SemanticRule =
  this.productions.values.first().semanticRules.first()

internal fun param(typedVar: TypedVar) =
  paramPlain(id(identifier = typedVar.type), typedVar.name)
