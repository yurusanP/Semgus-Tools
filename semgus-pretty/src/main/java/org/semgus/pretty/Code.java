package org.semgus.pretty;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

/**
 * Pretty code.
 * Learned from org.aya.pretty.
 */
public sealed interface Code extends Prettiable {
  @Override default @NotNull Code codify() {
    return this;
  }

  record Empty() implements Code {}

  record Char(char c) implements Code {}

  record Text(@NotNull String s) implements Code {}

  record Line() implements Code {}

  record FlatAlt(@NotNull Code l, @NotNull Code r) implements Code {}

  record Cat(@NotNull List<Code> xs) implements Code {}

  record Nest(int i, @NotNull Code x) implements Code {}

  // TODO: Not needed for now.
  record Union(@NotNull Code l, @NotNull Code r) implements Code {}

  static @NotNull Code empty() {
    return new Empty();
  }

  static @NotNull Code plain(char c) {
    return new Char(c);
  }

  static @NotNull Code plain(@NotNull String s) {
    return new Text(s);
  }

  static @NotNull Code nest(int i, @NotNull Code x) {
    return i == 0 ? x : new Nest(i, x);
  }

  static @NotNull Code flatAlt(@NotNull Code l, @NotNull Code r) {
    return new FlatAlt(l, r);
  }

  static @NotNull Code hardline() {
    return new Line();
  }

  static @NotNull Code line() {
    return flatAlt(hardline(), plain(' '));
  }

  static @NotNull Code line0() {
    return flatAlt(hardline(), empty());
  }

  // TODO: Not needed for now.
  static @NotNull Code group(@NotNull Code x) {
    return x instanceof FlatAlt xFlatAlt ? xFlatAlt.l : x;
  }

  static @NotNull Code encloseDelim(@NotNull Code l, @NotNull Code r, @NotNull Code d, @NotNull List<Code> xs) {
    return enclose(l, r, concatWith(d, xs));
  }

  static @NotNull Code list(@NotNull List<Code> xs) {
    return group(
      encloseDelim(
        plain('['),
        plain(']'),
        plain(", "),
        xs
      )
    );
  }

  static @NotNull Code list(@NotNull Code... xs) {
    return list(List.of(xs));
  }

  static @NotNull Code tupled(@NotNull List<Code> xs) {
    return group(
      encloseDelim(
        plain('('),
        plain(')'),
        plain(", "),
        xs
      )
    );
  }

  static @NotNull Code tupled(@NotNull Code... xs) {
    return tupled(List.of(xs));
  }

  static @NotNull Code concatWith(@NotNull Code d, @NotNull List<Code> xs) {
    return hcat(
      xs.stream()
        .flatMap(x -> Stream.of(d, x))
        .skip(1)
        .toList()
    );
  }

  static @NotNull Code hsep(@NotNull List<Code> xs) {
    return concatWith(plain(' '), xs);
  }

  static @NotNull Code hsep(@NotNull Code... xs) {
    return hsep(List.of(xs));
  }

  static @NotNull Code vsep(@NotNull List<Code> xs) {
    return concatWith(line(), xs);
  }

  static @NotNull Code vsep(@NotNull Code... xs) {
    return vsep(List.of(xs));
  }

  static @NotNull Code sep(@NotNull List<Code> xs) {
    return group(vsep(xs));
  }

  static @NotNull Code sep(@NotNull Code... xs) {
    return sep(List.of(xs));
  }

  static @NotNull Code hcat(@NotNull List<Code> xs) {
    return new Cat(xs);
  }

  static @NotNull Code hcat(@NotNull Code... xs) {
    return hcat(List.of(xs));
  }

  static @NotNull Code vcat(@NotNull List<Code> xs) {
    return concatWith(line0(), xs);
  }

  static @NotNull Code vcat(@NotNull Code... xs) {
    return vcat(List.of(xs));
  }

  static @NotNull Code cat(@NotNull List<Code> xs) {
    return group(vcat(xs));
  }

  static @NotNull Code cat(@NotNull Code... xs) {
    return cat(List.of(xs));
  }

  static @NotNull Code spaces(int n) {
    return n <= 0 ? empty()
      : n == 1 ? plain(' ')
      : plain(" ".repeat(n));
  }

  static @NotNull Code enclose(@NotNull Code l, @NotNull Code r, @NotNull Code x) {
    return hcat(l, x, r);
  }

  static @NotNull Code surround(@NotNull Code x, @NotNull Code l, @NotNull Code r) {
    return hcat(l, x, r);
  }

  static @NotNull Code squotes(@NotNull Code x) {
    return enclose(plain('\''), plain('\''), x);
  }

  static @NotNull Code dquotes(@NotNull Code x) {
    return enclose(plain('\"'), plain('\"'), x);
  }

  static @NotNull Code parens(@NotNull Code x) {
    return enclose(plain('('), plain(')'), x);
  }

  static @NotNull Code angles(@NotNull Code x) {
    return enclose(plain('<'), plain('>'), x);
  }

  static @NotNull Code brackets(@NotNull Code x) {
    return enclose(plain('['), plain(']'), x);
  }

  static @NotNull Code braces(@NotNull Code x) {
    return enclose(plain('{'), plain('}'), x);
  }

  static @NotNull Code verts(@NotNull Code x) {
    return enclose(plain('|'), plain('|'), x);
  }

  static @NotNull Code semi(@NotNull Code x) {
    return hcat(x, plain(';'));
  }

  static @NotNull Code block(@NotNull Code prefix, int i, @NotNull Code body) {
    return hcat(
      hsep(prefix, plain('{')),
      nest(i, hcat(
        line(),
        body
      )),
      line(),
      plain('}')
    );
  }
}
