package aoc2024

import utils.InputUtils


fun main() {

    val testInput = """<<HERE>>""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        return input.sumOf { it.length }.toLong()
    }


    fun part2(input: List<String>): Long {
        return part1(input)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 161L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 21)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
