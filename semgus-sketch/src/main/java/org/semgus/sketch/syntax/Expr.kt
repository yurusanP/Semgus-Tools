package org.semgus.sketch.syntax

import org.semgus.sketch.syntax.Op.*

/**
 * Sketch expression.
 */
internal sealed class Expr : Syntax() {
  data class Ref(val id: Id) : Expr()
  data class App(val fn: Id, val args: Sequence<Expr>) : Expr()
  data class Get(val obj: Id, val field: Id) : Expr()
  data class Assign(val l: Id, val r: Expr) : Expr()
  sealed class NaryLike(open val op: Op, open val es: Sequence<Expr>) : Expr()
  data class Unary(override val op: Op, val e: Expr) : NaryLike(op, sequenceOf(e))
  data class Binary(override val op: Op, val l: Expr, val r: Expr) : NaryLike(op, sequenceOf(l, r))
  data class Nary(override val op: Op, override val es: Sequence<Expr>) : NaryLike(op, es)
  data class Ite(val i: Expr, val t: Expr, val e: Expr) : Expr()
  data class Choice(val es: Sequence<Expr>) : Expr()
  data class Forall(val binds: Sequence<Param>, val e: Expr) : Expr()

  fun toMap(outVarNames: Set<String>): Map<String, Expr> {
    val binds = mutableMapOf<String, Expr>()
    fun aux(e: Expr) {
      when (e) {
        is NaryLike -> {
          when (e.op) {
            AND -> e.es.forEach { aux(it) }
            OR -> TODO("Simplify formula in propositional logic")
            NOT -> TODO("Simplify formula in propositional logic")
            XOR -> TODO("Simplify formula in propositional logic")
            EQ -> {
              val (outExprs, nonOutExprs) = e.es.partition { it is Ref && outVarNames.contains(it.id.name) }
              outExprs
                .map { (it as Ref).id.name }
                .forEach { outIdName ->
                  nonOutExprs.forEach { nonOutExpr ->
                    if (binds.containsKey(outIdName)) TODO("Check if resolution is possible.")
                    binds[outIdName] = nonOutExpr
                  }
                }
            }
            else -> throw IllegalStateException("Cannot resolve bindings.")
          }
        }
        else -> throw IllegalStateException("Not an expression evaluated from an SMT term.")
      }
    }
    aux(this)
    return binds.toMap()
  }
}

internal fun ref(id: Id) = Expr.Ref(id)
internal fun ref(param: Param) = ref(param.id)
internal fun refPlain(s: String) = ref(idPlain(s))
internal fun refPlain(n: Long) = ref(idPlain(n))

internal fun app(fn: Id, args: Sequence<Expr>) = Expr.App(fn, args)
internal fun appPlain(fnName: String, args: Sequence<Expr>) = app(idPlain(fnName), args)

internal fun appWithField(fn: Id, args: Sequence<Expr>, fieldParams: Sequence<Param>) = app(
  fn,
  args = args
    .zip(fieldParams)
    .map { (arg, fieldParam) ->
      assign(id(fieldParam.id.name) { withField() }, arg)
    },
)

internal fun argsBnded0(args: Sequence<Expr>) = args + refPlain("bnd")
internal fun argsBnded1(args: Sequence<Expr>) = args + binary(MINUS, refPlain("bnd"), refPlain(1))

internal fun get(obj: Id, field: Id) = Expr.Get(obj, field)

internal fun getPlain(obj: Id, fieldName: String) = get(obj, id(fieldName) { withField() })

internal fun assign(l: Id, r: Expr) = Expr.Assign(l, r)
internal fun assignPlain(lName: String, r: Expr) = assign(idPlain(lName), r)

internal fun unary(op: Op, e: Expr) = Expr.Unary(op, e)
internal fun binary(op: Op, l: Expr, r: Expr) = Expr.Binary(op, l, r)
internal fun nary(op: Op, es: Sequence<Expr>) = Expr.Nary(op, es)

internal fun constraint(ntVarName: String, outputs: Sequence<Param>) = nary(
  op = AND,
  es = outputs.map { v ->
    binary(
      op = EQ,
      l = getPlain(
        obj = idPlain(ntVarName),
        fieldName = v.id.name,
      ),
      r = refPlain(v.id.name),
    )
  },
)

internal fun ite(i: Expr, t: Expr, e: Expr) = Expr.Ite(i, t, e)

internal fun choice(es: Sequence<Expr>) = Expr.Choice(es)

internal fun forall(binds: Sequence<Param>, e: Expr) = Expr.Forall(binds, e)
