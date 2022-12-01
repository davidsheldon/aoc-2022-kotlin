package aoc2022

import aoc2022.utils.InputUtils

fun main() {
    val testInput = """1000
2000
3000

4000

5000
6000

7000
8000
9000

10000""".split("\n")



    fun elfCalories1(input: List<String>) =
        input.asSequence().splitBy(String::isBlank)
            .map { calList -> calList.map(String::toInt).sum() }

    fun Sequence<String>.elfCaloriesInternal() = sequence {
        var currentElf = 0
        forEach {
            if (it.isBlank()) {
                yield(currentElf)
                currentElf = 0
            }
            else {
                currentElf += it.toInt()
            }
        }
    }

    fun elfCalories(input: List<String>) = input.asSequence().elfCaloriesInternal()


    fun part1(input: List<String>): Int {
        return elfCalories(input)
            .max()
    }

    fun part2(input: List<String>): Int {
        return elfCalories(input)
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 24000)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 1)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
