package aoc2023

import utils.InputUtils


fun main() {
    val testInput = """
        
    """.trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        return input.size
    }


    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == -1)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 19)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
