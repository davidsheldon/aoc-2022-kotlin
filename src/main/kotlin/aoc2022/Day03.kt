package aoc2022

import aoc2022.utils.InputUtils

fun main() {
    val testInput = """vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw""".split("\n")


    fun inHalf(s: String) = Pair(s.substring(0, s.length / 2).toSet(), s.substring(s.length / 2).toSet())

    fun Char.score() = when (this) {
        in 'a'..'z' -> 1 + (this - 'a')
        in 'A'..'Z' -> 27 + (this - 'A')
        else -> -1

    }

    fun part1(input: List<String>): Int {
        return input.map { inHalf(it) }.sumOf {
            it.first.intersect(it.second).first().score()
        }
    }

    fun part2(input: List<String>): Int {
        return input.windowed(3,3).sumOf {
            it.map(String::toSet)
                .reduce { a, b -> a.intersect(b)}
                .first()
                .score()
        }
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 157)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
