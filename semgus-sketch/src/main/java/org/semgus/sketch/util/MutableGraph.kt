package org.semgus.sketch.util

/**
 * Mutable graph.
 */
internal data class MutableGraph<T>(
  val vertices: MutableList<MutableVertex<T>> = mutableListOf(),
) {
  fun addVertex(data: T): Boolean {
    val vertex = MutableVertex(
      index = vertices.size,
      data,
      succIndices = mutableListOf(),
      predCnt = 0,
    )
    return vertices.add(vertex)
  }

  fun addEdge(srcIndex: Int, dstIndex: Int): Boolean {
    vertices[dstIndex].predCnt += 1
    return vertices[srcIndex].succIndices.add(dstIndex)
  }

  /**
   * Sorts a graph topologically.
   * It assumes that the graph is a DAG.
   */
  fun topoSort() = buildList {
    val copy = this@MutableGraph.copy()

    // vertices with 0 in-degree
    val vertices0 = copy.vertices.filter { it.predCnt == 0 }.toMutableList()

    while (vertices0.isNotEmpty()) {
      val curr = vertices0.removeLast()
      this.add(curr.data)
      curr.succIndices.forEach { succIndex ->
        val succ = copy.vertices[succIndex]
        succ.predCnt -= 1
        if (succ.predCnt == 0) vertices0.add(succ)
      }
    }

    if (copy.vertices.any { v -> v.predCnt != 0 }) {
      throw IllegalStateException("The graph is not a DAG.")
    }
  }.asSequence()
}

/**
 * Mutable vertex.
 */
internal data class MutableVertex<T>(
  val index: Int,
  val data: T,
  val succIndices: MutableList<Int>,
  var predCnt: Int,
)
