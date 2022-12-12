package aoc2021


import aoc2022.applyN
import aoc2022.utils.InputUtils

// 8 + 6 + 6 + 6 + 6

fun simulate(i: List<Int>): List<Int> = i.flatMap {
    when (it) {
        0 -> listOf(6, 8)
        else -> listOf(it - 1)
    }
}

fun fishAfterImpl(days: Int): Long =
    when {
        days < 0 -> 1
        else -> fishAfter(days - 7) + fishAfter(days - 9)
    }

val map = HashMap<Int, Long>()
fun fishAfter(days: Int): Long = when {
    days < 0 -> 1
    else -> map.computeIfAbsent(days, ::fishAfterImpl)
}

fun fishAfter(counter: Int, days: Int): Long {
    return fishAfter(days - 1 - counter)
}

fun main() {
    val testInput = """3,4,3,1,2""".split("\n")


    fun part1(input: List<String>): Int {
        val states = input[0].split(',').map { it.toInt() }
        return applyN(states, 80, ::simulate).size
    }


    fun part2(input: List<String>): Long {
        // Populate cache
        for (x in 0..260) {
            print("$x ")
            println(fishAfter(x))
        }

        val states = input[0].split(',').map { it.toInt() }
        return states.groupingBy { it }.eachCount().map { (k, v) -> fishAfter(k, 256) * v }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 5934)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 6)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
