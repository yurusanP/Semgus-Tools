package org.semgus.sketch.translator

import org.semgus.java.`object`.TypedVar
import org.semgus.java.problem.SemanticRule
import org.semgus.sketch.base.ir.NonTerminal
import org.semgus.sketch.base.ir.Rule
import org.semgus.sketch.base.ir.subst
import org.semgus.sketch.base.ir.varNames
import org.semgus.sketch.util.MutableGraph

internal data class RuleTrans(
  val semRule: SemanticRule,
  val index: Int,
  val semNameToNTs: Map<String, NonTerminal>,
) {
  private val headNT = semNameToNTs[semRule.head().name()]!!

  // TODO: Just sort them topologically here.
  // TODO: Why can't we have an expression in the childNT call?
  private val childNTs = semRule.bodyRelations().asSequence().map { bodyRel ->
    val newVarNames = bodyRel.arguments().asSequence().map(TypedVar::name)
    semNameToNTs[bodyRel.name()]!!.subst(newVarNames)
  }

  private val varGraph = MutableGraph<String, Nothing>().apply {
    childNTs.forEach { childNT ->
      childNT.vars.varNames().forEach { varName ->
        this.addVertex(varName)
      }

      childNT.inputs.varNames().forEach { inputVarName ->
        this.addEdge(inputVarName, childNT.aux.varName)
      }

      childNT.outputs.varNames().forEach { outputVarName ->
        this.addEdge(childNT.aux.varName, outputVarName)
      }
    }
  }

  private val varRanks = varGraph.toSortedList()
    .mapIndexed { index, varName -> varName to index }
    .toMap()

  private val varBinds = semRule.constraint().toVarBinds(headNT, childNTs)

  fun trans() = Rule(
    index,
    headNT,
    childNTs,
    varRanks,
    varBinds,
  )
}

/**
 * Translates a SemGuS rule into a Sketch rule.
 */
internal fun SemanticRule.toSketchRule(index: Int, semNameToNTs: Map<String, NonTerminal>) =
  RuleTrans(this, index, semNameToNTs).trans()
