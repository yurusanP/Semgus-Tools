package org.semgus.sketch.syntax

import org.semgus.pretty.Prettiable
import org.semgus.sketch.frontend.distill

/**
 * Sketch syntax.
 */
internal sealed class Syntax : Prettiable {
  override fun codify() = distill()
}
