package aoc2022

import aoc2022.utils.InputUtils


fun main() {
    val testInput = """nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"""

    fun <T> List<T>.allDifferent() = size == this.toSet().size

    fun String.windowsOfDistinctChars(len: Int) = asSequence()
        .windowed(len)
        .mapIndexed { index, chars -> IndexedValue(index + len, chars) }
        .filter { (_, chars) -> chars.allDifferent() }


    fun part1(input: String): Int =
        input.windowsOfDistinctChars(4).first().index


    fun part2(input: String): Int =
        input.windowsOfDistinctChars(14).first().index

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 10)

    val puzzleInput = InputUtils.downloadAndGetString(2022, 6)


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
