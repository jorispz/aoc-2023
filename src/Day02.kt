data class Round(val red: Int, val green: Int, val blue: Int) {
    fun possible(maxRed: Int, maxGreen: Int, maxBlue: Int): Boolean {
        return red <= maxRed && green <= maxGreen && blue <= maxBlue
    }
}

data class Game(val gameID: Int, val rounds: List<Round>) {
    val minimumPower = rounds.maxOf { it.red } * rounds.maxOf { it.green } * rounds.maxOf { it.blue }
}

fun main() {

    fun part1(games: List<Game>) = games.filter { it.rounds.all { it.possible(12, 13, 14) } }.sumOf { it.gameID }
    fun part2(games: List<Game>) = games.fold(0) { acc, game -> acc + game.minimumPower }

    val testData = readInput("Day02_test")
    val testGames = parse(testData)
    check(part1(testGames) == 8)
    check(part2(testGames) == 2286)

    val data = readInput("Day02")
    val games = parse(data)
    part1(games).print { "Part 1: $it" }
    part2(games).print { "Part 2: $it" }
}

fun parse(data: List<String>): List<Game> {
    return data.map { line ->
        val (gameID, game) = Regex("""Game (\d*):(.*)""").matchEntire(line)!!.destructured
        val rounds = game.split(";").map { round ->
            val reds = Regex("""(\d{1,2}) red""").find(round)?.destructured?.toList()?.first()?.toInt() ?: 0
            val greens = Regex("""(\d{1,2}) green""").find(round)?.destructured?.toList()?.first()?.toInt() ?: 0
            val blues = Regex("""(\d{1,2}) blue""").find(round)?.destructured?.toList()?.first()?.toInt() ?: 0
            Round(reds, greens, blues)
        }
        Game(gameID.toInt(), rounds)
    }
}