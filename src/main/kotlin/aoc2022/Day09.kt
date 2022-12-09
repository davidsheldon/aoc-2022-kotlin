package aoc2022

import aoc2022.utils.InputUtils
import utils.Coordinates

class Day09(val size: Int) {
    var positions = Array(size) { Coordinates(0,0) }
    val visited = mutableSetOf(tail)
    private var head: Coordinates
        get() = positions.first()
        set(c) { positions[0] = c }
    private val tail: Coordinates
        get() = positions.last()

    fun moveHead(dir: Char) {
        when(dir) {
            'U' -> head = head.dY(1)
            'D' -> head = head.dY(-1)
            'L' -> head = head.dX(-1)
            'R' -> head = head.dX(1)
        }
        updateTail()
        visited.add(tail)
    }

    private fun updateTail() {
        repeat(size - 1) { updatePair(it) }
    }

    private fun Coordinates.follow(other: Coordinates): Coordinates {
        if (isTouching(other)) return this
        return this + (other - this).sign()
    }

    private fun updatePair(idx: Int) {
        positions[idx + 1] = positions[idx + 1].follow(positions[idx])
    }


}

fun main() {
    val testInput = """R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2""".split("\n")

    fun simulateSnake(input: List<String>, n: Int): Int {

        return input.fold(Day09(n)) { state, command ->
            val (dir, dist) = command.split(" ")

            repeat(dist.toInt()) {
                state.moveHead(dir[0])
            }

            state
        }.visited.size
    }
    fun part1(input: List<String>): Int = simulateSnake(input, 2)


    fun part2(input: List<String>): Int = simulateSnake(input, 10)

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 13)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 9).toList()


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
