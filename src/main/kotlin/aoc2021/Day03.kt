package aoc2021

import aoc2022.utils.InputUtils


fun main() {
    val testInput = """00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010""".split("\n")

    fun <K> Map<K, Int>.topKeyByValues() = this.keys.sortedByDescending { this[it] }.first()

    fun <T> Iterable<T>.mostFrequent() = groupingBy { it }.eachCount().topKeyByValues()

    fun swapBits(gamma: String) = gamma.map {
        when (it) {
            '0' -> '1'
            '1' -> '0'
            else -> it
        }
    }.joinToString("")

    fun part1(input: List<String>): Int {
        val gamma = input.asSequence()
            .flatMap { digits ->
                digits.mapIndexed { index, c -> IndexedValue(index, c) }
            }
            .groupBy({ it.index }, { it.value })
            .map { it.key to it.value.mostFrequent() }
            .sortedBy { it.first }
            .map { it.second }.joinToString("")
        val epsilon = swapBits(gamma)
        println(gamma)
        println(epsilon)
        return gamma.toInt(2) * epsilon.toInt(2)
    }

    fun filtered(digit: Int, boolean: Boolean, items: List<String>) {
        items.map { it[digit] }.mostFrequent()
    }

    fun part2(input: List<String>): Int = part1(input) // TODO

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 198)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
