package aoc2022

import aoc2022.utils.bfs
import utils.Cube
import utils.InputUtils
import utils.toPoint3d


fun main() {
    val testInput = """2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5""".split("\n")

    fun part1(input: List<String>): Int {
        val points = input.map { it.toPoint3d() }.toSet()
        return points.sumOf { point -> point.connected().filter { it !in points }.count() }
    }

    fun part2(input: List<String>): Int {
        val points = input.map { it.toPoint3d() }.toSet()

        val space = points.fold(Cube(points.first(), points.first()), Cube::expand).padded(1)

        val outside = space.min
        val air = bfs(outside) { point -> point.connected().filter { it in space && it !in points } }.toSet()
        return points.sumOf { point -> point.connected().filter { it in air }.count() }
    }

    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 64)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 18).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

