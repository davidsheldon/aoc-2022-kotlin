package aoc2024

import aoc2023.listOfLongs
import utils.InputUtils

private fun part1Solved(target: Long, nums: List<Long>): Boolean {
    if (nums.isEmpty()) return target == 0L
    val last = nums.last()
    val head = nums.dropLast(1)
    if (last == target) return true

    val ret = (target % last == 0L && part1Solved(target / last, head)) ||
            (target >= last && part1Solved(target - last, head))
    //println("${Sum(target,nums)}: $ret")
    return ret
}

private fun List<Long>.mergeLastTwo(): List<Long> =
    dropLast(2) + listOf(takeLast(2).joinToString("").toLong())

private fun part2Solved(target: Long, nums: List<Long>): Boolean {
    if (nums.isEmpty()) return target == 0L
    val last = nums.last()
    val head = nums.dropLast(1)
    if (last == target) return true

    val ret = (target % last == 0L && part2Solved(target / last, head)) ||
            (target >= last && part2Solved(target - last, head)) ||
            part2Merged(head, target, last)
   // println("- ${Sum(target,nums)}: $ret")
    return ret
}

private fun part2Merged(heads: List<Long>, target: Long, last: Long): Boolean {
    if (heads.isEmpty() || !target.toString().endsWith(last.toString())) return false
    val newTarget = target.toString().removeSuffix(last.toString()).toLong()
    return part2Solved(newTarget, heads)
}


private data class Sum(val target: Long, val nums: List<Long>) {
    fun canBeSolvedPart1(): Boolean = part1Solved(target, nums)
    fun canBeSolvedPart2(): Boolean = part2Solved(target, nums)
}

private fun String.parseSum(): Sum =
    parsedBy("(\\d+): (.*)".toRegex()) {
        val (target, w) = it.destructured
        Sum(target.toLong(), w.listOfLongs())
    }



fun main() {

    val testInput = """190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        return input
            .map { it.parseSum()}
            .filter { it.canBeSolvedPart1() }
            .sumOf { it.target }.toLong()
    }


    fun part2(input: List<String>): Long {
        return input
            .map { it.parseSum()}
            .filter { it.canBeSolvedPart2() }
            .sumOf { it.target }.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 3749L)
    println(part2(testInput)) // 11387

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 7)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
