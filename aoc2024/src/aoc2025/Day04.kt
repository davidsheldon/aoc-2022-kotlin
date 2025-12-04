package aoc2025

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils
import kotlin.time.measureTime

fun main() {
        val testInput = """
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.""".trimIndent().split("\n")


    fun canMove(coord: Coordinates, map: ArrayAsSurface): Boolean =
        coord.adjacentIncludeDiagonal().count { map.checkedAt(it) == '@' } < 4

    fun part1(input: List<String>): Long {
        val map = ArrayAsSurface(input)
        return map.findAll { it == '@' }
            .filter { coord -> canMove(coord, map) }.count().toLong()

    }


    fun part2(input: List<String>): Long {
        val map = ArrayAsSurface(input)
        val rolls = map.findAll { it == '@' } .toMutableSet()
        var countRemoved = 0L
        do {
            val toRemove = rolls.filter { c -> c.adjacentIncludeDiagonal().count { rolls.contains(it)} < 4 }

            //println("Removing ${toRemove.size}")
            countRemoved += toRemove.size
            rolls.removeAll(toRemove)
        } while (toRemove.isNotEmpty())
        return countRemoved
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 13L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 4)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
