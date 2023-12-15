data class ConditionRecord(val conditions: String, val damagedGroups: List<Int>) {
    private val contiguousSegments: List<String> = conditions.split(".").filter { it.isNotEmpty() }

    val couldBeValid: Boolean
        get() {
            if (done) return contiguousSegments.map { it.length } == damagedGroups
            val minimumSegments = contiguousSegments.filterNot { it.all { it == '?' } }.size
            if (minimumSegments > damagedGroups.size) return false
            return true
        }

    val done = conditions.none { it == '?' }

    fun next(): List<ConditionRecord> {
        return if (done) emptyList() else
            listOf(
                copy(conditions = conditions.replaceFirst("?", "#")),
                copy(conditions = conditions.replaceFirst("?", "."))
            )
    }

}

fun main() {

    fun parse(lines: List<String>): List<ConditionRecord> = lines.map {
        val (a, b) = it.split(" ")
        ConditionRecord(a, b.split(",").map { it.toInt() })
    }

    val records = parse(readInput("Day12")).print()
    records.sumOf { record ->
        val queue = ArrayDeque<ConditionRecord>()
        queue.add(record)
        var result = 0
        while (queue.isNotEmpty()) {
            val current = queue.removeLast()
            if (current.done) {
                if (current.couldBeValid) {
                    result++
                }
            } else {
                if (current.couldBeValid) queue.addAll(current.next())
            }
        }
        result
    }.print()


}