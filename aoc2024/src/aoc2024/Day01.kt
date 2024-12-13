package aoc2024

import utils.InputUtils
import kotlin.math.abs

fun main() {
        val testInput = """
            3   4
            4   3
            2   5
            1   3
            3   9
            3   3""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val pairs = toPairs(input)
        val firsts = pairs.map { it.first }.sorted()
        val seconds = pairs.map { it.second }.sorted()

        return firsts.zip(seconds).sumOf { (first, second) -> abs(first - second) }
    }


    fun part2(input: List<String>): Long {
        val pairs = toPairs(input)
        val firsts = pairs.map { it.first }.sorted()
        val secondCounts = pairs.groupingBy { it.second }.eachCount()

        return firsts.sumOf { it * secondCounts.getOrDefault(it, 0) }


    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 11L)

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 1)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}

private fun toPairs(input: List<String>): List<Pair<Long, Long>> = input.asSequence()
    .map { it.split("\\s+".toRegex(), 2).map { it.toLong() }.zipWithNext()[0] }
    .toList()
