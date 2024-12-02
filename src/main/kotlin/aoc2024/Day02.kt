package aoc2024

import utils.InputUtils
import kotlin.math.abs
import kotlin.math.sign

data class Report(val levels: Array<Int>) {

    fun safe(): Boolean {
        val diffs = levels.toList().zipWithNext { a, b -> b - a }
        val signs = diffs.map { it.sign }

        return diffs.all { abs(it) <= 3 && it != 0 } && signs.all { it == signs[0] }
    }

    fun canBeSafe(): Boolean {
        return safe() || withOneRemoved().any { it.safe() }
    }

    fun withOneRemoved(): List<Report> {
        return levels.mapIndexed { index, _ ->
            Report(levels.filterIndexed { i, _ -> i != index }.toTypedArray())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Report) return false

        if (!levels.contentEquals(other.levels)) return false

        return true
    }

    override fun hashCode(): Int {
        return levels.contentHashCode()
    }
}


fun main() {
    val testInput = """7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9""".split("\n")


    fun List<String>.toReports() = map { line ->
        Report(line.split(" ").map { it.toInt() }.toTypedArray())
    }

    fun part1(input: List<String>): Int {
        return input.toReports()
            .count { it.safe() }
    }


    fun part2(input: List<String>): Int {
        return input.toReports().count { it.canBeSafe() }

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 2)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 2)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
