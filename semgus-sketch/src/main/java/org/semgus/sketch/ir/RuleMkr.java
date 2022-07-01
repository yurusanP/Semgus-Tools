package org.semgus.sketch.ir;

import org.jetbrains.annotations.NotNull;
import org.semgus.java.object.RelationApp;
import org.semgus.java.problem.SemanticRule;
import org.semgus.sketch.base.Expr;
import org.semgus.sketch.base.Id;
import org.semgus.sketch.base.Param;
import org.semgus.sketch.util.Evaluator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public record RuleMkr(
  @NotNull SemanticRule semRule,
  @NotNull Map<String, NonTerminal> nts,
  @NotNull NonTerminal nt,
  @NotNull List<String> outputNames
  ) {
  static @NotNull RuleMkr build(@NotNull SemanticRule semRule, @NotNull Map<String, NonTerminal> nts) {
    var nt = nts.get(semRule.head().name());
    var outputNames = nt.params().get("output").stream()
      .map(Param::id)
      .map(Id::name)
      .toList();

    return new RuleMkr(semRule, nts, nt, outputNames);
  }

  @NotNull Rule mkRule() {
    // TODO: Why can't we have an expression in the childNT call?

    var childNTs = semRule.bodyRelations().stream()
      .map(this::mkChildNT)
      .toList();

    var e = Evaluator.eval(semRule.constraint());
    Map<String, Expr> bindings = new HashMap<>();
    updBindings(bindings, e);

    return new Rule(nt, childNTs, Collections.unmodifiableMap(bindings));
  }

  private @NotNull NonTerminal mkChildNT(@NotNull RelationApp bodyRel) {
    var childNT = nts.get(bodyRel.name());

    var resOrdVars = IntStream.range(0, bodyRel.arguments().size())
      .mapToObj(i -> {
        var v = childNT.ordVars().get(i);
        return new Var(v.decl(), bodyRel.arguments().get(i).name(), v.attrs());
      })
      .toList();

    return new NonTerminal(
      childNT.name(),
      childNT.relName(),
      resOrdVars,
      childNT.params()
    );
  }

  // TODO: Should investigate later... Focusing on getting sum-by-while working...
  private void updBindings(@NotNull Map<String, Expr> bindings, @NotNull Expr e) {
    if (e instanceof Expr.Binary eBinary) {
      if (eBinary.op().name().equals("==")) {
        if (eBinary.l() instanceof Expr.Ref l && outputNames.contains(l.id().name())) {
          bindings.put(l.id().name(), eBinary.r());
        } else if (eBinary.r() instanceof Expr.Ref r && outputNames.contains(r.id().name())) {
          bindings.put(r.id().name(), eBinary.l());
        } else {
          throw new RuntimeException("Can't handle complicated constraint for now: " + eBinary);
        }
      } else if (eBinary.op().name().equals("&&")) {
        updBindings(bindings, eBinary.l());
        updBindings(bindings, eBinary.r());
      }
    }
  }
}
