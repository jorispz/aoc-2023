import kotlin.math.roundToInt

fun extrapolateByLagrange(yValues: List<Int>, x: Int): Int {
    val xValues = yValues.indices.toList()
    return xValues.fold(0.0) { acc, i ->
        acc + xValues.fold(yValues[i].toDouble()) { termAcc, j ->
            if (i != j) termAcc * (x - j) / (i - j) else termAcc
        }
    }.roundToInt()

//    var result = 0.0
//    for (i in xValues) {
//        var term = yValues[i].toDouble()
//
//        for (j in xValues) {
//            if (j != i) {
//                term = term * (x - xValues[j]).toDouble() / (xValues[i] - xValues[j]).toDouble()
//            }
//        }
//
//        result += term
//    }
//
//    return result.roundToLong()
}

fun main() {
    fun parse(file: String) = readInput(file).map { it.split(" ").map { it.toInt() } }

    val testInput = parse("Day09_test")
    check(testInput.sumOf { extrapolateByLagrange(it, it.size) } == 114)
    check(testInput.sumOf { extrapolateByLagrange(it, -1) } == 2)

    val input = parse("Day09")
    input.sumOf { extrapolateByLagrange(it, it.size) }.print { "Part 1: $it" } // 1681758908
    input.sumOf { extrapolateByLagrange(it, -1) }.print { "Part 2: $it" } // 803
}