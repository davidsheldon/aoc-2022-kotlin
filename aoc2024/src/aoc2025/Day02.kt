package aoc2025

import utils.InputUtils
import java.util.function.Predicate
import kotlin.time.measureTime

fun main() {
    val testInput = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"

    fun isValid(id: String): Boolean {
        if (id.length % 2 == 1) return true
        val half = id.length  / 2
        return id.take(half) != id.drop(half)
    }


    fun runIt(input: String, filter: Predicate<String>): Long {
        val inputs: List<String> = input.split(",").flatMap {
            val r = it.split("-").map(String::toLong)
            val range: LongRange = r[0]..r[1]

            range.map { it.toString() }
        }
        return inputs.filter { filter.test(it) }.sumOf { it.toLong() }
    }

    val cache = mutableMapOf<Int, List<Int>>()

    fun isValid2(id: String): Boolean {
        val factors = cache.computeIfAbsent(id.length) { n ->
            (1 .. n/2).filter { n % it == 0}
        }

        return factors.any { n ->
            val a = id.chunked(n)
            a.all { it == a[0] }
        }
    }


    fun part1(input: String): Long {
        return runIt(input) { !isValid(it) }
    }




    fun part2(input: String): Long {
        return runIt(input) { !isValid2(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 1227775554L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 2)
    val input = puzzleInput.toList()


    println(measureTime { println(part1(input[0])) })
    println(measureTime { println(part2(input[0])) })
}
