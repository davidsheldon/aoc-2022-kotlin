package aoc2022

import utils.InputUtils
import utils.Coordinates

class Maze(val data: List<String>) {
    val start = findCoordinate('S')
    val end = findCoordinate('E')

    private fun findCoordinate(c: Char): Coordinates =
        findCoordinates(c).first()

    fun findCoordinates(c: Char): Sequence<Coordinates> =
        sequence { data.forEachIndexed {rowNum, row ->
            row.forEachIndexed { colNum, ch -> if (ch == c) yield(Coordinates(colNum, rowNum)) }
        }}

    fun heightAt(coord: Coordinates): Int {
        val (x,y) = coord
        return when {
            coord == start -> 'a'.code
            coord == end -> 'z'.code
            y < 0 || y >= data.size -> -10
            x < 0 || x >= data[y].length -> -10
            else -> data[y][x].code

        }
    }


}

fun main() {
    val testInput = """Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi""".split("\n")


    fun calculateDistanceToEnd(maze: Maze): HashMap<Coordinates, Int> {
        val distances = HashMap<Coordinates, Int>()
        val queue = ArrayDeque<Coordinates>()
        distances[maze.end] = 0
        queue.add(maze.end)

        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()
            val height = maze.heightAt(pos)
            val distance = distances[pos]!!
            pos.adjacent().filter { it !in distances && maze.heightAt(it) >= height - 1 }
                .forEach {
                    queue.addLast(it)
                    distances[it] = distance + 1
                }
        }
        return distances
    }

    fun routeToEnd(
        maze: Maze,
        coord: Coordinates,
        distances: HashMap<Coordinates, Int>
    ): Sequence<Coordinates> = sequence {
            var pos = coord
            while (pos != maze.end) {
                val height = maze.heightAt(pos)
                pos = pos.adjacent().filter { it in distances && maze.heightAt(it) <= height + 1 }
                    .sortedBy { distances[it] }.first()
                yield(pos)
            }
        }

    fun part1(input: List<String>): Int {
        val maze = Maze(input)
        val distances = calculateDistanceToEnd(maze)
        val seq = routeToEnd(maze, maze.start, distances)

        return seq.count()
    }

    fun part2(input: List<String>): Int {
        val maze = Maze(input)
        val distances = calculateDistanceToEnd(maze)

        return maze.findCoordinates('a')
            .filter { it in distances }
            .minOf {
                routeToEnd(maze, it, distances).count()
            }
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 31)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 12).toList()


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
