package org.semgus.sketch.util

import org.semgus.sketch.base.syntax.*

/**
 * The map for evaluating SMT terms into Sketch expressions.
 */
internal val toSketchExprMap = mapOf<String, (Sequence<Expr>) -> Expr>(
  // Core Theory
  "true" to { refPlain("true") },
  "false" to { refPlain("false") },
  "and" to { es -> nary(Op.AND, es) },
  "or" to { es -> nary(Op.OR, es) },
  "not" to { (iter) -> unary(Op.NOT, iter.next()) },
  "!" to { (iter) -> unary(Op.NOT, iter.next()) },
  "xor" to { (iter) -> binary(Op.XOR, iter.next(), iter.next()) },
  // TODO: "=>"?
  "=" to { (iter) -> binary(Op.EQ, iter.next(), iter.next()) },
  // TODO: "distinct"?
  "ite" to { (iter) -> ite(iter.next(), iter.next(), iter.next()) },

  // Ints Theory
  "-" to { (iter) ->
    val next = iter.next()
    if (!iter.hasNext()) unary(Op.MINUS, next)
    else binary(Op.MINUS, next, iter.next())
  },
  "+" to { (iter) -> binary(Op.PLUS, iter.next(), iter.next()) },
  "*" to { (iter) -> binary(Op.TIMES, iter.next(), iter.next()) },
  "div" to { (iter) -> binary(Op.DIV, iter.next(), iter.next()) },
  "mod" to { (iter) -> binary(Op.MOD, iter.next(), iter.next()) },
  "abs" to { (iter) ->
    val next = iter.next()
    ite(binary(Op.LT, next, refPlain(0)), unary(Op.MINUS, next), next)
  },
  "<=" to { (iter) -> binary(Op.LTE, iter.next(), iter.next()) },
  "<" to { (iter) -> binary(Op.LT, iter.next(), iter.next()) },
  ">=" to { (iter) -> binary(Op.GTE, iter.next(), iter.next()) },
  ">" to { (iter) -> binary(Op.GT, iter.next(), iter.next()) },

  // TODO: Bit Vectors Theory

  // TODO: Strings Theory
)

/**
 * Makes destructuring a sequence in lambda possible.
 */
private operator fun <T> Sequence<T>.component1(): Iterator<T> {
  return this.iterator()
}
