package aoc2023

import aoc2022.utils.MazeToTarget
import utils.*
import utils.Direction

val pipes = mapOf(
    '|' to setOf(N, S),
    '-' to setOf(E, W),
    'L' to setOf(N, E),
    'J' to setOf(N, W),
    '7' to setOf(S, W),
    'F' to setOf(S, E),
)

private class Maze(input: List<String>): ArrayAsSurface(input) {
    val overrides = mutableMapOf<Coordinates, Char>()
    fun start(): Coordinates = indexed().firstOrNull { it.second == 'S' }?.first ?: throw IllegalStateException("Cant find start")
    fun movesFrom(coord: Coordinates): List<Coordinates> =
        (pipes[at(coord)] ?: setOf()).map { coord.move(it, 1)}

    fun exitsFrom(coord: Coordinates) = coord.adjacent()
    .filter {
        val dir = it.compassTo(coord)
        val pipeExits = pipes[checkedAt(it)] ?: setOf()
        pipeExits.contains(dir)
    }

    fun pipeAt(coord: Coordinates): Char {
        val exits = exitsFrom(coord).map { coord.compassTo(it) }.toSet()
        return pipes.entries.firstOrNull { it.value == exits }?.key ?: '.'
    }

    override fun at(c: Coordinates): Char {
        return overrides.getOrElse(c) { super.at(c) }
    }

    fun override(c: Coordinates, char: Char) { overrides[c] = char }
}
fun main() {
    val testInput = """7-F7-
.FJ|7
SJLL7
|F--J
LJ.LJ""".trimIndent().split("\n")

    val test2Input = """FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L""".trimIndent().split("\n")

    fun part1(input: List<String>): Int {
        val maze = Maze(input)
        val start = maze.start()
        println(start)
        maze.override(start, maze.pipeAt(start))
        println(maze.exitsFrom(start).toList())
        val route = MazeToTarget(start) {
            maze.movesFrom(it).toList()
        }

        return route.distances.values.max()

    }


    fun part2(input: List<String>): Int {
        val maze = Maze(input)
        val start = maze.start()
        println(start)
        println(maze.exitsFrom(start).toList())
        val startPipe = maze.pipeAt(start)
        println("Start: $startPipe")
        maze.override(start, startPipe)
        val route = MazeToTarget(start) {
            maze.movesFrom(it).toList()
        }
        return maze.rows().sumOf { row ->
            var pipeCount = 0
            var state : Direction? = null

            val insides = row.map {
                if (route.distances.contains(it)) {
                    val pipe = pipes[maze.at(it)] ?: setOf()
                    if (pipe.contains(N)) {
                        if (state == null) state = N
                        else if (state == N) { state = null }
                        else if (state == S) { pipeCount++; state = null }
                    }
                    if (pipe.contains(S)) {
                        if (state == null) state = S
                        else if (state == N) { pipeCount++; state = null }
                        else if (state == S) { state = null }
                    }
                    0
                }
                else pipeCount % 2
            }.toList()
//            println(insides)
            //println(row.map { route.distances[it] ?: -1}.toList())
            insides.sum()
        }

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 8)

    println(part2(test2Input))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 10)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
