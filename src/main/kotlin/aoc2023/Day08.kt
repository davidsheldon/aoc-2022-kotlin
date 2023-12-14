package aoc2023

import utils.InputUtils

private enum class Direction{
    LEFT, RIGHT;
}
private fun parseDirection(c: Char) = when(c) {
    'L' -> Direction.LEFT
    'R' -> Direction.RIGHT
    else -> throw IllegalArgumentException("Wrong char $c")
}

data class Position(val index: Int, val loc: String)

private class Day08(val instructions: List<Direction>, val maps: Map<String, Pair<String, String>>) {
    fun move(current: String, dir: Direction): String =
        when(dir) {
            Direction.LEFT -> maps[current]!!.first
            Direction.RIGHT -> maps[current]!!.second
        }

    fun loopSize(start: String): Int {
        return loopFrom(start).count()

    }

    private fun loopFrom(start: String): Sequence<Position> {
        val seen = mutableSetOf<Position>()
        val positions = positionsFrom(start)
        val loop = positions.takeWhile { seen.add(it) }
        return loop
    }

    fun locationsLoop(start: String) = loopFrom(start).map { it.loc }

    private fun positionsFrom(start: String): Sequence<Position> {
        val positions = sequence {
            var current = start
            do {
                instructions.forEachIndexed { i, dir ->
                    val pos = Position(i, current)
                    yield(pos)
                    current = move(current, dir)
                }
            } while (true)
        }
        return positions
    }

    fun locationsFrom(start: String): Sequence<String> {
        return positionsFrom(start).map { it.loc }
    }


}

private fun parseDay08(input: List<String>): Day08 {
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

        return problem.locationsFrom("AAA")
            //.onEach { println(it) }
            .takeWhile { it != "ZZZ" }.count()

    }


    fun part2(input: List<String>): Long {
        val problem = parseDay08(input)

        val initial = problem.maps.keys.filter { it.endsWith("A") }

        return initial.map { start ->
            val loop = problem.locationsLoop(start).toList()
            val ends = loop.count { it.endsWith("Z") }
            val index = loop.indexOfFirst { it.endsWith("Z") }
            println("$start has loop size ${loop.size}")
            println("    loop has $ends ends and index $index")
            index.toLong()
        }.lcm()


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
