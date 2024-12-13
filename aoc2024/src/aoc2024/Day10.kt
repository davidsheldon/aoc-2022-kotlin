package aoc2024

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils


fun <T> walkTo(
    start: T,
    neighbours: (T) -> Sequence<T>,
    endCondition: (T) -> Boolean,
): Sequence<T> {
    val queue = ArrayDeque<T>()
    queue.add(start)
    return sequence {
        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()
            if (endCondition(pos)) {
                yield(pos)
            }
            else
              queue.addAll(neighbours(pos))
        }
    }
}


private class Topo(val input: List<String>): ArrayAsSurface(input) {
    fun trailheads() = indexed().filter { (_, c) -> c == '0' }.map { it.first }
    fun nextFrom(c: Coordinates): Sequence<Coordinates> {
        val height = at(c)
        if (height == '9')
            return emptySequence<Coordinates>()
        else
            return c.adjacent().filter { coordinates -> checkedAt(coordinates) == height + 1 }
    }
    fun scoreTrailhead(c: Coordinates) = bfs(c) { nextFrom(it) }
        .count { coordinates -> at(coordinates) == '9' }

    fun scoreTrailhead2(c: Coordinates) = walkTo(c, { nextFrom(it) }, { coordinates -> at(coordinates) == '9' }).count()

}

fun main() {

    val testInput = """89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732""".trimIndent().split("\n")

    val t1 = """...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9""".trimIndent().split("\n")
    val t = Topo(t1)
    println(t.scoreTrailhead(Coordinates(3, 0)))


    fun part1(input: List<String>): Long {
        val map = Topo(input)

        return map.trailheads().sumOf {map.scoreTrailhead(it)}.toLong()
    }


    fun part2(input: List<String>): Long {
        val map = Topo(input)

        return map.trailheads().sumOf {map.scoreTrailhead2(it)}.toLong()    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 36L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 10)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
