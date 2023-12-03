package aoc2021


import aoc2022.product
import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils

class Surface(val input: List<String>): ArrayAsSurface(input) {
    fun basinAt(start: Coordinates): Sequence<Coordinates> {
        val queue = ArrayDeque<Coordinates>()
        queue.add(start)

        val seen = mutableSetOf<Coordinates>()

        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()
            queue.addAll(pos.adjacent().filter { c -> c !in seen && inBounds(c) && at(c) != '9' })
            seen.add(pos)
        }
        return seen.asSequence()

    }

}

fun main() {
    val testInput = """2199943210
3987894921
9856789892
8767896789
9899965678""".split("\n")


    fun Surface.lowPoints() = allPoints().filter { p ->
        val c = at(p)
        p.adjacent().filter(this::inBounds).all { at(it) > c }
    }

    fun part1(input: List<String>): Int {
        val surface = Surface(input)
        return surface.lowPoints().sumOf { surface.at(it).digitToInt() + 1 }
    }

    fun part2(input: List<String>): Int {
        val surface = Surface(input)
        return surface.lowPoints().map {
            surface.basinAt(it).count()
        }.sortedDescending().take(3).product()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 15)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 9)
    val input = puzzleInput.toList()

    println(part1(input))
    println("=== Part 2 ===")
    println(part2(testInput))
    println(part2(input))
}


