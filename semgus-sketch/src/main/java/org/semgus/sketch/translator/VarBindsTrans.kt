package org.semgus.sketch.translator

import org.semgus.java.`object`.SmtTerm
import org.semgus.java.`object`.SmtTerm.Application
import org.semgus.java.`object`.SmtTerm.Application.TypedTerm
import org.semgus.java.`object`.SmtTerm.Variable
import org.semgus.sketch.base.ir.NonTerminal
import org.semgus.sketch.base.syntax.*

internal data class BindsTrans(
  val term: SmtTerm,
  val headNT: NonTerminal,
  val childNTs: Sequence<NonTerminal>,
) {
  fun trans(): Map<String, Expr> = mutableMapOf<String, Expr>().apply {
    fun eval(term: SmtTerm) {
      when (term.opName()) {
        "true", "false" -> return
        "and" -> with(term.subTerms()) {
          this.forEach { eval(it) }
        }
        "or" -> TODO()
        "not" -> term.subTerm(0).let { not0 ->
          when (not0.opName()) {
            "true", "false" -> return
            "and" -> TODO()
            "or" -> TODO()
            "not" -> eval(not0.subTerm(0))
          }
        }
        "=" -> with(term.subTerms()) {
          val (lhs, rhs) = this.partition { it is Variable }
          lhs.forEach { l ->
            lhs.forEach { l2 ->
              this@apply.put(l.toString(), refPlain(l2.toString()))
            }
            rhs.forEach { r ->
              this@apply.put(l.toString(), r.toSketchExpr())
            }
          }
        }
      }
    }
    eval(term)
  }
}

/**
 * Evaluates an SMT to Sketch bindings.
 */
internal fun SmtTerm.toVarBinds(headNT: NonTerminal, childNTs: Sequence<NonTerminal>) =
  BindsTrans(this, headNT, childNTs).trans()

private fun SmtTerm.opName() = with(this as Application) {
  this.name().name()
}

private fun SmtTerm.subTerms() = with(this as Application) {
  this.arguments().asSequence().map(TypedTerm::term)
}

private fun SmtTerm.subTerm(index: Int) = with(this as Application) {
  this.arguments()[index].term()
}
