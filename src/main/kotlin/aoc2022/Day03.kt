package aoc2022

import utils.InputUtils

fun main() {
    val testInput = """vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw""".split("\n")


    fun inHalf(s: String) = (s.length / 2).let { split -> s.substring(0, split) to s.substring(split) }

    fun Char.score() = when (this) {
        in 'a'..'z' -> 1 + (this - 'a')
        in 'A'..'Z' -> 27 + (this - 'A')
        else -> -1

    }

    fun <T> Iterable<Set<T>>.intersectAll() = reduce {a, b -> a.intersect(b) }
    fun Iterable<String>.scoreSingleCommonCharacter() = map(String::toSet).intersectAll().first().score()

    fun part1(input: List<String>): Int = input.sumOf {
        inHalf(it).toList().scoreSingleCommonCharacter()
    }

    fun part2(input: List<String>) = input.chunked(3).sumOf(List<String>::scoreSingleCommonCharacter)


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 157)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
