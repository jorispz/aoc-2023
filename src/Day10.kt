fun main() {

//    val map = Map2D(readInput("Day10_test_3")) { c, p -> c }
//    val start = map.find('S').single()
//    val startPipe = 'F'

    val map = Map2D(readInput("Day10")) { c, p -> c }
    val start = map.find('S').single()
    val startPipe = '-'
    fun pipeAt(p: Position) = if (p == start) startPipe else map[p]

    val loop = mutableListOf(start)
    do {
        val previous = if (loop.size >= 2) loop[loop.size - 2] else null
        val current = loop.last()
        val pipe = pipeAt(current)
        val next = when (pipe) {
            '|' -> if (previous != current.up) current.up else current.down
            '-' -> if (previous != current.right) current.right else current.left
            'L' -> if (previous != current.up) current.up else current.right
            'J' -> if (previous != current.left) current.left else current.up
            '7' -> if (previous != current.down) current.down else current.left
            'F' -> if (previous != current.right) current.right else current.down
            else -> error("Illegal map element ${map[current]}")
        }
        loop.add(next)
    } while (next != start)
    loop.dropLast(1)
    (loop.size / 2).print { "Part 1: $it" } // 6831


    (0..map.maxY).sumOf { y ->
        val row = (0..map.maxX).map { x -> Position(x, y) }
        val intersections = mutableListOf<Pair<Position, Int>>()
        var segmentStartPipe: Char? = null
        loop.filter { it in row }.sortedBy { it.x }.forEach { p ->
            val pipe = pipeAt(p)
            if (pipe == '|') {
                intersections.add(p to 1)
            } else if (pipe in listOf('L', 'F')) {
                segmentStartPipe = pipe
            } else if (pipe in listOf('J', '7')) {
                if (segmentStartPipe == 'L' && pipe == 'J') {
                    intersections.add(p to 2)
                } else if (segmentStartPipe == 'L' && pipe == '7') {
                    intersections.add(p to 1)
                } else if (segmentStartPipe == 'F' && pipe == '7') {
                    intersections.add(p to 2)
                } else if (segmentStartPipe == 'F' && pipe == 'J') {
                    intersections.add(p to 1)
                }
            }
        }
        row.filter { it !in loop }.count { p ->
            intersections.filter { it.first.x > p.x }.sumOf { it.second } % 2 != 0
        }
    }.print { "Part 2: $it" } // 3-5

}

