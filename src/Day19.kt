data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {

    operator fun get(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("Unknown property $c")
    }
}

data class PartRange(val x: IntRange, val m: IntRange, val a: IntRange, val s: IntRange) {

    operator fun get(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("Unknown property $c")
    }

    fun split(property: Char, value: Int): Pair<PartRange?, PartRange?> {
        val range = this[property]
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


data class Workflow(val name: String, val rules: List<Rule>)

sealed class Rule()

data class LessThan(val property: Char, val value: Int, val ifTrue: Rule) : Rule()
data class GreaterThan(val property: Char, val value: Int, val ifTrue: Rule) : Rule()
data class Redirect(val workflow: String) : Rule()
data object Accept : Rule()
data object Reject : Rule()


fun main() {

    fun parse(fileName: String): Pair<List<Workflow>, List<PartRange>> {
        val lines = readInput(fileName)
        val (ruleLines, inputLines) = lines.indexOfFirst { it.isBlank() }
            .let { Pair(lines.take(it), lines.drop(it + 1)) }

        val workflows = ruleLines.map { line ->
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

    fun Workflow.eval(p: PartRange): List<Pair<PartRange, Rule>> {
        val results = mutableListOf<Pair<PartRange, Rule>>()
        var remaining: PartRange? = p
        rules.forEach { rule ->
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

    fun pairs(start: PartRange): List<Pair<PartRange, Rule>> {
        var work = listOf<Pair<PartRange, Rule>>(Pair(start, Redirect("in")))
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


    parts.flatMap {
        pairs(it)
    }.print().filter { it.second == Accept }.map { it.first }.distinct().print()
        .sumOf { it.x.first + it.m.first + it.a.first + it.s.first }.print { "Part 1: $it" }

    val startRange = PartRange(1..4000, 1..4000, 1..4000, 1..4000)
    var work = pairs(startRange)
    work.filter { it.second is Accept }.sumOf { (range, rule) ->
        (range.x.length + 1).toLong() * (range.m.length + 1) * (range.a.length + 1) * (range.s.length + 1)
    }.print { "Part 2: $it" }


}