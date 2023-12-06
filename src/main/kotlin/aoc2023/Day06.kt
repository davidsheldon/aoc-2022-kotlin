package aoc2023

import utils.InputUtils
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val testInput = """Time:      7  15   30
Distance:  9  40  200""".trimIndent().split("\n")

    fun distances(racetime: Int): Sequence<Int> = (1 until racetime)
        .asSequence()
        .map { it * (racetime - it) }

    // Solve a*x^2 + b*x + c = 0
    fun solveQuadratic(a:Double, b: Double,c: Double): Pair<Double, Double> {
        val discriminant = b * b - 4 * a * c
        // Assume there are 2 roots so we don't have to worry about complex numbers
        val root1 = (-b + sqrt(discriminant)) / (2 * a)
        val root2 = (-b - sqrt(discriminant)) / (2 * a)
        return Pair(root1, root2)
    }

    fun distancesOver(raceTime: Long, target: Long): Long {
        // x (t-x) = y
        // xt-x^2-y = 0
        val roots = solveQuadratic(-1.0, raceTime.toDouble(), (-target).toDouble())

        val lower = ceil(roots.first).toLong()
        val upper = floor(roots.second).toLong()

        return 1 + (upper - lower)
    }

    fun part1(input: List<String>): Int {
        val (times, distances) = input.map { it.substringAfter(":").listOfNumbers() }
        val races = times.zip(distances)

        return races.map { (time, distance) -> distances(time).count { it > distance } }.product()
    }


    fun part2(input: List<String>): Long {
        val (time, distance) = input.map {
            it.substringAfter(":").replace(" ", "").toLong() }

        return distancesOver(time, distance)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 288)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 6)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
