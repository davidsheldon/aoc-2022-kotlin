package aoc2024

import utils.*
import utils.mazes.MazeToTarget


fun main() {

    val testInput = """###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############""".trimIndent().split("\n")



    fun part1(input: List<String>, minSave: Int = 100): Long {
        val map = ArrayAsSurface(input)
        val maze = MazeToTarget(map.find('E')) {
                c -> c.adjacent().filter { map.checkedAt(it) != '#' }.toList()
        }
        println(maze.distances[map.find('S')])

        return maze.routeFrom(map.find('S'))
            .sumOf { scs ->
                val myD = maze.distances[scs] ?: 0
                val savings = listOf(N,E,S,W)
                    .map { maze.distances[scs.move(it,2)] ?: Int.MAX_VALUE}
                    .filter { it <= myD - 2 - minSave }
                    .map { myD -2 - it }
                if (savings.isNotEmpty()) println(" $scs $savings")

                savings.count()
            }
        .toLong()
    }


    fun part2(input: List<String>, minSave: Int = 100): Long {
        val map = ArrayAsSurface(input)
        val maze = MazeToTarget(map.find('E')) {
                c -> c.adjacent().filter { map.checkedAt(it) != '#' }.toList()
        }
        println(maze.distances[map.find('S')])

        return maze.routeFrom(map.find('S'))
            .sumOf { scs ->
                val myD = maze.distances[scs] ?: 0

                val savings = maze.distances.filter {
                    val distance = it.key.distanceTo(scs)
                    distance in 2..20 && it.value <= myD - distance - minSave
                }

                savings.count()
            }
            .toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput, 20)
    println(testValue)
    check(testValue == 5L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 20)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
