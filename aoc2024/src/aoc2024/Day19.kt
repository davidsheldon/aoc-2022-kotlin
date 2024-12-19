package aoc2024

import utils.InputUtils


fun possibleImpl(pattern: String, towels: Collection<String>, cache: MutableMap<String, Boolean>): Boolean
        =
    if (pattern.isEmpty()) true
    else towels.any { pattern.startsWith(it) && possible(pattern.drop(it.length), towels,cache) }


fun possible(pattern: String, towels: Collection<String>, cache: MutableMap<String, Boolean>): Boolean {
    return cache.getOrPut(pattern) { possibleImpl(pattern, towels, cache) }
}

fun waysImpl(pattern: String, towels: Collection<String>, cache: MutableMap<String, Long>): Long
        =
    if (pattern.isEmpty()) 1
    else towels.sumOf { if (pattern.startsWith(it)) ways(pattern.drop(it.length), towels, cache) else 0 }


fun ways(pattern: String, towels: Collection<String>, cache: MutableMap<String, Long>): Long {
    return cache.getOrPut(pattern) { waysImpl(pattern, towels, cache) }
}

fun main() {

    val testInput = """r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb""".trimIndent().split("\n")





    fun part1(input: List<String>): Long {
        val (p1,patterns) = input.toBlocksOfLines().toList()
        val towels = p1[0].split(", ")

        return patterns.count { possible(it, towels, mutableMapOf()) }.toLong()
    }


    fun part2(input: List<String>): Long {
        val (p1,patterns) = input.toBlocksOfLines().toList()
        val towels = p1[0].split(", ")

        return patterns.sumOf { ways(it, towels, mutableMapOf()) }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 6L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 19)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
