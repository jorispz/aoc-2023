fun main() {

    fun part1(input: List<String>) =
        input.sumOf { l -> "${l.first { it.isDigit() }}${l.last { it.isDigit() }}".toInt() }

    fun part2(input: List<String>): Int {
        return part1(input.map { l -> l.replaceAll(digits) })
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val input = readInput("Day01")
    part1(input).print { "Part 1: $it" }

    val testInput2 = readInput("Day01_test_2")
    check(part2(testInput2) == 281)
    part2(input).print { "Part 2: $it" }
}

private val digits = mapOf(
    "one" to "o1e",
    "two" to "t2o",
    "three" to "t3e",
    "four" to "f4r",
    "five" to "f5e",
    "six" to "s6x",
    "seven" to "s7n",
    "eight" to "e8t",
    "nine" to "n9e"
)

fun String.replaceAll(map: Map<String, String>): String {
    return map.keys.fold(this) { s, key -> s.replace(key, map.getValue(key)) }
}
