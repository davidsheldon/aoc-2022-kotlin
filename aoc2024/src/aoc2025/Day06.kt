package aoc2025

import aoc2024.product
import utils.InputUtils
import kotlin.time.measureTime

fun main() {
        val testInput = """
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  """.trimIndent().split("\n")

    fun split(input: List<String>): List<List<String>> {
        val emptyColumns = listOf(-1) + input.first().indices.filter { i ->
            input.all { it[i] == ' '}
        } + listOf(input.first().length)
        return emptyColumns.zipWithNext().map { (s,e) ->
            input.map { it.substring(s+1,e) }
        }
    }

    fun doOctoMath(input: List<String>, numCreator: (List<String>) -> List<Long>): Long {
        val sums = split(input)

        return sums.sumOf { sum ->

            val operator = sum.last()
            val numbers = numCreator(sum.dropLast(1))

            when (operator.trim()) {
                "*" -> numbers.product()
                "+" -> numbers.sum()
                else -> error("Unknown operator $operator")
            }
        }
    }


    fun part1(input: List<String>): Long {
        return doOctoMath(input) {
            it.map { it.trim().toLong() }
        }
    }

    fun List<String>.rotate(): List<String> = first().indices.map { i-> this.map {it[i]}.toCharArray().concatToString() }

    fun part2(input: List<String>): Long {
        return doOctoMath(input) {
            it.rotate().map { it.trim().toLong() }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 4277556L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 6)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
