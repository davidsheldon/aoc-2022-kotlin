package aoc2024

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils
import utils.mazes.MazeToTarget


fun main() {

    val testInput = """5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0""".trimIndent().split("\n")



    fun part1(input: List<String>, gridSize: Int = 71, initial: Int = 1024): Long {
        val obs = input.take(initial).map {
            val (x,y) = it.split(",").map(String::toInt)
            Coordinates(x, y)
        }
        val grid = ArrayAsSurface(List(gridSize) { ".".repeat(gridSize)})
        val map = obs.fold(grid) { g, c -> g.replace(c, '#')}
        println(map)

        val maze = MazeToTarget(Coordinates(gridSize-1, gridSize-1)) {
            c -> c.adjacent().filter { map.checkedAt(it) == '.' }.toList()
        }

        println(maze.routeFrom(Coordinates(0,0)).fold(map) { m,c -> m.replace(c, 'O')})

        return maze.distanceFrom(Coordinates(0,0)).toLong()-1
    }

    fun canSolve(rain: List<Coordinates>, initial: Int, gridSize: Int = 71): Boolean {
        val grid = ArrayAsSurface(List(gridSize) { ".".repeat(gridSize)})
        val map = rain.take(initial).fold(grid) { g, c -> g.replace(c, '#')}

        val maze = MazeToTarget(Coordinates(gridSize-1, gridSize-1)) {
                c -> c.adjacent().filter { map.checkedAt(it) == '.' }.toList()
        }
        return maze.canReach(Coordinates(0,0))
    }

    fun part2(input: List<String>, gridSize: Int=71): String {
        val obs = input.map {
            val (x,y) = it.split(",").map(String::toInt)
            Coordinates(x, y)
        }

        var low = 0
        var high = obs.size-1

        while (low <= high) {
            val mid = (low + high).ushr(1) // safe from overflows

            val canSolve = canSolve(obs, mid, gridSize)
            println(" $mid -> $canSolve  ${obs.drop(mid).firstOrNull()}")

            if(canSolve) {
                low = mid + 1
            }
            else
                high = mid - 1
        }

        return obs[high].toString()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput, 7, 12)
    println(testValue)
    check(testValue == 22L)
    println(part2(testInput,7))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 18)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
