package aoc2023

import utils.InputUtils

fun main() {
    val testInput = """0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45""".trimIndent().split("\n")

    fun Iterable<Int>.differences() = zip(drop(1)).map { it.second - it.first }
    fun Iterable<Int>.differencesTable() =
        generateSequence(this) { it.differences()}
           .takeWhile { row -> row.any { it != 0 } }

    fun predictNext(firstRow: Iterable<Int>): Int {
        return firstRow.differencesTable()
            //.onEach { println(it) }
            .map { it.last() }.sum()
    }
    fun predictPrevious(firstRow: Iterable<Int>): Int {
        return firstRow.differencesTable()
            .map { it.first() }
            .toList()
            .reduceRight { i, acc -> i-acc }
    }

    fun part1(input: List<String>): Int {
        return input.map { it.listOfNumbers() }
            .sumOf { predictNext(it) }

    }


    fun part2(input: List<String>): Int {
        return input.map { it.listOfNumbers() }
            .sumOf { predictPrevious(it) }

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 114)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 9)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
