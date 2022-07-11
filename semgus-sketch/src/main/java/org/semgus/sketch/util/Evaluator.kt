package org.semgus.sketch.util

import org.semgus.java.`object`.SmtTerm
import org.semgus.java.`object`.SmtTerm.Application
import org.semgus.java.`object`.SmtTerm.CNumber
import org.semgus.java.`object`.SmtTerm.CString
import org.semgus.java.`object`.SmtTerm.Quantifier
import org.semgus.java.`object`.SmtTerm.Variable
import org.semgus.sketch.syntax.*
import org.semgus.sketch.syntax.Op.*

/**
 * Evaluates SMT terms to Sketch expressions.
 */
internal fun SmtTerm.toExpr(): Expr = when (this) {
  is Application -> (fns[this.name().name()] ?: TODO("Not supported theory $this"))
    .invoke(this.arguments().asSequence().map { it.term().toExpr() })
  is Variable -> refPlain(this.name())
  is CNumber -> refPlain(this.value())
  is CString -> refPlain(this.value())
  // TODO: What about exists?
  is Quantifier -> forall(
    binds = this.bindings().asSequence().map { typedVar -> param(typedVar) },
    e = this.child().toExpr(),
  )
  else -> throw IllegalStateException("Unexpected value: $this")
}

/**
 * The helper map for evaluation.
 */
internal val fns = mutableMapOf<String, (Sequence<Expr>) -> Expr>(
  // Core Theory
  "true" to { refPlain("true") },
  "false" to { refPlain("false") },
  "and" to { es -> nary(AND, es) },
  "or" to { es -> nary(OR, es) },
  "not" to { (iter) -> unary(NOT, iter.next()) },
  "!" to { (iter) -> unary(NOT, iter.next()) },
  "xor" to { (iter) -> binary(XOR, iter.next(), iter.next()) },
  // TODO: "=>"?
  "=" to { (iter) -> binary(EQ, iter.next(), iter.next()) },
  // TODO: "distinct"?
  "ite" to { (iter) -> ite(iter.next(), iter.next(), iter.next()) },

  // Ints Theory
  "-" to { (iter) ->
    val next = iter.next()
    if (!iter.hasNext()) unary(MINUS, next)
    else binary(MINUS, next, iter.next())
  },
  "+" to { (iter) -> binary(PLUS, iter.next(), iter.next()) },
  "*" to { (iter) -> binary(TIMES, iter.next(), iter.next()) },
  "div" to { (iter) -> binary(DIV, iter.next(), iter.next()) },
  "mod" to { (iter) -> binary(MOD, iter.next(), iter.next()) },
  "abs" to { (iter) ->
    val next = iter.next()
    ite(binary(LT, next, refPlain(0)), unary(MINUS, next), next)
  },
  "<=" to { (iter) -> binary(LTE, iter.next(), iter.next()) },
  "<" to { (iter) -> binary(LT, iter.next(), iter.next()) },
  ">=" to { (iter) -> binary(GTE, iter.next(), iter.next()) },
  ">" to { (iter) -> binary(GT, iter.next(), iter.next()) },

  // TODO: Bit Vectors Theory

  // TODO: Strings Theory
)

/**
 * Makes destructuring a sequence in lambda possible.
 */
private operator fun <T> Sequence<T>.component1(): Iterator<T> {
  return this.iterator()
}
