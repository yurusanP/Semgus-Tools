package org.semgus.sketch.ir;


import org.semgus.sketch.base.Expr;

import java.util.List;
import java.util.Map;

public record Rule(
  NonTerminal nt,
  List<NonTerminal> childNTs,
  Map<String, Expr> bindings  // output variable names -> bindings
) {}
