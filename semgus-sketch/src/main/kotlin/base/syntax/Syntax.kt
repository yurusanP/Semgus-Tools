package org.semgus.sketch.base.syntax

import org.semgus.pretty.Prettiable
import org.semgus.sketch.codegen.distill

/**
 * Sketch syntax.
 */
internal sealed class Syntax : Prettiable {
  override fun codify() = distill()
}
