package org.semgus.sketch.ir;

import org.jetbrains.annotations.NotNull;
import org.semgus.java.object.AnnotatedVar;
import org.semgus.java.object.TypedVar;
import org.semgus.java.problem.SemanticRule;
import org.semgus.java.problem.SemgusNonTerminal;
import org.semgus.sketch.base.Id;
import org.semgus.sketch.base.Param;
import org.semgus.sketch.util.Pair;

import java.util.*;

// TODO: Change visibility in the project.

public record NonTerminalMkr(
  @NotNull SemgusNonTerminal semgusNT,
  @NotNull SemanticRule semRule0,
  @NotNull List<Pair<TypedVar, AnnotatedVar>> fullVars
) {
  static @NotNull NonTerminalMkr build(@NotNull SemgusNonTerminal semgusNT) {
    var semRule0 = IR.getSemRule0(semgusNT);

    var fullVars = semRule0.head().arguments().stream()
      .map(typedVar -> Pair.of(typedVar, semRule0.variables().get(typedVar.name())))
      .toList();

    return new NonTerminalMkr(semgusNT, semRule0, fullVars);
  }

  @NotNull NonTerminal mkNT() {
    return new NonTerminal(
      semgusNT.name(),
      semRule0.head().name(),
      mkOrdVars(),
      mkParams()
    );
  }

  private @NotNull List<Var> mkOrdVars() {
    return fullVars.stream()
      .map(p -> new Var(
        mkParam(p.first()),
        p.first().name(),
        p.second().attributes().keySet())
      )
      .toList();
  }

  private @NotNull Map<String, List<Param>> mkParams() {
    Map<String, List<Param>> res = new HashMap<>();

    fullVars.forEach(p -> p.second().attributes().keySet()
      .forEach(attr -> {
        res.putIfAbsent(attr, new ArrayList<>());
        res.get(attr).add(mkParam(p.first()));
      })
    );

    return Collections.unmodifiableMap(res);
  }

  private @NotNull Param mkParam(@NotNull TypedVar typedVar) {
    return Param.plain(Id.fromSemgusId(typedVar.type()), typedVar.name());
  }
}
