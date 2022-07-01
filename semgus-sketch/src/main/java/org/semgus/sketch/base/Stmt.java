package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public sealed interface Stmt extends Syntax {
  record VarDef(@NotNull Param decl, @NotNull Expr init) implements Stmt {}

  record FnDef(@NotNull Param decl, @NotNull List<Param> params, @NotNull Stmt body) implements Stmt {}

  record StructDef(@NotNull Id id, @NotNull List<Param> fields) implements Stmt {}

  record Atomic(@NotNull Id prefix, @NotNull Expr e) implements Stmt {}

  record Seq(@NotNull List<Stmt> ss) implements Stmt {}

  record Skip() implements Stmt {}

  static @NotNull Stmt varDef(@NotNull Param decl, @NotNull Expr init) {
    return new VarDef(decl, init);
  }

  static @NotNull Stmt fnDef(@NotNull Param decl, @NotNull List<Param> params, @NotNull Stmt body) {
    return new FnDef(decl, params, body);
  }

  static @NotNull Stmt structDef(@NotNull Id id, @NotNull List<Param> fields) {
    return new StructDef(id, fields);
  }

  static @NotNull Stmt ntTypeDef(@NotNull String ntName, @NotNull List<Param> fields) {
    return structDef(Id.id(ntName, IdAttr.withNTType()), fields);
  }

  static @NotNull Stmt atomic(@NotNull Id prefix, @NotNull Expr e) {
    return new Atomic(prefix, e);
  }

  static @NotNull Stmt aAssert(@NotNull Expr e) {
    return atomic(Id.keyAssert(), e);
  }

  static @NotNull Stmt bndChk() {
    var e = Expr.binaryPlain(">", Expr.refPlain("bnd"), Expr.refPlain(0));
    return Stmt.aAssert(e);
  }

  static @NotNull Stmt aReturn(@NotNull Expr e) {
    return atomic(Id.keyReturn(), e);
  }

  static @NotNull Stmt seq(@NotNull List<Stmt> ss) {
    return new Seq(ss);
  }

  static @NotNull Stmt bndChked(@NotNull List<Stmt> ss) {
    return seq(Stream.concat(Stream.of(bndChk()), ss.stream()).toList());
  }

  static @NotNull Stmt bndChked(@NotNull Stmt s) {
    return bndChked(List.of(s));
  }

  static @NotNull Stmt skip() {
    return new Skip();
  }
}
