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

    fun <K> Map<K, Int>.topKeyByValues() = if (values.size == values.toSet().size) {
        this.keys.sortedByDescending { this[it] }.first()
    } else {
        null
    }

    fun <T> Iterable<T>.mostFrequent() = groupingBy { it }.eachCount().topKeyByValues()

    fun not(c: Char): Char = ('1' + '0'.code - c).toChar()

    fun swapBits(gamma: String) = gamma.map { not(it) }.joinToString("")

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

    fun filtered(digit: Int, boolean: Boolean, items: List<String>): String {
        val digits = items.map { it[digit] }
        val mostFrequent = digits.mostFrequent() ?: '1'
        val toMatch = if (boolean) mostFrequent else not(mostFrequent)
        val matched = items.filter { it[digit] == toMatch }
        return if (matched.size == 1) matched.first()
        else filtered(digit + 1, boolean, matched)
    }

    fun part2(input: List<String>): Int {
        val oxygenBits = filtered(0, true, input)
        val co2Bits = filtered(0, false, input)

        println(oxygenBits)
        println(co2Bits)
        return oxygenBits.toInt(2) * co2Bits.toInt(2)

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 198)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
