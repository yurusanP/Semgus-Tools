package org.semgus.pretty

/**
 * Pretty simple code.
 * Learned from org.aya.pretty.
 */
sealed class SimpleCode : Prettiable {
  object Empty : SimpleCode()
  data class Line(val i: Int, val xs: Sequence<SimpleCode>) : SimpleCode()
  data class CPlain(val c: Char) : SimpleCode()
  data class SPlain(val s: String) : SimpleCode()

  override fun codify() = this
}

fun simpleEmpty() = SimpleCode.Empty
fun simpleLine(i: Int, xs: Sequence<SimpleCode>) = SimpleCode.Line(i, xs)
fun simplePlain(c: Char) = SimpleCode.CPlain(c)
fun simplePlain(s: String) = SimpleCode.SPlain(s)

fun Code.best() = be(
  mutableListOf(0 to this),
)

private fun be(ps: MutableList<Pair<Int, Code>>): Sequence<SimpleCode> {
  if (ps.isEmpty()) return sequenceOf(simpleEmpty())

  val (i, x) = ps.removeLast()

  return when (x) {
    Code.Empty -> be(ps)
    Code.Line -> sequenceOf(simpleLine(i, be(ps)))
    is Code.CPlain -> sequenceOf(simplePlain(x.c)) + be(ps)
    is Code.SPlain -> sequenceOf(simplePlain(x.s)) + be(ps)
    is Code.FlatAlt -> be(
      ps.apply { this += i to x.l },
    )
    is Code.Cat -> be(
      ps.apply {
        this += x.xs.map { i to it }.toList().asReversed()
      },
    )
    is Code.Nest -> be(
      ps.apply { this += i + x.i to x.x },
    )
    is Code.Union -> TODO("Union is not handled for now.")
  }
}
