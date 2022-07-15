package org.semgus.sketch.base.syntax

/**
 * Sketch identifier.
 */
internal data class Id(
  val name: String,
  val props: Sequence<IdProp>,
) : Syntax()

internal fun id(name: String, props: Sequence<IdProp>) = Id(name, props)
internal inline fun id(name: String, builderAction: MutableList<IdProp>.() -> Unit) =
  id(name, buildProps(builderAction))

internal fun idPlain(s: String) = Id(s, emptySequence())
internal fun idPlain(n: Number) = idPlain(n.toString())

/**
 * Sketch identifier properties.
 */
internal sealed class IdProp {
  object TmpStruct : IdProp()
  data class Prefix(val s: String) : IdProp()
  data class Postfix(val s: String) : IdProp()
}

internal inline fun buildProps(builderAction: MutableList<IdProp>.() -> Unit) =
  mutableListOf<IdProp>().apply(builderAction).asSequence()

internal fun MutableList<IdProp>.withTmpStruct() {
  this += IdProp.TmpStruct
}

internal fun MutableList<IdProp>.withPrefix(s: String = " ") {
  this += IdProp.Prefix(s)
}

internal fun MutableList<IdProp>.withPostfix(s: String = " ") {
  this += IdProp.Postfix(s)
}

internal fun MutableList<IdProp>.withGenerator() {
  this.withPrefix()
  this.withPrefix("generator")
}

internal fun MutableList<IdProp>.withHarness() {
  this.withPrefix()
  this.withPrefix("harness")
}

internal fun MutableList<IdProp>.withNTType() {
  this.withPostfix("_t")
}

internal fun MutableList<IdProp>.withField() {
  this.withPostfix("_field")
}

internal fun MutableList<IdProp>.withRuleIndex(i: Int) {
  this.withPostfix("_rule$i")
}

internal fun MutableList<IdProp>.withTmpNTType() {
  this.withNTType()
  this.withTmpStruct()
}

internal fun MutableList<IdProp>.withBVSize(sz: Int) {
  this.withPostfix("[$sz]")
}
