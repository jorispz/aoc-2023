fun main() {

    val lines = readInput("Day14")
    val map = Map2D(lines) { c, _ -> c }

    fun load() = map.find('O').sumOf { map.height - it.y }

    fun move(heading: Heading) {
        var stable = false
        while (!stable) {
            stable = true
            map.positions().forEach { p ->
                val next = p.move(heading)
                if (map[p] == 'O' && map.contains(next) && map[next] == '.') {
                    map[next] = 'O'
                    map[p] = '.'
                    stable = false
                }
            }
        }
    }
    // Manual inspection: value 104626 at index 181 seen again at 181, 195, 209, 223
    // 181 + 71428558 * 14 + 7 = 1000000000
    // So we need the value at 188
    val seenValues = mutableMapOf<Int, Int>()
    (1..188).forEach {
        move(Heading.N)
        move(Heading.W)
        move(Heading.S)
        move(Heading.E)
        if (it == 1) println("Part 1: ${load()}")
    }

    load().print { "Part 2: $it" }
}

