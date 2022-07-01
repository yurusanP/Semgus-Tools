package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public record Param(@NotNull Id type, @NotNull Id id) implements Syntax {
  public static @NotNull Param param(@NotNull Id type, @NotNull Id id) {
    return new Param(type, id);
  }

  public static @NotNull Param plain(@NotNull Id type, @NotNull String id) {
    return param(type, Id.plain(id));
  }

  public static @NotNull Param intPlain(@NotNull String id) {
    return param(Id.plain("int"), Id.plain(id));
  }

  public static @NotNull Param bnd() {
    return Param.intPlain("bnd");
  }

  public static @NotNull List<Param> bnded0(@NotNull List<Param> params) {
    var bndParam = Param.intPlain("bnd");
    return Stream.concat(params.stream(), Stream.of(bndParam)).toList();
  }

  public static @NotNull Param bitPlain(@NotNull String id) {
    return param(Id.plain("bit"), Id.plain(id));
  }
}
