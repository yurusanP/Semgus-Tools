package org.semgus.sketch.frontend

import org.semgus.pretty.*
import org.semgus.sketch.syntax.*

/**
 * Frontend of sketch syntax.
 */
internal fun Syntax.distill(): Code = when (this) {
  is Id -> props.fold(plain(this.name) as Code) { acc, prop ->
    when (prop) {
      IdProp.TmpStruct -> verts(acc)
      is IdProp.Prefix -> hcat(plain(prop.s), acc)
      is IdProp.Postfix -> hcat(acc, plain(prop.s))
    }
  }

  is Param -> hsep(
    this.type.distill(),
    this.id.distill(),
  )

  is Expr.Ref -> this.id.distill()
  is Expr.Call -> hcat(
    this.fn.distill(),
    tuple0(this.args.distill()),
  )
  is Expr.Get -> hcat(
    this.obj.distill(),
    plain('.'),
    this.field.distill(),
  )
  is Expr.Assign -> hsep(
    this.l.distill(),
    plain('='),
    this.r.distill(),
  )
  is Expr.Unary -> hcat(
    plain(this.op.s),
    parens(this.e.distill()),
  )
  is Expr.Binary -> parens(
    hsep(
      this.l.distill(),
      plain(this.op.s),
      this.r.distill(),
    ),
  )
  is Expr.Nary -> hsepDelim(
    plain(this.op.s),
    this.es.distill(),
  ).let {
    if (this.es.count() == 1) it else parens(it)
  }
  is Expr.Ite -> parens(
    hsep(
      this.i.distill(),
      plain('?'),
      this.t.distill(),
      plain(':'),
      this.e.distill(),
    ),
  )
  is Expr.Choice -> hsepEncloseDelim(
    plain("{| "),
    plain(" |}"),
    plain('|'),
    this.es.distill(),
  )
  is Expr.Forall -> this.e.distill()

  Stmt.Skip -> empty()
  is Stmt.VarDef -> semi(
    hsep(
      this.decl.distill(),
      plain('='),
      this.init.distill(),
    ),
  )
  is Stmt.FnDef -> block(
    2,
    hcat(
      this.decl.distill(),
      tuple0(this.params.distill()),
    ),
    this.body.distill(),
  )
  is Stmt.StructDef -> block(
    2,
    hsep(
      plain("struct"),
      this.id.distill(),
    ),
    vsep(
      this.fields.distill().map(::semi),
    ),
  )
  is Stmt.Atomic -> semi(
    hsep(
      this.prefix.distill(),
      this.e.distill(),
    ),
  )
  is Stmt.Seq -> vsep(
    this.ss.distill(),
  )
}

internal fun Sequence<Syntax>.distill(): Sequence<Code> = this.map(Syntax::distill)
