package aoc2021

import utils.InputUtils

fun main() {
    val testInput = """199
200
208
210
200
207
240
269
260
263""".split("\n")


    fun Sequence<Int>.countIncreases() = windowed(2).count { it.first() < it.last() }

    fun part1(input: List<String>): Int {
        return input.asSequence().map { it.toInt() }.countIncreases()
    }

    fun part2(input: List<String>): Int {
        return input.asSequence().map { it.toInt() }.windowed(3).map { it.sum() }.countIncreases()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 7)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 1)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
