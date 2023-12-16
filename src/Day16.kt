data class Tile(val type: Char) {

    val passed = mutableSetOf<Beam>()
    fun energize(beam: Beam): List<Beam> {
        if (passed.contains(beam)) {
            return emptyList()
        } else {
            passed.add(beam)
            val pos = beam.position
            val heading = beam.heading
            return when ("${beam.heading}${type}") {
                "N.", "E.", "S.", "W." -> listOf(Beam(pos.move(heading), heading))
                "E-", "W-", "N|", "S|" -> listOf(Beam(pos.move(heading), heading))
                "E|", "W|" -> listOf(Beam(pos.up, Heading.N), Beam(pos.down, Heading.S))
                "N-", "S-" -> listOf(Beam(pos.left, Heading.W), Beam(pos.right, Heading.E))
                "E\\", "W/" -> listOf(Beam(pos.down, Heading.S))
                "E/", "W\\" -> listOf(Beam(pos.up, Heading.N))
                "N\\", "S/" -> listOf(Beam(pos.left, Heading.W))
                "N/", "S\\" -> listOf(Beam(pos.right, Heading.E))
                else -> error("${beam.heading}${type}")
            }
        }
    }

    fun reset() = passed.clear()

    override fun toString() = if (passed.size == 0) type.toString() else passed.size.toString()
}

data class Beam(val position: Position, val heading: Heading)

fun main() {

    fun parse(file: String): Map2D<Tile> {
        val lines = readInput(file)
        return Map2D(lines) { c, p -> Tile(c) }
    }


    fun shine(map: Map2D<Tile>, origin: Beam): Int {
        val beams = map[origin.position].energize(origin).toMutableList()
        while (beams.isNotEmpty()) {
            val beam = beams.removeFirst()
            beams.addAll(map.getOrNull(beam.position)?.energize(beam) ?: emptyList())
        }

        return map.positions().filter { map[it].passed.isNotEmpty() }.count()
    }

    var map = parse("Day16")
    fun reset() = map.also {
        map.positions().forEach { map[it].reset() }
    }

    shine(reset(), Beam(Position.ORIGIN, Heading.E)).print { "Part 1: $it" } // 7728

    ((0..map.maxX).map { Beam(Position(it, 0), Heading.S) } +
            (0..map.maxX).map { Beam(Position(it, map.maxY), Heading.N) } +
            (0..map.maxY).map { Beam(Position(0, it), Heading.E) } +
            (0..map.maxY).map { Beam(Position(map.maxX, it), Heading.W) }).maxOf { shine(reset(), it) }
        .print { "Part 2: $it" } // 8061

}