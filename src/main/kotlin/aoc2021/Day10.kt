package aoc2021


import utils.InputUtils
import java.util.*


fun main() {
    val testInput = """[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]""".split("\n")

    val opens = "([{<"
    val closes = ")]}>"
    fun score(c: Char) = when(c) {
        ')' -> 3
        ']' -> 57
        '}' -> 1197
        '>' -> 25137
        else -> 0
    }

    fun validate(s: String): Int {
        val stack = Stack<Char>()
        for(c in s) {
            if(c in opens) { stack.push(c) }
            if(c in closes) {
                val o = stack.pop()
                val expected = closes[opens.indexOf(o)]
                if (c != expected) {
                    //println("Was $c expected $expected")
                    return score(c)
                }
            }
        }
        return 0
    }

    fun tail(s: String): String {
        val stack = Stack<Char>()
        for(c in s) {
            if(c in opens) { stack.push(c) }
            if(c in closes) {
                val o = stack.pop()
                val expected = closes[opens.indexOf(o)]
                if (c != expected) {
                    return ""
                }
            }
        }
        return stack.joinToString("").reversed()
    }


    fun part1(input: List<String>): Int {
        return input.sumOf(::validate)
    }

    fun part2Score(s: String): Long {
        return s.fold(0L ) { acc, c -> (acc * 5) + (opens.indexOf(c) + 1)}
    }


    fun part2(input: List<String>): Long {
        val scores = input
            .asSequence()
            .filter { validate(it) ==0 }
            .map(::tail)
            .filter(String::isNotBlank)
            .onEach { println("$it ${part2Score(it)}") }
            .map { part2Score(it)}.sorted().toList()
        return scores.median()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 26397)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 10)
    val input = puzzleInput.toList()

    println(part1(input))
    println("=== Part 2 ===")
    println(part2(testInput))
    println(part2(input))
}

private fun  List<Long>.median(): Long =
        this[size / 2]



