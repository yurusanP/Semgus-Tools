package org.semgus.sketch.translator

import org.semgus.java.`object`.SmtTerm
import org.semgus.java.`object`.SmtTerm.*
import org.semgus.sketch.base.syntax.*
import org.semgus.sketch.util.toSketchExprMap

/**
 * Evaluates SMT terms to Sketch expressions.
 */
internal fun SmtTerm.toSketchExpr(): Expr = when (this) {
  is Application -> (toSketchExprMap[this.name().name()] ?: { es ->
    call(
      fn = idPlain("target_Sem"),
      args = es.filterNot { it is Expr.Call },
    )
  }).invoke(
    this.arguments().asSequence().map {
      it.term().toSketchExpr()
    },
  )

  is Variable -> refPlain(this.name())

  is CNumber -> refPlain(this.value())

  is CString -> refPlain(this.value())

  // TODO: What about exists?
  is Quantifier -> forall(
    binds = this.bindings().asSequence().map { typedVar -> param(typedVar) },
    e = this.child().toSketchExpr(),
  )

  else -> throw IllegalStateException("Unexpected value: $this")
}
