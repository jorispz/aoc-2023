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
}

sealed class RuleResult {
    data object Accept : RuleResult()
    data object Reject : RuleResult()
    data object Continue : RuleResult()
    data class Delegate(val workflow: String) : RuleResult()
}


data class Workflow(val name: String, val rules: List<Rule>)

sealed class Rule() {
    abstract fun eval(p: Part): RuleResult
//    abstract fun eval(p: PartRange): Pair<Pair<PartRange, RuleResult>, Pair<PartRange, RuleResult>?>
}

data class LessThan(val property: Char, val value: Int, val result: RuleResult) : Rule() {
    override fun eval(p: Part) = if (p[property] < value) result else RuleResult.Continue
}

data class GreaterThan(val property: Char, val value: Int, val result: RuleResult) : Rule() {
    override fun eval(p: Part): RuleResult = if (p[property] > value) result else RuleResult.Continue
}

data class Forward(val result: RuleResult) : Rule() {
    override fun eval(p: Part) = result
}


fun main() {

    fun parse(fileName: String): Pair<List<Workflow>, List<Part>> {
        val lines = readInput(fileName)
        val (ruleLines, inputLines) = lines.indexOfFirst { it.isBlank() }
            .let { Pair(lines.take(it), lines.drop(it + 1)) }

        val workflows = ruleLines.map { line ->
            line.indexOfFirst { it == '{' }.let { i ->
                val name = line.substring(0, i)

                val rules = line.substring(i + 1, line.length - 1).split(",").map { r ->
                    if (!r.contains(":")) {
                        when (r) {
                            "A" -> Forward(RuleResult.Accept)
                            "R" -> Forward(RuleResult.Reject)
                            else -> Forward(RuleResult.Delegate(r))
                        }
                    } else {
                        val property = r[0]
                        val op = r[1]
                        val value = r.substring(2, r.indexOf(":")).toInt()
                        val result: RuleResult = r.substring(r.indexOf(":") + 1).let {
                            when (it) {
                                "A" -> RuleResult.Accept
                                "R" -> RuleResult.Reject
                                else -> RuleResult.Delegate(it)
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
        }.map { Part(it[0], it[1], it[2], it[3]) }

        return Pair(workflows, inputs)
    }

    val (workflows, parts) = parse("Day19")
    val start = workflows.single { it.name == "in" }

    parts.map { part ->
        var currentRuleSet = start
        var ruleNumber = 0
        var lastResult: RuleResult? = null

        while (lastResult != RuleResult.Accept && lastResult != RuleResult.Reject) {
            lastResult = currentRuleSet.rules[ruleNumber].eval(part)
            when (lastResult) {
                is RuleResult.Continue -> ruleNumber++
                is RuleResult.Delegate -> {
                    ruleNumber = 0
                    currentRuleSet = workflows.single { it.name == lastResult.workflow }
                }

                else -> {}
            }
        }
        Pair(part, lastResult)

    }.sumOf { if (it.second == RuleResult.Accept) (it.first.x + it.first.m + it.first.a + it.first.s) else 0 }.print()

}