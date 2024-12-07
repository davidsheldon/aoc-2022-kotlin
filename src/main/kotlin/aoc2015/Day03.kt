package aoc2015

import utils.CompassPoint
import utils.Coordinates
import utils.InputUtils


fun main() {

    fun String.applyMoves() = map { c -> when (c) {
        '^' -> CompassPoint.N
        'v' -> CompassPoint.S
        '<' -> CompassPoint.W
        '>' -> CompassPoint.E
        else -> error("Invalid input '$c'")
    }}
        .runningFold(Coordinates(0,0)) { c, d -> c.move(d) }


    fun part1(input: List<String>): Long {
        val locations = input.joinToString("").applyMoves()

        return locations.distinct().size.toLong()
    }


    fun part2(input: List<String>): Long {
        val allMoves = input.joinToString("")
        val even = allMoves.filterIndexed { index, _ -> index % 2 == 0 }
        val odd = allMoves.filterIndexed { index, _ -> index % 2 != 0 }

        return (even.applyMoves() + odd.applyMoves()).distinct().size.toLong()

        return part1(input)
    }

    // test if implementation meets criteria from the description, like:
    println(" 2 = " + part1(listOf(">")))
    println(" 4 = " + part1(listOf("^>v<")))
    println(" 2 = " + part1(listOf("^v^v^v^v^v")))

    println(" 3 = " + part2(listOf("^v")))
    println(" 3 = " + part2(listOf("^>v<")))
    println(" 11 = " + part2(listOf("^v^v^v^v^v")))

    val puzzleInput = InputUtils.downloadAndGetLines(2015, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
