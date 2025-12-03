package aoc2025

import utils.InputUtils
import kotlin.time.measureTime

fun main() {
        val testInput = """
987654321111111
811111111111119
234234234234278
818181911112111""".trimIndent().split("\n")


    fun part1(input: List<String>): Long {
        return input.sumOf { joltage(it) }.toLong()
    }


    fun part2(input: List<String>): Long {
        return input.sumOf { bigJoltage(it, 12).toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput)) // 3121910778619
    check(testValue == 357L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 3)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })
    //175304218462560
}

fun joltage(bank: String): Int {
    val first = bank.dropLast(1).max()
    val firstPos = bank.indexOf(first)
    val last = bank.substring(firstPos + 1).max()
    return "$first$last".toInt()
}

fun bigJoltage(bank: String, digits: Int): String {
    if (digits == 2) return joltage(bank).toString()
    val first = bank.dropLast(digits-1).max()
    val firstPos = bank.indexOf(first)
    val last = bank.substring(firstPos + 1)

    return "$first${bigJoltage(last, digits - 1)}"
}

