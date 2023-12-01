package aoc2021


import utils.InputUtils
import kotlin.math.absoluteValue

fun main() {
    val testInput = """16,1,2,0,4,2,7,1,2,14""".split("\n")

    fun part1(input: List<String>): Int {
        val items = input[0].split(",").map { it.toInt() }

        return (items.min()..items.max()).minOf { pivot -> items.sumOf { (it - pivot).absoluteValue } }
    }

    fun triangle(i: Int) = (i * (i + 1)) / 2

    fun part2(input: List<String>): Int {
        val items = input[0].split(",").map { it.toInt() }

        return (items.min()..items.max()).minOf { pivot -> items.sumOf { triangle((it - pivot).absoluteValue) } }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 37)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 7)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
