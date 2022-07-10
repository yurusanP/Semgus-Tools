package org.semgus.sketch.syntax

/**
 * Sketch parameter.
 */
internal data class Param(val type: Id, val id: Id) : Syntax()

internal fun param(type: Id, id: Id) = Param(type, id)
internal fun paramPlain(type: Id, name: String) = param(type, idPlain(name))
internal fun intPlain(name: String) = paramPlain(idPlain("int"), name)
internal fun bitPlain(name: String) = paramPlain(idPlain("bit"), name)

internal fun paramsBnded0(params: Sequence<Param>) = params + intPlain("bnd")
