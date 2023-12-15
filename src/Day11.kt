fun main() {
    fun parse(lines: List<String>): Map2D<Char> {
        return Map2D(lines) { c, _ -> c }
    }

    fun solve(map: Map2D<Char>, expansion: Long): Long {
        val emptyRows = (0..map.maxY).filter { map.row(it).all { it == '.' } }
        val emptyColumns = (0..map.maxX).filter { map.column(it).all { it == '.' } }
        val galaxies = map.find('#').toList()
        val pairs = galaxies.flatMapIndexed { i, g -> galaxies.subList(i + 1, galaxies.size).map { Pair(g, it) } }

        return pairs.sumOf {
            val moveHorizontal = if (it.first.x < it.second.x) (it.first.x..it.second.x) else (it.second.x..it.first.x)
            val moveVertical = if (it.first.y < it.second.y) (it.first.y..it.second.y) else (it.second.y..it.first.y)
            val emptyRowsCrossed = emptyRows.count { it in moveVertical }
            val emptyColumnsCrossed = emptyColumns.count { it in moveHorizontal }
            moveHorizontal.length + (expansion - 1) * emptyColumnsCrossed + moveVertical.length + (expansion - 1) * emptyRowsCrossed
        }
    }

    val testMap = parse(readInput("Day11_test"))
    val map = parse(readInput("Day11"))
    solve(map, 2).print { "Part 1: $it" }
    solve(map, 1000000).print { "Part 2: $it" }


}