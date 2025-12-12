package aoc2025

import aoc2024.parsedBy
import aoc2024.toBlocksOfLines
import utils.InputUtils
import kotlin.time.measureTime

data class Day12(val w: Int, val h: Int, val counts: List<Int>)

fun main() {

    val testInput = """0:
###
##.
##.

1:
###
##.
.##

2:
.##
###
##.

3:
##.
###
##.

4:
###
#..
###

5:
###
.#.
###

4x4: 0 0 0 0 2 0
12x5: 1 0 1 0 2 2
12x5: 1 0 1 0 3 2""".trimIndent().split("\n")

    val countParser = { line: String -> line.split(" ").map { it.toInt() } }
    val lineParser = Regex("""(\d+)x(\d+): (.*)""")

    fun part1(input: List<String>): Long {
        val blocks = input.toBlocksOfLines()
        val presents = blocks.takeWhile { it.first().endsWith(":") }
            .associate { it.first().dropLast(1).toInt() to it.drop(1) }
        val problems = blocks.last().map { it.parsedBy(lineParser) {
            val (w, h, counts) = it.destructured
            Day12(w.toInt(), h.toInt(), countParser(counts))
        } }
        val presentSizes = presents.mapValues { it.value.sumOf { it.count { c -> c=='#'} } }
        return problems.count { p ->
            val totalSize = p.counts.mapIndexed { i, c -> (presentSizes[i] ?:0) * c }.sum()
            val maxSize = p.counts.sumOf { it * 9 }
            println("Area ${p.w * p.h} Total Size: $totalSize Max Size: $maxSize")
            if (totalSize > p.w * p.h) { return@count false }
            if (maxSize < p.w * p.h) { return@count true }
            return@count true
        }.toLong()

    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    //check(testValue == 3L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 12)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}

