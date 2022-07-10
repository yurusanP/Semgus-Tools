package org.semgus.sketch.syntax

/**
 * Sketch expression.
 */
internal sealed class Expr : Syntax() {
  data class Ref(val id: Id) : Expr()
  data class App(val fn: Id, val args: Sequence<Expr>) : Expr()
  data class Get(val obj: Id, val field: Id) : Expr()
  data class Assign(val l: Id, val r: Expr) : Expr()
  data class Unary(val op: Op, val e: Expr) : Expr()
  data class Binary(val op: Op, val l: Expr, val r: Expr) : Expr()
  data class Nary(val op: Op, val es: Sequence<Expr>) : Expr()
  data class Ite(val i: Expr, val t: Expr, val e: Expr) : Expr()
  data class Choice(val es: Sequence<Expr>) : Expr()
  data class Forall(val binds: Sequence<Param>, val e: Expr) : Expr()
}

internal fun ref(id: Id) = Expr.Ref(id)
internal fun ref(param: Param) = ref(param.id)
internal fun refPlain(s: String) = ref(idPlain(s))
internal fun refPlain(n: Long) = ref(idPlain(n))

internal fun app(fn: Id, args: Sequence<Expr>) = Expr.App(fn, args)
internal fun appPlain(fnName: String, args: Sequence<Expr>) = app(idPlain(fnName), args)

internal fun argsBnded0(args: Sequence<Expr>) = args + refPlain("bnd")
internal fun argsBnded1(args: Sequence<Expr>) = args + binary(Op.MINUS, refPlain("bnd"), refPlain(1))

internal fun get(obj: Id, field: Id) = Expr.Get(obj, field)

internal fun assign(l: Id, r: Expr) = Expr.Assign(l, r)
internal fun assignPlain(lName: String, r: Expr) = assign(idPlain(lName), r)

internal fun unary(op: Op, e: Expr) = Expr.Unary(op, e)
internal fun binary(op: Op, l: Expr, r: Expr) = Expr.Binary(op, l, r)
internal fun nary(op: Op, es: Sequence<Expr>) = Expr.Nary(op, es)

internal fun ite(i: Expr, t: Expr, e: Expr) = Expr.Ite(i, t, e)

internal fun choice(es: Sequence<Expr>) = Expr.Choice(es)

internal fun forall(binds: Sequence<Param>, e: Expr) = Expr.Forall(binds, e)
