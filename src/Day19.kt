data class PartRange(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {

    fun split(property: Char, value: Int): Pair<PartRange?, PartRange?> {
        val range = when (property) {
            'x' -> x
            'm' -> m
            'a' -> a
            's' -> s
            else -> error("Unknown property $property")
        }
        return if (value !in range) {
            if (value < range.first) Pair(null, this) else Pair(this, null)
        } else {
            val left = range.first..<value
            val right = value..range.last
            when (property) {
                'x' -> Pair(PartRange(left, m, a, s), PartRange(right, m, a, s))
                'm' -> Pair(PartRange(x, left, a, s), PartRange(x, right, a, s))
                'a' -> Pair(PartRange(x, m, left, s), PartRange(x, m, right, s))
                's' -> Pair(PartRange(x, m, a, left), PartRange(x, m, a, right))
                else -> error("Unknown property $property")
            }
        }
    }
}

sealed class Step()
data class LessThan(val property: Char, val value: Int, val ifTrue: Step) : Step()
data class GreaterThan(val property: Char, val value: Int, val ifTrue: Step) : Step()
data class Redirect(val workflow: String) : Step()
data object Accept : Step()
data object Reject : Step()

data class Workflow(val name: String, val steps: List<Step>) {
    fun eval(p: PartRange): List<Pair<PartRange, Step>> {
        val results = mutableListOf<Pair<PartRange, Step>>()
        var remaining: PartRange? = p
        steps.forEach { rule ->
            if (remaining != null) {
                when (rule) {
                    is LessThan -> {
                        val (left, right) = remaining!!.split(rule.property, rule.value)
                        if (left != null) results.add(Pair(left, rule.ifTrue))
                        remaining = right
                    }

                    is GreaterThan -> {
                        val (left, right) = remaining!!.split(rule.property, rule.value + 1)
                        if (right != null) results.add(Pair(right, rule.ifTrue))
                        remaining = left
                    }

                    else -> {
                        results.add(Pair(remaining!!, rule))
                        remaining = null
                    }
                }
            }
        }
        return results
    }
}


fun main() {

    fun parse(fileName: String): Pair<List<Workflow>, List<PartRange>> {
        val lines = readInput(fileName)
        val (workflowLines, inputLines) = lines.indexOfFirst { it.isBlank() }
            .let { Pair(lines.take(it), lines.drop(it + 1)) }

        val workflows = workflowLines.map { line ->
            line.indexOfFirst { it == '{' }.let { i ->
                val name = line.substring(0, i)

                val rules = line.substring(i + 1, line.length - 1).split(",").map { r ->
                    if (!r.contains(":")) {
                        when (r) {
                            "A" -> Accept
                            "R" -> Reject
                            else -> Redirect(r)
                        }
                    } else {
                        val property = r[0]
                        val op = r[1]
                        val value = r.substring(2, r.indexOf(":")).toInt()
                        val result = r.substring(r.indexOf(":") + 1).let {
                            when (it) {
                                "A" -> Accept
                                "R" -> Reject
                                else -> Redirect(it)
                            }
                        }

                        when (op) {
                            '<' -> LessThan(property, value, result)
                            '>' -> GreaterThan(property, value, result)
                            else -> error("Unknown operator $op")
                        }
                    }

                }
                Workflow(name, rules)
            }
        }

        val inputs = inputLines.map {
            it.substring(1..it.length - 2).split(",").map { l ->
                l.substring(2..<l.length).toInt()
            }
        }.map { PartRange(it[0]..it[0], it[1]..it[1], it[2]..it[2], it[3]..it[3]) }

        return Pair(workflows, inputs)
    }

    val (workflows, parts) = parse("Day19")

    fun solve(start: PartRange): List<Pair<PartRange, Step>> {
        var work = listOf<Pair<PartRange, Step>>(Pair(start, Redirect("in")))
        while (work.any { it.second is Redirect }) {
            work = work.flatMap { pair ->
                val (range, rule) = pair
                when (rule) {
                    is Redirect -> {
                        val w = workflows.single { it.name == rule.workflow }
                        w.eval(range)
                    }
                    Accept -> listOf(pair)
                    Reject -> listOf(pair)
                    else -> error("$range $rule")
                }
            }
        }
        return work
    }


    parts.flatMap { solve(it) }.filter { it.second == Accept }
        .sumOf { (pr, _) -> pr.x.first + pr.m.first + pr.a.first + pr.s.first }
        .print { "Part 1: $it" }

    solve(PartRange(1..4000, 1..4000, 1..4000, 1..4000)).filter { it.second is Accept }
        .sumOf { (pr, _) ->
            (pr.x.length + 1).toLong() * (pr.m.length + 1) * (pr.a.length + 1) * (pr.s.length + 1)
        }.print { "Part 2: $it" }


}