package org.semgus.pretty

/**
 * Prints pretty string.
 */
fun Code.stringPretty() = this.best().layout()

private fun Sequence<SimpleCode>.layout(): String = this
  .map(SimpleCode::layout)
  .joinToString("")

private fun SimpleCode.layout(): String = when (this) {
  SimpleCode.Empty -> ""
  is SimpleCode.Line -> "\n${" ".repeat(this.i)}${this.xs.layout()}"
  is SimpleCode.CPlain -> this.c.toString()
  is SimpleCode.SPlain -> this.s
}
