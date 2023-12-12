package aoc2023

import utils.InputUtils

val dots = "\\.+".toRegex()
fun String.replaceAt(index: Int, c: Char): String {
    val sb = StringBuilder(this)
    sb.setCharAt(index, c)
    return sb.toString()
}

fun main() {
    val testInput = """???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1""".trimIndent().split("\n")

    fun matches(arrangement: String, groups: List<Int>): Boolean {
        val actual = arrangement.split(dots).map { it.length }.filter { it != 0 }
        return actual == groups

    }
    fun matches(arrangement: String, pattern: String) {
        arrangement.zip(pattern).all { (a, p) ->
            a == p || p == '?'
        }
    }


    fun fix(pattern: String, groups: List<Int>): List<String> {
        val firstQ = pattern.indexOf('?')
        if (firstQ == -1) {
            if (matches(pattern, groups))
            return listOf(pattern.toString())
            else return listOf()
        }
        return fix(pattern.replaceAt(firstQ, '.'), groups) +
                fix(pattern.replaceAt(firstQ, '#'), groups)
    }

    //fun fix(pattern: String, groups: List<Int>) = fix(StringBuilder(pattern), groups)


    fun part1(input: List<String>): Int {
        return input.map {
            val (pattern, groupString) = it.split(" ")
            val groups = listOfNumbers(groupString)
            pattern to groups
        }
            .sumOf {
                val size = fix(it.first, it.second).size
                size
            }

    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 21)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 12)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
