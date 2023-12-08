package aoc2023

import utils.InputUtils

enum class Direction{
    LEFT, RIGHT;
}
fun parseDirection(c: Char) = when(c) {
    'L' -> Direction.LEFT
    'R' -> Direction.RIGHT
    else -> throw IllegalArgumentException("Wrong char $c")
}

class Day08(val instructions: List<Direction>, val maps: Map<String, Pair<String, String>>) {
    fun move(current: String, dir: Direction): String =
        when(dir) {
            Direction.LEFT -> maps[current]!!.first
            Direction.RIGHT -> maps[current]!!.second
        }


}

fun parseDay08(input: List<String>): Day08 {
    val (instructionsList, maps) = blocksOfLines(input).toList()
    val tree = maps.parsedBy("(\\w+) = \\((\\w+), (\\w+)\\)".toRegex()) {
        val (label, left, right) = it.destructured
        label to Pair(left, right)
    }.toMap()
    val instructions = instructionsList[0].map { parseDirection(it) }
    return Day08(instructions, tree)
}
fun main() {
    val testInput = """LLR

AAA = (BBB, BBB)
BBB = (AAA, ZZZ)
ZZZ = (ZZZ, ZZZ)""".trimIndent().split("\n")


    fun part1(input: List<String>): Int {
        val problem = parseDay08(input)

        val locations = sequence<String> {
            var current = "AAA"
            yield(current)
            problem.instructions.asSequence().repeatForever().forEach { dir ->
                current = problem.move(current, dir)
                yield(current)
            }
        }
        return locations
            //.onEach { println(it) }
            .takeWhile { it != "ZZZ" }.count()

    }


    fun part2(input: List<String>): Int {
        val problem = parseDay08(input)

        val initial = problem.maps.keys.filter { it.endsWith("A") }
        println(initial)
        return problem.instructions.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 6)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 8)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
