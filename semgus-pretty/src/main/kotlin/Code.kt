package org.semgus.pretty

/**
 * Pretty code.
 * Learned from org.aya.pretty.
 */
sealed class Code : Prettiable {
  object Empty : Code()
  object Line : Code()
  data class CPlain(val c: Char) : Code()
  data class SPlain(val s: String) : Code()
  data class FlatAlt(val l: Code, val r: Code) : Code()
  data class Cat(val xs: Sequence<Code>) : Code()
  data class Nest(val i: Int, val x: Code) : Code()

  // TODO: Not needed for now.
  data class Union(val l: Code, val r: Code) : Code()

  override fun codify() = this
}

fun empty() = Code.Empty

fun space() = plain(' ')
fun spaces(n: Int) =
  if (n <= 0) empty()
  else if (n == 1) space()
  else plain(" ".repeat(n))

fun hardline() = Code.Line
fun line0() = flatAlt(hardline(), empty())
fun line() = flatAlt(hardline(), space())

fun plain(c: Char) = Code.CPlain(c)
fun plain(s: String) = Code.SPlain(s)

fun flatAlt(l: Code, r: Code) = Code.FlatAlt(l, r)

fun hcat(xs: Sequence<Code>) = Code.Cat(xs)
fun hcat(vararg xs: Code) = hcat(xs.asSequence())
fun vcat(xs: Sequence<Code>) = hcatDelim(line0(), xs)
fun vcat(vararg xs: Code) = vcat(xs.asSequence())
fun cat(xs: Sequence<Code>) = group(vcat(xs))
fun cat(vararg xs: Code) = cat(xs.asSequence())

fun hsep(xs: Sequence<Code>) = hcatDelim(space(), xs)
fun hsep(vararg xs: Code) = hsep(xs.asSequence())
fun vsep(xs: Sequence<Code>) = hcatDelim(line(), xs)
fun vsep(vararg xs: Code) = vsep(xs.asSequence())
fun sep(xs: Sequence<Code>) = group(vsep(xs))
fun sep(vararg xs: Code) = sep(xs.asSequence())

fun nest(i: Int, x: Code) =
  if (i == 0) x
  else Code.Nest(i, x)

// TODO: Not needed for now.
fun union(l: Code, r: Code) = Code.Union(l, r)

// TODO: Not needed for now.
fun group(x: Code) =
  if (x is Code.FlatAlt) x.l
  else x

fun hcatDelim(d: Code, xs: Sequence<Code>) = hcat(
  xs.flatMap { sequenceOf(d, it) }.drop(1),
)

fun hsepDelim(d: Code, xs: Sequence<Code>) = hsep(
  xs.flatMap { sequenceOf(d, it) }.drop(1),
)

fun surround(x: Code, l: Code, r: Code) = hcat(l, x, r)
fun enclose(l: Code, r: Code, x: Code) = hcat(l, x, r)

fun hcatEncloseDelim(l: Code, r: Code, d: Code, xs: Sequence<Code>) =
  enclose(l, r, hcatDelim(d, xs))

fun hsepEncloseDelim(l: Code, r: Code, d: Code, xs: Sequence<Code>) =
  enclose(l, r, hsepDelim(d, xs))

fun list(xs: Sequence<Code>) = group(
  hcatEncloseDelim(
    flatAlt(plain("[ "), plain('[')),
    flatAlt(plain(" ]"), plain(']')),
    plain(", "),
    xs,
  ),
)

fun list0(xs: Sequence<Code>) = hcatEncloseDelim(
  plain('['),
  plain(']'),
  plain(", "),
  xs,
)

fun tuple(xs: Sequence<Code>) = group(
  hcatEncloseDelim(
    flatAlt(plain("( "), plain('(')),
    flatAlt(plain(" )"), plain(')')),
    plain(", "),
    xs,
  ),
)

fun tuple0(xs: Sequence<Code>) = hcatEncloseDelim(
  plain('('),
  plain(')'),
  plain(", "),
  xs,
)

fun set(xs: Sequence<Code>) = group(
  hcatEncloseDelim(
    flatAlt(plain("{ "), plain('{')),
    flatAlt(plain(" }"), plain('}')),
    plain(", "),
    xs,
  ),
)

fun set0(xs: Sequence<Code>) = hcatEncloseDelim(
  plain('{'),
  plain('}'),
  plain(", "),
  xs,
)

fun block(i: Int, prefix: Code, body: Code) = hcat(
  hsep(prefix, plain('{')),
  nest(i, hcat(line(), body)),
  line(),
  plain('}'),
)

fun squotes(x: Code) = enclose(plain('\''), plain('\''), x)
fun dquotes(x: Code) = enclose(plain('\"'), plain('\"'), x)
fun parens(x: Code) = enclose(plain('('), plain(')'), x)
fun angles(x: Code) = enclose(plain('<'), plain('>'), x)
fun brackets(x: Code) = enclose(plain('['), plain(']'), x)
fun braces(x: Code) = enclose(plain('{'), plain('}'), x)
fun verts(x: Code) = enclose(plain('|'), plain('|'), x)
fun semi(x: Code) = hcat(x, plain(';'))
