data class Card(val id: Int, val winners: List<Int>, val actuals: List<Int>) {
    val matches by lazy {
        winners.intersect(actuals).size
    }

    fun points(): Int {
        return if (matches > 0) 1 shl matches - 1 else 0
    }
}

fun main() {

    fun parse(lines: List<String>): List<Card> {
        return lines.mapIndexed { lineNo, line ->
            val (winners, actuals) = line.substringAfter(":").split("|")
                .map { nums -> nums.split(" ").filter { it.isNotBlank() }.map { it.toInt() } }
            Card(lineNo, winners, actuals)
        }
    }

    fun part1(cards: List<Card>) = cards.sumOf { it.points() }
    fun part2(cards: List<Card>): Int {
        val numCards = IntArray(cards.size) { 1 }
        cards.forEach { card ->
            ((card.id + 1)..(card.id + card.matches).coerceAtMost(cards.size - 1)).forEach {
                numCards[it] = numCards[it] + numCards[card.id]
            }
        }
        return numCards.fold(0) { acc, i -> acc + i }
    }

    val testCards = parse(readInput("Day04_test"))
    check(part1(testCards) == 13)
    check(part2(testCards) == 30)

    val cards = parse(readInput("Day04"))
    part1(cards).print { "Part 1: $it" }
    part2(cards).print { "Part 2: $it" }


}