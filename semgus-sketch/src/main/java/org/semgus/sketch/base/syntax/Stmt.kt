package org.semgus.sketch.base.syntax

/**
 * Sketch statement.
 */
internal sealed class Stmt : Syntax() {
  object Skip : Stmt()
  data class VarDef(val decl: Param, val init: Expr) : Stmt()
  data class FnDef(val decl: Param, val params: Sequence<Param>, val body: Stmt) : Stmt()
  data class BlockDef(val prefix: Id, val decls: Sequence<Param>) : Stmt()
  data class Atomic(val prefix: Id, val e: Expr) : Stmt()
  data class Seq(val ss: Sequence<Stmt>) : Stmt()
}

internal fun skip() = Stmt.Skip

internal fun varDef(decl: Param, init: Expr) = Stmt.VarDef(decl, init)

internal fun bndDef(bnd: Int) = varDef(intPlain("bnd"), refPlain(bnd))

internal fun fnDef(decl: Param, params: Sequence<Param>, body: Stmt) = Stmt.FnDef(decl, params, body)

internal fun blockDef(prefix: Id, decls: Sequence<Param>) = Stmt.BlockDef(prefix, decls)
internal fun ntTypeDef(objName: String, fieldDecls: Sequence<Param>) = blockDef(
  prefix = id(objName) { withNTType() },
  decls = fieldDecls.map { param(it.type, id(it.id.name) { withField() }) },
)

internal fun atomic(prefix: Id, e: Expr) = Stmt.Atomic(prefix, e)
internal fun aAssert(e: Expr) = atomic(idPlain("assert"), e)
internal fun aBndChk() = aAssert(binary(Op.GT, refPlain("bnd"), refPlain(0)))
internal fun aReturn(e: Expr) = atomic(idPlain("return"), e)

internal fun seq(ss: Sequence<Stmt>) = Stmt.Seq(ss)
internal inline fun seqWith(first: Stmt, withAction: Sequence<Stmt>.() -> Sequence<Stmt>) =
  seq(sequenceOf(first).run(withAction))

