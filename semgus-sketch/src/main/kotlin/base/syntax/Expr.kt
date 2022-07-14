package org.semgus.sketch.base.syntax

import org.semgus.sketch.base.syntax.Op.*

/**
 * Sketch expression.
 */
internal sealed class Expr : Syntax() {
  data class Ref(val id: Id) : Expr()
  data class Call(val fn: Id, val args: Sequence<Expr>) : Expr()
  data class Dot(val l: Id, val r: Id) : Expr()
  data class Assign(val l: Id, val r: Expr) : Expr()
  data class Unary(val op: Op, val e: Expr) : Expr()
  data class Binary(val op: Op, val l: Expr, val r: Expr) : Expr()
  data class Nary(val op: Op, val es: Sequence<Expr>) : Expr()
  data class Ite(val i: Expr, val t: Expr, val e: Expr) : Expr()
  data class Choice(val es: Sequence<Expr>) : Expr()
  data class Forall(val binds: Sequence<Param>, val e: Expr) : Expr()
}

internal fun ref(id: Id) = Expr.Ref(id)
internal fun refPlain(s: String) = ref(idPlain(s))
internal fun refPlain(n: Number) = ref(idPlain(n))

internal fun call(fn: Id, args: Sequence<Expr>) = Expr.Call(fn, args)
internal fun callPlain(fnName: String, args: Sequence<Expr>) = call(idPlain(fnName), args)

internal fun callWithFieldNames(fn: Id, args: Sequence<Expr>, fieldNames: Sequence<String>) = call(
  fn,
  args = args.zip(fieldNames)
    .map { (arg, fieldName) ->
      assign(id(fieldName) { withField() }, arg)
    },
)

internal fun argsBnded0(args: Sequence<Expr>) = args + refPlain("bnd")
internal fun argsBnded1(args: Sequence<Expr>) = args + binary(MINUS, refPlain("bnd"), refPlain(1))

internal fun dot(l: Id, r: Id) = Expr.Dot(l, r)
internal fun get(objName: String, fieldName: String) = dot(idPlain(objName), id(fieldName) { withField() } )

internal fun assign(l: Id, r: Expr) = Expr.Assign(l, r)

internal fun unary(op: Op, e: Expr) = Expr.Unary(op, e)
internal fun binary(op: Op, l: Expr, r: Expr) = Expr.Binary(op, l, r)
internal fun nary(op: Op, es: Sequence<Expr>) = Expr.Nary(op, es)

internal fun ite(i: Expr, t: Expr, e: Expr) = Expr.Ite(i, t, e)

internal fun choice(es: Sequence<Expr>) = Expr.Choice(es)

internal fun forall(binds: Sequence<Param>, e: Expr) = Expr.Forall(binds, e)
