package aoc2023

import utils.InputUtils

fun main() {
    val testInput = """1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet""".split("\n")

    val test2Input = """
two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen        
    """.trimIndent().split("\n")

    val numbers = mapOf(
        "one" to "1",
        "two" to "2",
        "three" to "3",
        "four" to "4",
        "five" to "5",
        "six" to "6",
        "seven" to "7",
        "eight" to "8",
        "nine" to "9"
    )
    val allNumbers = numbers + numbers.entries.associate { it.value to it.value }

    fun extractNum(s: String): Long {
        return (buildString {
            append(s.first { it.isDigit() })
            append(s.last { it.isDigit() })
        }).toLong()
    }


    fun part1(input: List<String>): Long {
        return input.sumOf { extractNum(it) }
    }

    fun extractWordNums(s: String): Long {
        val first = allNumbers
            .map { (long, short) -> s.indexOf(long) to short }
            .filter { it.first >= 0 }.minByOrNull { it.first }!!.second
        val last = allNumbers.map { (long, short) -> s.lastIndexOf(long) to short }
            .filter { it.first >= 0 }.maxByOrNull { it.first }!!.second


        return (first + "" + last).toLong()
    }

    fun part2(input: List<String>): Long {
        return input.map {
            extractWordNums(it).also { println(it) }
        }.sum()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 142L)
    val test2 = part2(test2Input)
    println(test2)
    check(test2 == 281L)

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 1)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
