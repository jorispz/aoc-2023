fun main() {

    fun String.hash() = fold(0) { hash, char -> ((hash + char.code) * 17) % 256 }

    val input = readInput("Day15").single().split(",")
    input.sumOf { it.hash() }.print { "Part 1: $it" }

    val boxes = Array(256) { mutableListOf<Pair<String, String>>() }
    input.forEach { s ->
        val (label, focalLength) = s.split("=", "-")
        val box = boxes[label.hash()]

        when (focalLength) {
            "" -> {
                box.removeAll { it.first == label }
            }

            else -> {
                val index = box.indexOfFirst { it.first == label }
                if (index == -1) box.add(Pair(label, focalLength)) else box[index] = Pair(label, focalLength)
            }
        }
    }
    boxes.withIndex().sumOf { (boxNumber, box) ->
        box.withIndex().sumOf { (lensIndex, lens) ->
            (boxNumber + 1) * (lensIndex + 1) * lens.second.toInt()
        }
    }.print { "Part 2: $it" }

}