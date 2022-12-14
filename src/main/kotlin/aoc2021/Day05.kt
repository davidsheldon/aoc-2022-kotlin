package aoc2021


import aoc2022.parsedBy
import aoc2022.takeWhilePlusOne
import aoc2022.utils.InputUtils
import kotlin.math.sign

data class Coord(val x: Int, val y: Int) {
    // Unit in the direction of the other one
    fun directionTo(other: Coord): Coord = Coord(other.x - x, other.y - y).normalise()

    fun normalise() = Coord(x = x.sign, y = y.sign)
    operator fun plus(other: Coord) = Coord(x = x + other.x, y = y+other.y)
}
data class Line(val start: Coord, val end: Coord) {
    fun allPoints(): Sequence<Coord> {
        val direction = start.directionTo(end)
        return generateSequence(start) {
            it + direction
        }.takeWhilePlusOne { it != end }
    }
    fun isHorizOrVertical() =  start.x == end.x || start.y == end.y
}

fun main() {
    val testInput = """0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2""".split("\n")



    fun parse(input: List<String>): Sequence<Line> {
        return input.parsedBy("(\\d+),(\\d+) -> (\\d+),(\\d+)".toRegex()) {
            val (x1, y1, x2, y2) = it.destructured.toList().map { it.toInt() }
            Line(Coord(x1, y1), Coord(x2, y2))
        }
    }


    fun part1(input: List<String>): Int {
        val counts = parse(input)
            .filter { it.isHorizOrVertical() }
            .flatMap {
                it.allPoints()
            }.groupingBy { it }.eachCount()
        return counts.filter { (_, count) -> count > 1 }.count()
    }


    fun part2(input: List<String>): Int {
        val counts = parse(input)
            .flatMap {
                it.allPoints()
            }.groupingBy { it }.eachCount()
        return counts.filter { (_, count) -> count > 1 }.count()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 5)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 5)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
