interface Graph<V : Graph.Vertex> {
    interface Vertex

    fun heuristicDistance(a: V, b: V): Long
    fun weight(a: V, b: V): Long

    fun getNeighbors(v: V): Set<V>
}


/*
This implements the A* algorithm to find the shortest route in a graph of vertices with weighted edges. Note that
this becomes Dijkstra's algorithm when the heuristic function is set to constant 0

Hasn't been properly tested accept for AoC 2022 Day 12, which is Dijkstra
 */
fun <V : Graph.Vertex> findShortestPath(graph: Graph<V>, start: V, end: (V) -> Boolean): List<V> {

    val cameFrom = mutableMapOf<V, V>()
    val openVertices = mutableSetOf(start)
    val closedVertices = mutableSetOf<V>()
    val costFromStart = mutableMapOf(start to 0L)
    val estimatedTotalCost = mutableMapOf(start to 0L)

    while (openVertices.isNotEmpty()) {
        val currentPos = openVertices.minBy { estimatedTotalCost.getValue(it) }
        if (currentPos == end) {
            val path = mutableListOf(currentPos)
            var current = currentPos
            while (cameFrom.containsKey(current)) {
                current = cameFrom.getValue(current)
                path.add(0, current)
            }
            return path.toList()
        }
        openVertices.remove(currentPos)
        closedVertices.add(currentPos)
        graph.getNeighbors(currentPos).filterNot { closedVertices.contains(it) }  // Exclude previous visited vertices
            .forEach { neighbour ->
                val score = costFromStart.getValue(currentPos) + graph.weight(currentPos, neighbour)
                if (score < costFromStart.getOrElse(neighbour) { Long.MAX_VALUE }) {
                    if (!openVertices.contains(neighbour)) {
                        openVertices.add(neighbour)
                    }
                    cameFrom.put(neighbour, currentPos)
                    costFromStart[neighbour] = score
                    estimatedTotalCost[neighbour] = score + 0L
                }
            }
    }
    return emptyList<V>()
}


fun main() {

    val map = Map2D(readInput("Day17_test")) { c, p -> c.digitToInt() }

    data class Node(val p: Position, val heading: Heading, val stepsInHeading: Int) : Graph.Vertex

    val graph = object : Graph<Node> {
        override fun heuristicDistance(a: Node, b: Node) = 0L

        override fun weight(a: Node, b: Node) = map[b.p].toLong()

        override fun getNeighbors(v: Node): Set<Node> {
            val (position, heading, numSteps) = v
            val headings =
                listOf(heading, heading.left(), heading.right()).let { if (numSteps == 3) it.drop(1) else it }
            return headings.mapNotNull {
                val p = position.move(it)
                if (map.contains(p)) Node(p, it, if (it == heading) numSteps + 1 else 1) else null
            }.toSet()
        }

    }

    findShortestPath(graph, Node(Position.ORIGIN, Heading.E, 0)) { it.p == Position(map.maxX, map.maxY) }.print()
}
