enum class Rank(val order: Int) {
    N2(2), N3(3), N4(4), N5(5), N6(6), N7(7), N8(8), N9(9), T(10), J(11), Q(12), K(13), A(14)
}

enum class HandType {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

data class Hand(val cards: List<Rank>, val bid: Int)

fun main() {

    fun parse(lines: List<String>): List<Hand> {
        return lines.map { line ->
            val (cards, bid) = line.split(" ").let {
                Pair(it.first().map { c ->
                    when (c) {
                        '2' -> Rank.N2
                        '3' -> Rank.N3
                        '4' -> Rank.N4
                        '5' -> Rank.N5
                        '6' -> Rank.N6
                        '7' -> Rank.N7
                        '8' -> Rank.N8
                        '9' -> Rank.N9
                        'T' -> Rank.T
                        'J' -> Rank.J
                        'Q' -> Rank.Q
                        'K' -> Rank.K
                        'A' -> Rank.A
                        else -> error(c)
                    }
                }, it.last().toInt())
            }
            Hand(cards, bid)
        }
    }


    fun part1(game: List<Hand>): Int {

        fun Hand.handType(): HandType {
            this.cards.flatMap { if (it != Rank.J) listOf(it) else listOf(it) }
            return this.cards.groupingBy { it }.eachCount().let {
                if (it.values.contains(5)) {
                    HandType.FIVE_OF_A_KIND
                } else if (it.values.contains(4)) {
                    HandType.FOUR_OF_A_KIND
                } else if (it.values.contains(3) && it.values.contains(2)) {
                    HandType.FULL_HOUSE
                } else if (it.values.contains(3)) {
                    HandType.THREE_OF_A_KIND
                } else if (it.values.count { it == 2 } == 2) {
                    HandType.TWO_PAIR
                } else if (it.values.contains(2)) {
                    HandType.ONE_PAIR
                } else {
                    HandType.HIGH_CARD
                }
            }
        }

        val compareHands = Comparator<Hand> { a, b ->
            val sortByType = a.handType().compareTo(b.handType())
            if (sortByType != 0) sortByType else a.cards.mapIndexed { i, c -> c.compareTo(b.cards[i]) }
                .first { it != 0 }
        }

        return game.sortedWith(compareHands).withIndex().sumOf { (it.index + 1) * it.value.bid }
    }

    fun part2(game: List<Hand>): Int {

        fun Hand.handType(): HandType {
            return this.cards.groupingBy { it }.eachCount().let {
                if (it.values.contains(5)) {
                    HandType.FIVE_OF_A_KIND
                } else if (it.values.contains(4)) {
                    if (it[Rank.J] == 1 || it[Rank.J] == 4) HandType.FIVE_OF_A_KIND else HandType.FOUR_OF_A_KIND
                } else if (it.values.contains(3) && it.values.contains(2)) {
                    if (it[Rank.J] in 2..3) HandType.FIVE_OF_A_KIND else HandType.FULL_HOUSE
                } else if (it.values.contains(3)) {
                    if (it[Rank.J] == 1 || it[Rank.J] == 3) HandType.FOUR_OF_A_KIND else HandType.THREE_OF_A_KIND
                } else if (it.values.count { it == 2 } == 2) {
                    if (it[Rank.J] == 2) HandType.FOUR_OF_A_KIND else if (it[Rank.J] == 1) HandType.FULL_HOUSE else HandType.TWO_PAIR
                } else if (it.values.contains(2)) {
                    if (it[Rank.J] in 1..2) HandType.THREE_OF_A_KIND else HandType.ONE_PAIR
                } else {
                    if (it[Rank.J] == 1) HandType.ONE_PAIR else HandType.HIGH_CARD
                }
            }
        }

        val compareHands = Comparator<Hand> { a, b ->
            val sortByType = a.handType().compareTo(b.handType())
            if (sortByType != 0) sortByType else a.cards.mapIndexed { i, c ->
                val other = b.cards[i]
                if (c == Rank.J && other == Rank.J) 0 else if (c == Rank.J) -1 else if (other == Rank.J) 1 else c.compareTo(
                    b.cards[i]
                )
            }.first { it != 0 }
        }

        return game.sortedWith(compareHands).withIndex().sumOf { (it.index + 1) * it.value.bid }
    }

    val testGame = parse(readInput("Day07_test"))
    check(part1(testGame) == 6440)
    check(part2(testGame) == 5905)

    val hintGame = parse(readInput("Day07_hint"))
    check(part1(hintGame) == 6592)
    check(part2(hintGame) == 6839)

    val game = parse(readInput("Day07"))
    part1(game).print { "Part 1:  $it" }
    part2(game).print { "Part 2:  $it" }

}