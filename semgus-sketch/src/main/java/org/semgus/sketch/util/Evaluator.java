package org.semgus.sketch.util;

import org.semgus.java.object.SmtTerm;
import org.semgus.sketch.base.Expr;
import org.semgus.sketch.base.Id;
import org.semgus.sketch.base.Param;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Evaluator {
  Map<String, Function<List<Expr>, Expr>> fns = Map.ofEntries(
    // Core Theory
    Map.entry("true", es -> Expr.refPlain("true")),
    Map.entry("false", es -> Expr.refPlain("false")),
    // TODO: Support Expr.Nary.
    Map.entry("and", es -> Expr.binaryPlain("&&", es.get(0), es.get(1))),
    Map.entry("or", es -> Expr.binaryPlain("||", es.get(0), es.get(1))),
    Map.entry("not", es -> Expr.unaryPlain("!", es.get(0))),
    Map.entry("!", es -> Expr.unaryPlain("!", es.get(0))),
    Map.entry("xor", es -> Expr.binaryPlain("^", es.get(0), es.get(1))),
    // TODO: Map.entry("=>")?
    Map.entry("=", es -> Expr.binaryPlain("==", es.get(0), es.get(1))),
    // TODO: Map.entry("distinct")?
    Map.entry("ite", es -> Expr.cond(es.get(0), es.get(1), es.get(2))),

    // Ints Theory
    Map.entry("-", es -> es.size() == 1
      ? Expr.unaryPlain("-", es.get(0))
      : Expr.binaryPlain("-", es.get(0), es.get(1)
    )),
    Map.entry("+", es -> Expr.binaryPlain("+", es.get(0), es.get(1))),
    Map.entry("*", es -> Expr.binaryPlain("*", es.get(0), es.get(1))),
    Map.entry("div", es -> Expr.binaryPlain("/", es.get(0), es.get(1))),
    Map.entry("mod", es -> Expr.binaryPlain("%", es.get(0), es.get(1))),
    // TODO: Map.entry("abs")? Use Expr.Cond.
    Map.entry("<=", es -> Expr.binaryPlain("<=", es.get(0), es.get(1))),
    Map.entry("<", es -> Expr.binaryPlain("<", es.get(0), es.get(1))),
    Map.entry(">=", es -> Expr.binaryPlain(">=", es.get(0), es.get(1))),
    Map.entry(">", es -> Expr.binaryPlain(">", es.get(0), es.get(1)))

    // TODO: Bit Vectors Theory

    // TODO: Strings Theory
  );

  static Expr eval(SmtTerm t) {
    return switch (t) {
      case SmtTerm.Application tApp -> fns.getOrDefault(
          tApp.name().name(),
          es -> Expr.appPlain(tApp.name().name(), es))
        .apply(tApp.arguments().stream()
          .map(SmtTerm.Application.TypedTerm::term)
          .map(Evaluator::eval)
          .toList());
      case SmtTerm.Variable tVar -> Expr.refPlain(tVar.name());
      case SmtTerm.CNumber tNum -> Expr.refPlain(tNum.value());
      case SmtTerm.CString tStr -> Expr.refPlain(tStr.value());
      // TODO: What about exists?
      case SmtTerm.Quantifier tQnt -> tQnt.type().toString().equals("∀")
        ? Expr.forall(
        tQnt.bindings().stream()
          .map(typedVar -> Param.plain(
            Id.fromSemgusId(typedVar.type()),
            typedVar.name()))
          .toList(),
        eval(tQnt.child()))
        : Expr.refPlain("??");
      // TODO: case SmtTerm.CBitVector ->
      default -> throw new IllegalStateException("Unexpected value: " + t);
    };
  }
}
