package org.semgus.sketch.ir;

import org.semgus.sketch.base.Id;
import org.semgus.sketch.base.Param;

import java.util.Set;

public record Var(
  Param decl,
  String varName,
  Set<String> attrs
) {
}
