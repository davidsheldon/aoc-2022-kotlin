package aoc2023

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils
import utils.boundingBox

fun Coordinates.distanceTo(other: Coordinates) = (other - this).length()

fun <T> allPairs(items: List<T>): Sequence<Pair<T,T>> = items.asSequence().flatMapIndexed { a, first ->
    (a..<items.size).mapNotNull { b -> items[b].let { first to it } }
}

fun main() {
    val testInput = """...#......
.......#..
#.........
..........
......#...
.#........
.........#
..........
.......#..
#...#.....""".trimIndent().split("\n")

    fun expand(galaxies: List<Coordinates>, scale: Int = 2): List<Coordinates> {
        val bounds = galaxies.boundingBox()
        val emptyColumns = (bounds.first.x..bounds.second.x)
            .filter { x -> galaxies.none { it.x == x } }
        val emptyRows = (bounds.first.y..bounds.second.y)
            .filter { y -> galaxies.none { it.y == y } }

        val expanded = galaxies.map { coord ->
            val newX = coord.x + (emptyColumns.count { it < coord.x } * (scale - 1))
            val newY = coord.y + (emptyRows.count { it < coord.y } * (scale - 1))
            Coordinates(newX, newY)
        }
        return expanded
    }

    fun part1(input: List<String>): Int {
        val universe = ArrayAsSurface(input)
        val galaxies = universe.indexed()
            .filter { (_, char) -> char != '.' }
            .map { it.first }.toList()
        val expanded = expand(galaxies)
        println((galaxies.size * (galaxies.size - 1)) / 2)
        println(expanded)
        println(expanded[4].distanceTo(expanded[8]))
        println(expanded[0].distanceTo(expanded[6]))
        println(expanded[2].distanceTo(expanded[5]))
        return allPairs(expanded).sumOf { it.first.distanceTo(it.second) }
    }

    fun part2(input: List<String>): Long {
        val universe = ArrayAsSurface(input)
        val galaxies = universe.indexed()
            .filter { (_, char) -> char != '.' }
            .map { it.first }.toList()
        val expanded = expand(galaxies, 1_000_000)
        return allPairs(expanded).sumOf { it.first.distanceTo(it.second).toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 374)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 11)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
