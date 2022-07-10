package org.semgus.sketch.util

import org.semgus.java.`object`.SmtTerm
import org.semgus.java.`object`.SmtTerm.Application
import org.semgus.java.`object`.SmtTerm.Application.TypedTerm
import org.semgus.java.`object`.SmtTerm.CNumber
import org.semgus.java.`object`.SmtTerm.CString
import org.semgus.java.`object`.SmtTerm.Quantifier
import org.semgus.java.`object`.SmtTerm.Variable
import org.semgus.sketch.syntax.*

/**
 * Evaluates SMT terms to Sketch expressions.
 */
internal fun SmtTerm.toExpr(): Expr = when (this) {
  is Application -> (fns[this.name.name] ?: { appPlain(fnName = this.name.name, args = it.asSequence()) })
    .invoke(
      this.arguments.asSequence()
        .map(TypedTerm::term)
        .map(SmtTerm::toExpr)
        .toList(),
    )
  is Variable -> refPlain(this.name)
  is CNumber -> refPlain(this.value)
  is CString -> refPlain(this.value)
  // TODO: What about exists?
  is Quantifier -> forall(
    binds = this.bindings.asSequence().map { typedVar -> param(typedVar) },
    e = this.child.toExpr(),
  )
  else -> throw IllegalStateException("Unexpected value: $this")
}

/**
 * The helper map for evaluation.
 */
private val fns = mapOf<String, (List<Expr>) -> Expr>(
  // Core Theory
  "true" to { refPlain("true") },
  "false" to { refPlain("false") },
  // TODO: Support Nary.
  "and" to { (l, r) -> binary(Op.AND, l, r) },
  "or" to { (l, r) -> binary(Op.OR, l, r) },
  "not" to { (x) -> unary(Op.NOT, x) },
  "!" to { (x) -> unary(Op.NOT, x) },
  "xor" to { (l, r) -> binary(Op.XOR, l, r) },
  // TODO: "=>"?
  "=" to { (l, r) -> binary(Op.EQ, l, r) },
  // TODO: "distinct"?
  "ite" to { (i, t, e) -> ite(i, t, e) },

  // Ints Theory
  "-" to {
    if (it.size == 1) {
      val (x) = it
      unary(Op.MINUS, x)
    } else {
      val (l, r) = it
      binary(Op.MINUS, l, r)
    }
  },
  "+" to { (l, r) -> binary(Op.PLUS, l, r) },
  "*" to { (l, r) -> binary(Op.TIMES, l, r) },
  "div" to { (l, r) -> binary(Op.DIV, l, r) },
  "mod" to { (l, r) -> binary(Op.MOD, l, r) },
  "abs" to { (x) -> ite(binary(Op.LT, x, refPlain(0)), unary(Op.MINUS, x), x) },
  "<=" to { (l, r) -> binary(Op.LTE, l, r) },
  "<" to { (l, r) -> binary(Op.LT, l, r) },
  ">=" to { (l, r) -> binary(Op.GTE, l, r) },
  ">" to { (l, r) -> binary(Op.GT, l, r) },

  // TODO: Bit Vectors Theory

  // TODO: Strings Theory
)
