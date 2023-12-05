data class Mapping(val sourceRange: LongRange, val offset: Long) {
    fun mapRange(input: LongRange): Pair<LongRange?, List<LongRange>> {
        val unmapped = mutableListOf<LongRange>()

        if (input.first > sourceRange.last || input.last < sourceRange.first) {
            unmapped.add(input)
            return Pair(null, listOf(input))
        }
        if (input.first < sourceRange.first) {
            unmapped.add(LongRange(input.first, sourceRange.first - 1))
        }
        if (input.last > sourceRange.last) {
            unmapped.add(LongRange(sourceRange.last + 1, input.last))
        }
        return Pair(
            LongRange(
                input.first.coerceAtLeast(sourceRange.first) + offset,
                sourceRange.last.coerceAtMost(input.last) + offset
            ), unmapped
        )
    }
}

fun main() {

    fun parse(lines: List<String>): Pair<List<Long>, List<List<Mapping>>> {
        val seeds = lines.first().split(" ").drop(1).map { it.toLong() }
        val maps = mutableListOf<List<Mapping>>()
        var mappings = mutableListOf<Mapping>()
        lines.drop(2).forEach { line ->
            if (line.endsWith("map:")) {
                //ignore
            } else if (line.isEmpty()) {
                maps.add(mappings)
                mappings = mutableListOf()
            } else {
                val numbers = line.split(" ").map { it.toLong() }
                mappings += Mapping(LongRange(numbers[1], numbers[1] + numbers[2] - 1), numbers[0] - numbers[1])
            }
        }
        return Pair(seeds, maps)
    }

    fun part1(seeds: List<Long>, map: List<List<Mapping>>): Long {
        return seeds.minOf { seed ->
            map.fold(seed) { current, mappings ->
                current + (mappings.find { current in it.sourceRange }?.offset ?: 0L)
            }
        }
    }

    fun part2(seedRanges: List<LongRange>, map: List<List<Mapping>>): Long {

        fun List<Mapping>.mapRange(input: List<LongRange>): List<LongRange> {
            val mapped = mutableListOf<LongRange>()
            val todo = input.toMutableList()

            this.forEach { mapping ->
                todo.toList().forEach { r ->
                    todo.remove(r)
                    val (m, u) = mapping.mapRange(r)
                    m?.let { mapped.add(it) }
                    todo.addAll(u)
                }
            }
            return mapped + todo
        }

        return seedRanges.minOf { seedRange ->
            map.fold(listOf(seedRange)) { l, lm -> lm.mapRange(l) }.minOf { it.first }
        }

    }

    val (testSeeds, testMappings) = parse(readInput("Day05_test"))
    check(part1(testSeeds, testMappings) == 35L)
    check(part2(testSeeds.chunked(2) { LongRange(it.first(), it.last() + it.first() - 1) }, testMappings) == 46L)

    val (seeds, mappings) = parse(readInput("Day05"))
    part1(seeds, mappings).print { "Part 1: $it" }
    part2(seeds.chunked(2) { LongRange(it.first(), it.last() + it.first() - 1) }, mappings).print { "Part 2: $it" }

}