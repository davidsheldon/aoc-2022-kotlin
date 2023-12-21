package aoc2023

import aoc2022.utils.MazeToTarget
import utils.*

private data class Pos(val loc: Coordinates, val steps: Int)
private class Garden(input: List<String>): ArrayAsSurface(input) {
    fun startCoord(): Coordinates = indexed().firstOrNull { it.second == 'S' }?.first ?: throw IllegalStateException("Cant find start")

    fun doubleMovesFrom(loc: Coordinates): List<Coordinates> =
        movesFrom(loc)
            .flatMap { it.adjacent() }
            .filter { it.isGarden() }

    fun movesFrom(loc: Coordinates): List<Coordinates> =
        loc.adjacent().filter { it.isGarden() }
            .toList()

    private fun Coordinates.isGarden() = checkedAt(this, '#') != '#'

}

private class InfiniteGarden(input: List<String>): ArrayAsSurface(input) {
    fun startCoord(): Coordinates = indexed().firstOrNull { it.second == 'S' }?.first ?: throw IllegalStateException("Cant find start")

    fun movesFrom(loc: Coordinates): List<Coordinates> =
        loc.adjacent().filter { it.isGarden() }
            .flatMap { it.adjacent() }
            .filter { it.isGarden() }.toList()
    fun singleMovesFrom(loc: Coordinates): List<Coordinates> =
        loc.adjacent().filter { it.isFirstGarden() }.toList()

    private fun Coordinates.isGarden() = at(this.wrapped()) != '#'
    private fun Coordinates.isFirstGarden() = checkedAt(this, '#') != '#'
    private fun Coordinates.wrapped() = Coordinates(posRem(this.x,  getWidth()), posRem(this.y,  getHeight()))
    private fun posRem(x: Int, q: Int) = ((x%q)+q)%q

}


fun main() {
    val testInput = """...........
.....###.#.
.###.##..#.
..#.#...#..
....#.#....
.##..S####.
.##..#...#.
.......##..
.##.#.####.
.##..##.##.
...........""".trimIndent().split("\n")



    fun part1(input: List<String>, steps: Int=64): Int {
        val g = Garden(input)

        val start = g.startCoord()

//        val maze2 = MazeToTarget(start, { g.doubleMovesFrom(it) }, (steps / 2)-1)
//        println(maze2.distances.size)
        val maze = MazeToTarget(start, { g.movesFrom(it) }, steps)
        return maze.distances.values.count { it <= steps && it % 2 == steps % 2 }
    }


    fun part2(input: List<String>, steps: Int=500): Int {
        val g = InfiniteGarden(input)

        val start = g.startCoord()

        val maze = MazeToTarget(start, { g.movesFrom(it) }, (steps / 2)-1)

        println(listOf(N,S, E,W).map { start.move(it, 2* if (it in listOf(E,W)) g.getWidth() else g.getHeight()) }
            .map { maze.distances[it] })

        val g2 = Garden(input)
        val singleMaze = MazeToTarget(start) { g2.movesFrom(it) }
        fun getLine(x1: Int, y1:Int, x2: Int,y2:Int):List<Int> {
            return Coordinates(x1,y1).lineTo(Coordinates(x2, y2)).map { singleMaze.distances[it] ?: -1 }.toList()
        }
        // Edges
        println(getLine(0,0,g.getWidth()-1,0))
        println(getLine(0,0,0,g.getHeight()-1))
        println(getLine(0,g.getHeight()-1,g.getWidth()-1,g.getHeight()-1))
        println(getLine(g.getWidth()-1,0,g.getWidth()-1,g.getHeight()-1))


        return maze.distances.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput,6)
    println(testValue)
    check(testValue == 16)

    println(part2(testInput, 500))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 21)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
