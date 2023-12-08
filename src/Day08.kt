fun main() {

    fun parse(lines: List<String>): Pair<String, Map<String, Pair<String, String>>> {
        val nodeMap = mutableMapOf<String, Pair<String, String>>()
        val directions = lines.first()
        lines.drop(2).forEach { line ->
            val parts = line.split(" ")
            val key = parts[0]
            val left = parts[2].drop(1).dropLast(1)
            val right = parts[3].dropLast(1)
            nodeMap[key] = Pair(left, right)
        }
        return directions to nodeMap
    }

    fun part1(directions: String, nodeMap: Map<String, Pair<String, String>>): Int {
        var current = "AAA"
        val target = "ZZZ"
        var steps = 0
        directions.asSequence().infinite().forEach { step ->
            steps++
            val next = if (step == 'L') nodeMap.getValue(current).first else nodeMap.getValue(current).second
            if (next == target) return steps
            current = next
        }
        return 0
    }

    fun part2(directions: String, nodeMap: Map<String, Pair<String, String>>): Long {
        val startNodes = nodeMap.keys.filter { it.endsWith("A") }
        val frequencies = startNodes.map { start ->
            var steps = 0L
            var current = start
            directions.asSequence().infinite().forEach { step ->
                steps++
                val next = if (step == 'L') nodeMap.getValue(current).first else nodeMap.getValue(current).second
                if (next.endsWith("Z")) return@map steps
                current = next
            }
            return 0L
        }
        return frequencies.findLCM()
    }

    val (testDirections2, nodeMap2) = parse(readInput("Day08_test2"))
    check(part1(testDirections2, nodeMap2) == 6)

    val (directions, nodeMap) = parse(readInput("Day08"))
    part1(directions, nodeMap).print { "Part 1: $it" }
    part2(directions, nodeMap).print { "Part 2: $it" }

}