package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;
import org.semgus.java.object.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record Id(@NotNull String name, @NotNull IdAttr... idAttrs) implements Syntax {
  // TODO: Should investigate later...
  public static @NotNull Id fromSemgusId(@NotNull Identifier id) {
    var name = switch (id.name()) {
      case "Int" -> "int";
      case "Bool" -> "bit";
      default -> id.name();
    };

    // var attributes = Stream.of(id.indices())
    //   .map(n -> (Attr) switch (n) {
    //     case Identifier.Index.NInt ni -> new Attr.AInt(ni.value());
    //     case Identifier.Index.NString ns -> new Attr.AString(ns.value());
    //     default -> throw new IllegalStateException("Unexpected value: " + n);
    //   })
    //   .toArray(Attr[]::new);
    var attributes = new IdAttr[0];

    return new Id(name, attributes);
  }

  public static @NotNull Id plain(@NotNull String s) {
    return new Id(s);
  }

  public static @NotNull Id plain(long i) {
    return plain(String.valueOf(i));
  }

  public static @NotNull Id keyAssert() {
    return plain("assert");
  }

  public static @NotNull Id keyReturn() {
    return plain("return");
  }

  public static @NotNull Id id(@NotNull String s, @NotNull Consumer<List<IdAttr>> idAttrBuilder) {
    List<IdAttr> as = new ArrayList<>();
    idAttrBuilder.accept(as);
    return new Id(s, as.toArray(IdAttr[]::new));
  }
}
