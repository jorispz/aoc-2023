import kotlin.math.min


val String.possibleMirrorPositions: Set<Int>
    get() {
        val maxIndex = this.length - 1
        return (0..<maxIndex).filter { i ->
            val checkLength = min(i + 1, maxIndex - i)
            (0..<checkLength).all { step ->
                this[i - step] == this[i + step + 1]
            }
        }.toSet()
    }

fun main() {

    check("####".possibleMirrorPositions == listOf(0, 1, 2))
    check("#.#".possibleMirrorPositions == emptyList<Int>())
    check("#.##.#".possibleMirrorPositions == listOf(2))

    fun parse(filename: String): List<Map2D<Char>> {
        val lines = readInput(filename)
        val lists = mutableListOf(mutableListOf<String>())
        lines.forEach { line ->
            if (line.isBlank()) {
                lists.add(mutableListOf())
            } else {
                lists.last().add(line)
            }
        }
        return lists.map {
            Map2D(it) { c, _ -> c }
        }
    }

    fun Map2D<Char>.mirrors(): Pair<Set<Int>, Set<Int>> {
        val verticalMirror = this.rows().map { r ->
            r.joinToString("").possibleMirrorPositions
        }.reduce { acc, l -> l.intersect(acc) }

        val horizontalMirror = this.columns().map { c ->
            c.joinToString("").possibleMirrorPositions
        }.reduce { acc, l -> l.intersect(acc) }

        return Pair(verticalMirror, horizontalMirror)
    }

    val maps = parse("Day13")
    val result = maps.map { map ->
        val (verticalMirrors, horizontalMirrors) = map.mirrors()
        check((verticalMirrors.size + horizontalMirrors.size) == 1)
        val part1 = (verticalMirrors.sumOf { it + 1 } + horizontalMirrors.sumOf { 100 * (it + 1) })

        val newVerticals = mutableSetOf<Int>()
        val newHorizontals = mutableSetOf<Int>()
        map.positions().forEach { p ->
            val current = map[p]
            map[p] = if (current == '#') '.' else '#'
            map.mirrors().also {
                newVerticals.addAll(it.first)
                newHorizontals.addAll(it.second)
            }
            map[p] = current
        }
        val part2 =
            ((newVerticals - verticalMirrors).sumOf { it + 1 } + (newHorizontals - horizontalMirrors).sumOf { 100 * (it + 1) }).print()

        Pair(part1, part2)
    }
    result.sumOf { it.first }.print { "Part 1: $it" }
    result.sumOf { it.second }.print { "Part 2: $it" }


}