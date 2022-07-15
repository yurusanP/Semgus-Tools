package org.semgus.sketch.translator

import org.semgus.java.`object`.SmtTerm
import org.semgus.java.`object`.SmtTerm.*
import org.semgus.sketch.base.ir.Target
import org.semgus.sketch.base.syntax.*
import org.semgus.sketch.base.syntax.Op.*

internal data class ExprTrans(
  val term: SmtTerm,
  val target: Target?,
) {
  fun trans() = when (term) {
    is Application -> term.toSketchExpr(target).invoke(
      term.arguments().asSequence().map {
        it.term().toSketchExpr(target)
      },
    )

    is Variable -> refPlain(term.name())

    is CNumber -> refPlain(term.value())

    is CString -> refPlain(term.value())

    // TODO: What about exists?
    is Quantifier -> forall(
      binds = term.bindings().asSequence().map { typedVar -> param(typedVar) },
      e = term.child().toSketchExpr(target),
    )

    is CBitVector -> bitvec(term.value(), term.size())

    else -> throw IllegalStateException("Unexpected value: $term")
  }
}

/**
 * Evaluates SMT terms to Sketch expressions.
 */
internal fun SmtTerm.toSketchExpr(target: Target? = null): Expr =
  ExprTrans(this, target).trans()

private fun Application.toSketchExpr(target: Target?): (Sequence<Expr>) -> Expr {
  return when (this.name().name()) {
    // Core Theory
    "true" -> { _ -> refPlain("true") }
    "false" -> { _ -> refPlain("false") }
    "and" -> { es -> nary(AND, es) }
    "or" -> { es -> nary(OR, es) }
    "not" -> { (iter) -> unary(NOT, iter.next()) }
    "!" -> { (iter) -> unary(NOT, iter.next()) }
    "xor" -> { (iter) -> binary(XOR, iter.next(), iter.next()) }
    // TODO: "=>"?
    "=" -> { (iter) -> binary(EQ, iter.next(), iter.next()) }
    // TODO: "distinct"?
    "ite" -> { (iter) -> ite(iter.next(), iter.next(), iter.next()) }

    // Ints Theory
    "-" -> { (iter) ->
      val next = iter.next()
      if (!iter.hasNext()) unary(MINUS, next)
      else binary(MINUS, next, iter.next())
    }
    "+" -> { (iter) -> binary(PLUS, iter.next(), iter.next()) }
    "*" -> { (iter) -> binary(TIMES, iter.next(), iter.next()) }
    "div" -> { (iter) -> binary(DIV, iter.next(), iter.next()) }
    "mod" -> { (iter) -> binary(MOD, iter.next(), iter.next()) }
    "abs" -> { (iter) ->
      val next = iter.next()
      ite(binary(Op.LT, next, refPlain(0)), unary(Op.MINUS, next), next)
    }
    "<=" -> { (iter) -> binary(LTE, iter.next(), iter.next()) }
    "<" -> { (iter) -> binary(LT, iter.next(), iter.next()) }
    ">=" -> { (iter) -> binary(GTE, iter.next(), iter.next()) }
    ">" -> { (iter) -> binary(GT, iter.next(), iter.next()) }

    // TODO: Bit Vectors Theory
//    "extract" -> { (iter) -> unary(`___`, iter.next()) }
//    "concat" -> { (iter) -> binary(`___`, iter.next(), iter.next()) }
    "bvnot" -> { (iter) -> unary(NOT, iter.next()) }
//    "bvneg" -> { (iter) -> unary(`___`, iter.next()) }
    "bvand" -> { (iter) -> binary(BVAND, iter.next(), iter.next()) }
    "bvor" -> { (iter) -> binary(BVOR, iter.next(), iter.next()) }
    "bvadd" -> { (iter) -> binary(PLUS, iter.next(), iter.next()) }
//    "bvmul" -> { (iter) -> binary(`___`, iter.next(), iter.next()) }
//    "bvudiv" -> { (iter) -> binary(`___`, iter.next(), iter.next()) }
//    "bvurem" -> { (iter) -> binary(`___`, iter.next(), iter.next()) }
    "bvshl" -> { (iter) -> binary(SHL, iter.next(), iter.next()) }
    "bvlshr" -> { (iter) -> binary(SHR, iter.next(), iter.next()) }
    "bvult" -> { (iter) -> binary(LT, iter.next(), iter.next()) }

    // TODO: Strings Theory

    target?.nt?.semName -> { es ->
      callPlain(
        fnName = "Target_Sem",
        args = es.filterNot { it is Expr.Call && it.fn.name == target!!.name },
      )
    }
    target?.name -> { es -> callPlain(target!!.name, es) }
    else -> throw IllegalStateException("Unsupported operation: ${this.name().name()}")
  }
}

/**
 * Makes destructuring a sequence in lambda possible.
 */
private operator fun <T> Sequence<T>.component1(): Iterator<T> {
  return this.iterator()
}
