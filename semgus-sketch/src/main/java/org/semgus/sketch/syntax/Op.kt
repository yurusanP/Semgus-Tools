package org.semgus.sketch.syntax

/**
 * Sketch operator.
 */
internal enum class Op(val s: String) {
  AND("&&"),
  OR("||"),
  NOT("!"),
  XOR("^"),
  EQ("=="),

  MINUS("-"),
  PLUS("+"),
  TIMES("*"),
  DIV("/"),
  MOD("%"),

  LTE("<="),
  LT("<"),
  GTE(">="),
  GT(">"),
}
