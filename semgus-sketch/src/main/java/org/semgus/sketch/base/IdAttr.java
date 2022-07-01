package org.semgus.sketch.base;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public sealed interface IdAttr {
  record Prefix(@NotNull String s) implements IdAttr {}

  record Postfix(@NotNull String s) implements IdAttr {}

  record TmpStruct() implements IdAttr {}

  static @NotNull Consumer<List<IdAttr>> withPrefix(@NotNull String prefix) {
    return as -> as.add(new IdAttr.Prefix(prefix));
  }

  static @NotNull Consumer<List<IdAttr>> withGenerator() {
    return withPrefix(" ").andThen(withPrefix("generator"));
  }

  static @NotNull Consumer<List<IdAttr>> withHarness() {
    return withPrefix(" ").andThen(withPrefix("harness"));
  }

  static @NotNull Consumer<List<IdAttr>> withPostfix(@NotNull String postfix) {
    return as -> as.add(new IdAttr.Postfix(postfix));
  }

  static @NotNull Consumer<List<IdAttr>> withNTType() {
    return withPostfix("_t");
  }

  static @NotNull Consumer<List<IdAttr>> withRuleIndex(int ruleIndex) {
    return withPostfix("_rule%d".formatted(ruleIndex));
  }

  static @NotNull Consumer<List<IdAttr>> withTmpStruct() {
    return as -> as.add(new IdAttr.TmpStruct());
  }

  static @NotNull Consumer<List<IdAttr>> withTmpNTType() {
    return withNTType().andThen(withTmpStruct());
  }
}
