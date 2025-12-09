package aoc2025

import aoc2024.covers
import aoc2024.intersects
import aoc2024.size
import aoc2024.toBlocksOfLines
import utils.InputUtils
import kotlin.time.measureTime

fun Sequence<LongRange>.simplify(): Sequence<LongRange> {
    val byStart = this.sortedBy { it.first }

    val i = byStart.iterator()
    var current = i.next()
    return sequence {
        while (i.hasNext()) {
            val next = i.next()
            if (current covers next) {
                continue
            }
            if (current intersects next) {
                current = current.first..next.last
            } else {
                yield (current)
                current = next
            }
        }
        yield (current)
    }
}

fun main() {
        val testInput = """
3-5
10-14
16-20
12-18

1
5
8
11
17
32""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val (ranges,ingredients) = input.toBlocksOfLines().toList()

        val fresh = ranges.map { it.toLongRange() }
        return ingredients.map { it.toLong() }.count { i -> fresh.any { r -> r.contains(i) } }.toLong()

    }


    fun part2(input: List<String>): Long {
        val fresh = input.toBlocksOfLines().first().map { it.toLongRange() }

        return fresh.asSequence().simplify().sumOf { it.size()}
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 3L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 5)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
