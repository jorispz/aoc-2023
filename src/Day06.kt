import kotlin.math.floor
import kotlin.math.sqrt

data class Race(val duration: Long, val record: Long) {

    val possibleWaysToWin: Long
        get() {
            val a = -1L
            val b = duration.toLong()
            val c = -record.toLong()
            val z = sqrt(b * b - 4 * a * c.toDouble())
            val x0 = floor((-b - z) / (2 * a))
            val x1 = floor((-b + z) / (2 * a))
            return (x0 - x1).toLong()
        }

}

fun main() {

    fun parse(lines: List<String>): List<Race> {
        val times = lines[0].split(" ").filter { it.isNotEmpty() }.drop(1).map { it.toLong() }
        val targets = lines[1].split(" ").filter { it.isNotEmpty() }.drop(1).map { it.toLong() }
        return times.zip(targets).map { Race(it.first, it.second) }
    }

    fun part1(races: List<Race>): Int {
        return races.map { r ->
            (1..<r.duration).map { (r.duration - it) * it }.count { it > r.record }
        }.reduce { a, b -> a * b }
    }

    val testRaces = parse(readInput("Day06_test"))
    check(part1(testRaces) == 288)

    val races = parse(readInput("Day06"))
    part1(races).print { "Part 1: $it" }

    Race(71530, 940200).possibleWaysToWin.print()
    Race(56977793L, 499221010971440L).possibleWaysToWin.print()

    // Distance traveled =  (71530 - T) * T
    // Target = 940200
    // -T^2 + 71530 * T - 940200 = 0
    // root: 71516.853448458
    //          13.146551542253
    // 71516 - 13 = 71503

    // 56977793
    // 499221010971440
    // 46163630.241029
    //10814162.758971
    // 46163630-10814162 =35349468
}