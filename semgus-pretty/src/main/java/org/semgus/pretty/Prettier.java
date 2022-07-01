package org.semgus.pretty;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Prettier {
  record Pair<F, S>(@NotNull F first, @NotNull S second) {
    public static <F, S> @NotNull Pair<F, S> of(@NotNull F first, @NotNull S second) {
      return new Pair<>(first, second);
    }
  }

  static @NotNull String pretty(@NotNull Code x) {
    return layout(best(x));
  }

  private static @NotNull String layout(@NotNull SimpleCode x) {
    return switch (x) {
      case SimpleCode.Empty ignored -> "";
      case SimpleCode.Char xChar -> String.valueOf(xChar.c());
      case SimpleCode.Text xText -> xText.s();
      case SimpleCode.Line xLine -> "\n%s%s".formatted(" ".repeat(xLine.i()), layout(xLine.xs()));
    };
  }

  private static @NotNull String layout(@NotNull Stream<SimpleCode> xs) {
    return xs.map(Prettier::layout).collect(Collectors.joining());
  }

  private static @NotNull Stream<SimpleCode> best(@NotNull Code x) {
    var ps = new Stack<Pair<Integer, Code>>();
    ps.add(Pair.of(0, x));
    return be(ps);
  }

  private static @NotNull Stream<SimpleCode> be(@NotNull Stack<Pair<Integer, Code>> ps) {
      if (ps.empty()) return Stream.of(new SimpleCode.Empty());

      var p = ps.pop();
      return switch (p.second()) {
        case Code.Empty ignored -> be(ps);
        case Code.Char codeChar -> Stream.concat(
          Stream.of(new SimpleCode.Char(codeChar.c())),
          be(ps)
        );
        case Code.Text codeText -> Stream.concat(
          Stream.of(new SimpleCode.Text(codeText.s())),
          be(ps)
        );
        case Code.Line ignored -> Stream.of(new SimpleCode.Line(p.first(), be(ps)));
        case Code.FlatAlt codeFlatAlt -> {
          ps.add(Pair.of(p.first(), codeFlatAlt.l()));
          yield be(ps);
        }
        case Code.Cat codeCat -> {
          var es = codeCat.xs().stream()
            .map(e -> Pair.of(p.first(), e))
            .collect(Collectors.toList());
          Collections.reverse(es);
          ps.addAll(es);
          yield be(ps);
        }
        case Code.Nest codeNest -> {
          ps.add(Pair.of(p.first() + codeNest.i(), codeNest.x()));
          yield be(ps);
        }
        // TODO: Not handled for now.
        case Code.Union codeUnion -> {
          ps.add(Pair.of(p.first(), codeUnion.l()));
          yield be(ps);
        }
      };
  }
}
