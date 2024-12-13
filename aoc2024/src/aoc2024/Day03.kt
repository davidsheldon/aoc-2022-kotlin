package aoc2024

import utils.InputUtils

val mulParser = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
val doDont = """(do|don't)\(\)""".toRegex()
val allParser = "($doDont|$mulParser)".toRegex()

fun calcMul(mul: String) = calcMul(mulParser.matchEntire(mul) ?: throw IllegalArgumentException("Unable to parse $mul"))

fun calcMul(matcher: MatchResult): Long {
    val (a, b) = matcher.destructured
    return a.toLong() * b.toLong()
}

fun main() {

    val testInput = """xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))""".trimIndent().split("\n")
    val testInput2 = """xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        return input.sumOf { mulParser.findAll(it)
            .sumOf { calcMul(it) }
        }
    }


    fun part2(input: List<String>): Long {
        return allParser.findAll(input.joinToString("\n"))
            .fold(true to 0L) { (enable, total), matchResult ->
                when(matchResult.value) {
                    "don't()" -> false to total
                    "do()" -> true to total
                    else -> enable to total + (if (enable) calcMul(matchResult.value) else 0)
                }
            }.second
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 161L)
    println(part2(testInput2))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
