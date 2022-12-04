package aoc2022

import aoc2022.utils.InputUtils

fun main() {
    val testInput = """2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8""".split("\n")


    fun String.toIntRange(): IntRange {
        val (start, end) = split('-')
        return start.toInt()..end.toInt()
    }

    infix fun IntRange.covers(second: IntRange) = first <= second.first && last >= second.last
    infix fun IntRange.intersects(second: IntRange) =
        second.first in this || second.last in this || this.first in second

    fun parseToRanges(input: List<String>) = input
        .map { it.split(',').map { it.toIntRange() } }

    fun part1(input: List<String>): Int = parseToRanges(input)
        .count {
            val (first, second) = it
            (first covers second) || (second.covers(first))
        }

    fun part2(input: List<String>) = parseToRanges(input)
        .count {
            val (first, second) = it
            first intersects second
        }
    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 2)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 4)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
