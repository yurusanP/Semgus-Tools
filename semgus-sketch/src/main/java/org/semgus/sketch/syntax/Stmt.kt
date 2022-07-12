package org.semgus.sketch.syntax

/**
 * Sketch statement.
 */
internal sealed class Stmt : Syntax() {
  object Skip : Stmt()
  data class VarDef(val decl: Param, val init: Expr) : Stmt()
  data class FnDef(val decl: Param, val params: Sequence<Param>, val body: Stmt) : Stmt()
  data class StructDef(val id: Id, val fields: Sequence<Param>) : Stmt()
  data class Atomic(val prefix: Id, val e: Expr) : Stmt()
  data class Seq(val ss: Sequence<Stmt>) : Stmt()
}

internal fun skip() = Stmt.Skip

internal fun varDef(decl: Param, init: Expr) = Stmt.VarDef(decl, init)

internal fun bndDef(bnd: Long) = varDef(intPlain("bnd"), refPlain(bnd))

internal fun fnDef(decl: Param, params: Sequence<Param>, body: Stmt) = Stmt.FnDef(decl, params, body)

internal fun structDef(id: Id, fields: Sequence<Param>) = Stmt.StructDef(id, fields)
internal fun ntTypeDef(ntName: String, fieldParams: Sequence<Param>) =
  structDef(
    id = id(ntName) { withNTType() },
    fields = fieldParams.map { fieldParam ->
      param(fieldParam.type, id(fieldParam.id.name) { withField() })
    },
  )

internal fun atomic(prefix: Id, e: Expr) = Stmt.Atomic(prefix, e)
internal fun aAssert(e: Expr) = atomic(idPlain("assert"), e)
internal fun aBndChk() = aAssert(binary(Op.GT, refPlain("bnd"), refPlain(0)))
internal fun aReturn(e: Expr) = atomic(idPlain("return"), e)

internal fun seq(ss: Sequence<Stmt>) = Stmt.Seq(ss)
internal fun sBndChked(ss: Sequence<Stmt>) = seq(sequenceOf(aBndChk()) + ss)
internal fun sBndChked(s: Stmt) = seq(sequenceOf(aBndChk(), s))

internal fun sBndDefed(ss: Sequence<Stmt>, bnd: Long) = seq(sequenceOf(bndDef(bnd)) + ss)

internal fun sBndDefed(s: Stmt, bnd: Long) = seq(sequenceOf(bndDef(bnd), s))
