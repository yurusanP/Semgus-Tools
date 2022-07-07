package org.semgus.sketch.frontend;

import org.jetbrains.annotations.NotNull;
import org.semgus.pretty.Code;
import org.semgus.sketch.base.*;

import java.util.List;
import java.util.stream.Collectors;

public interface Distiller {
  static @NotNull Code distill( Syntax x) {
    if (x == null)
      return new Code.Char('$');
    return switch (x) {
      case Id xId -> {
        var res = Code.plain(xId.name());
        for (var a : xId.idAttrs()) {
          switch (a) {
            case IdAttr.Prefix aPrefix -> res = Code.hcat(Code.plain(aPrefix.s()), res);
            case IdAttr.Postfix aPostfix -> res = Code.hcat(res, Code.plain(aPostfix.s()));
            case IdAttr.TmpStruct ignored -> res = Code.verts(res);
          }
        }
        yield res;
      }

      case Param xParam -> Code.hsep(
        distill(xParam.type()),
        distill(xParam.id())
      );

      case Expr.Ref xRef -> distill(xRef.id());
      case Expr.App xApp -> Code.hcat(
        distill(xApp.fn()),
        Code.tupled(distill(xApp.args()))
      );
      case Expr.Get xGet -> Code.hcat(
        distill(xGet.id()),
        Code.plain('.'),
        distill(xGet.field())
      );
      case Expr.Assign xAssign -> Code.hsep(
        distill(xAssign.id()),
        Code.plain('='),
        distill(xAssign.e())
      );
      case Expr.Unary xUnary -> Code.hcat(
        distill(xUnary.op()),
        Code.parens(distill(xUnary.e()))
      );
      case Expr.Binary xBinary -> Code.parens(
        Code.hsep(
          distill(xBinary.l()),
          distill(xBinary.op()),
          distill(xBinary.r())
        )
      );
      case Expr.Cond xCond -> Code.parens(
        Code.hsep(
          distill(xCond.i()),
          Code.plain('?'),
          distill(xCond.t()),
          Code.plain(':'),
          distill(xCond.e())
        )
      );
      case Expr.Choice xChoice -> Code.encloseDelim(
        Code.plain("{| "),
        Code.plain(" |}"),
        Code.plain(" | "),
        distill(xChoice.es())
      );
      // The forall expression won't be printed...
      case Expr.Forall xForall -> distill(xForall.e());

      case Stmt.VarDef xVarDef -> Code.semi(
        Code.hsep(
          distill(xVarDef.decl()),
          Code.plain('='),
          distill(xVarDef.init())
        )
      );
      case Stmt.FnDef xFnDef -> Code.block(
        Code.hcat(
          distill(xFnDef.decl()),
          Code.tupled(distill(xFnDef.params()))
        ),
        2,
        distill(xFnDef.body())
      );
      case Stmt.StructDef xStructDef -> Code.block(
        Code.hsep(
          Code.plain("struct"),
          distill(xStructDef.id())
        ),
        2,
        Code.vsep(
          distill(xStructDef.fields()).stream()
            .map(Code::semi)
            .collect(Collectors.toList())
        )
      );
      case Stmt.Atomic xAtomic -> Code.semi(
        Code.hsep(
          distill(xAtomic.prefix()),
          distill(xAtomic.e())
        )
      );
      case Stmt.Seq xSeq -> Code.vsep(
        distill(xSeq.ss())
      );
      case Stmt.Skip ignored -> Code.empty();
    };
  }

  static @NotNull List<Code> distill(@NotNull List<? extends Syntax> xs) {
    return xs.stream().map(Distiller::distill).toList();
  }
}
