package aoc2025

import utils.ArrayAsSurface
import utils.InputUtils
import kotlin.time.measureTime

fun main() {
        val testInput = """
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............""".trimIndent().split("\n")


    fun part1(input: List<String>): Long {
        val manifold = ArrayAsSurface(input)
        val start = manifold.find('S')

        var beams = setOf(start.x)
        var splits = 0L
        ((start.y+1)..<manifold.getHeight())
            .map { input[it] }
            .forEach { row ->
                val splitters = beams.filter { row[it] == '^'}.toSet()
                splits += splitters.size
                beams = (beams - splitters) +
                        (splitters.map { it - 1}) +
                        (splitters.map { it + 1})

            }


        return splits
    }


    fun part2(input: List<String>): Long {
        val manifold = ArrayAsSurface(input)
        val start = manifold.find('S')

        var beams = mapOf(start.x to 1L)
        var routes = 0L
        ((start.y+1)..<manifold.getHeight())
            .map { input[it] }
            .forEach { row ->
                val splitters = beams.filterKeys { row[it] == '^'}
                val newBeams = beams.toMutableMap()
                newBeams -= splitters.keys.toSet()

                for (split in splitters) {
                    newBeams.compute(split.key - 1) { _, v -> (v?:0) + split.value }
                    newBeams.compute(split.key + 1) { _, v -> (v?:0) + split.value }
                }
                beams = newBeams
            }


        return beams.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 21L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 7)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
