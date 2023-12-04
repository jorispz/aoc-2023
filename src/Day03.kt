sealed class EnginePart
class NumberedPart(val value: Int, val box: BoundingBox) : EnginePart() {
    override fun toString() = "${value.toString()} ($box)"
}

class SymbolPart(val symbol: String, val position: Position) : EnginePart() {
    override fun toString() = "$symbol ($position)"
}


fun main() {

    fun solve(input: List<String>): Pair<Int, Long> {
        val numbers = mutableListOf<NumberedPart>()
        val symbols = mutableListOf<SymbolPart>()
        input.forEachIndexed { lineNo, line ->
            numbers += Regex("""\d+""").findAll(line).map {
                NumberedPart(
                    it.value.toInt(),
                    BoundingBox(Position(it.range.first, lineNo), Position(it.range.last, lineNo))
                )
            }.toList()
            symbols += Regex("""[^\.\d]""").findAll(line).map { SymbolPart(it.value, Position(it.range.first, lineNo)) }
                .toList()
        }
        val part1 = numbers.filter { n -> symbols.any { s -> n.box.adjacentTo(s.position) } }.sumOf { it.value }

        val potentialGears = symbols.filter { it.symbol == "*" }
        val part2 = potentialGears.sumOf { s ->
            val adjacentNumbers = numbers.filter { it.box.adjacentTo(s.position) }
            if (adjacentNumbers.size == 2) adjacentNumbers[0].value.toLong() * adjacentNumbers[1].value.toLong() else 0
        }
        return Pair(part1, part2)
    }


    val testInput = readInput("Day03_test")
    val testSolution = solve(testInput)
    check(testSolution.first == 4361)
    check(testSolution.second == 467835L)

    val input = readInput("Day03")
    val solution = solve(input)
    solution.print { "Part 1: ${it.first}" }
    solution.print { "Part 2: ${it.second}" }


}