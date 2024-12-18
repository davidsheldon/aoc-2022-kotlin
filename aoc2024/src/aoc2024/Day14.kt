package aoc2024

import utils.Coordinates
import utils.InputUtils

private data class Robot(val pos: Coordinates, val vel: Coordinates) {

    fun posAfter(n: Int, w: Int, h: Int) =
         Coordinates(mod(pos.x + (n*vel.x.toLong()), w), mod(pos.y + (n*vel.y.toLong()), h))

    fun mod(x: Long, w: Int): Int = (((x % w) + w) % w).toInt()

}

private val lineParser = "p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)".toRegex()

fun main() {

    val testInput = """p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3""".trimIndent().split("\n")



    fun part1(input: List<String>, w: Int = 101, h: Int = 103): Long {
        var robots = input.parsedBy(lineParser) {
            val (px,py, vx, vy) = it.destructured
            Robot(Coordinates(px.toInt(), py.toInt()), Coordinates(vx.toInt(), vy.toInt()))
        }.toList()

        val middle = Coordinates(w/2, h/2)

        val counts = robots
            .map { it.posAfter(100, w, h) }
            .map { (it - middle).sign() }
            .filter { it.x != 0 && it.y != 0 }
            .groupingBy { it }
            .eachCount()
        println(counts)

        return counts.values.product().toLong()
    }


    fun part2(input: List<String>, w: Int = 101, h: Int = 103): Long {
        var robots = input.parsedBy(lineParser) {
            val (px,py, vx, vy) = it.destructured
            Robot(Coordinates(px.toInt(), py.toInt()), Coordinates(vx.toInt(), vy.toInt()))
        }.toList()
        val middle = Coordinates(w/2, h/2)

        return IntRange(1,w*h).minBy { n ->
            val counts = robots
                .map { it.posAfter(n, w, h) }
                .map { (it - middle).sign() }
                .filter { it.x != 0 && it.y != 0 }
                .groupingBy { it }
                .eachCount()

            counts.values.product().toLong()

        }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput, 11, 7)
    println(testValue)
    check(testValue == 12L)
    //println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 14)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
