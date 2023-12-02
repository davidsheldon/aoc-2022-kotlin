package aoc2015

import utils.InputUtils

fun main() {



    fun part1(input: String): Int {
        return input.count { it == '(' } - input.count { it == ')'}
    }

    fun part2(input: String): Int {
        val stops = input.asSequence().runningFold(0) { acc, c -> when(c) {
            '(' -> acc + 1
            ')' -> acc - 1
            else -> acc
        } }.toList()
        println(stops.take(10))

        return stops.indexOfFirst { it == -1 }
    }

    // test if implementation meets criteria from the description, like:
    val input = InputUtils.downloadAndGetString(2015, 1)

    println(part1(input))
    println(part2(input))
}
