package org.semgus.sketch.util

/**
 * Mutable graph.
 */
internal data class MutableGraph<K, V>(
  val vertices: MutableMap<K, MutableVertex<K, V>> = mutableMapOf(),
) {
  fun addVertex(key: K, value: MutableVertex<K, V> = MutableVertex()) =
    vertices.putIfAbsent(key, value) == null

  fun addEdge(srcKey: K, dstKey: K) = vertices[srcKey]?.let { src ->
    vertices[dstKey]?.let { dst ->
      dst.predCnt += 1
      src.succKeys.add(dstKey)
    }
  } ?: false

  /**
   * Sorts a graph topologically.
   * It assumes that the graph is a DAG.
   */
  fun toSortedList() = buildList {
    val copy = this@MutableGraph.copy()

    // vertex keys with 0 in-degree
    val keys0 = ArrayDeque(
      copy.vertices.asSequence()
        .filter { (_, v) -> v.predCnt == 0 }
        .map { (k, _) -> k }
        .toList()
    )

    while (keys0.isNotEmpty()) {
      val curr = keys0.removeFirst()
      this@buildList.add(curr)
      vertices[curr]!!.succKeys.forEach { succKey ->
        val succ = vertices[succKey]!!
        succ.predCnt -= 1
        if (succ.predCnt == 0) keys0.addLast(succKey)
      }
    }

    if (copy.vertices.values.any { v -> v.predCnt != 0 }) {
      throw IllegalStateException("The graph is not a DAG.")
    }
  }
}

/**
 * Mutable vertex.
 */
internal data class MutableVertex<K, V>(
  val data: V? = null,
  val succKeys: MutableList<K> = mutableListOf(),
  var predCnt: Int = 0,
)
