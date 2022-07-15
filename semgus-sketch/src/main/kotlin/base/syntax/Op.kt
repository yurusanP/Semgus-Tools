package org.semgus.sketch.base.syntax

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

  BVAND("&"),
  BVOR("|"),
  SHL("<<"),
  SHR(">>"),

//  `___`("_TODO_")
}
