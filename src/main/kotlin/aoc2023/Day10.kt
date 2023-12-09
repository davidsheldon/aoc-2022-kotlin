package aoc2023

import utils.InputUtils

fun main() {
    val testInput = """0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45""".trimIndent().split("\n")


    fun part1(input: List<String>): Int {
        return input.size

    }


    fun part2(input: List<String>): Int {
        return input.size

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 114)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 10)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
