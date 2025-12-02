package aoc2025

import utils.InputUtils

fun main() {
        val testInput = """
L68
L30
R48
L5
R60
L55
L1
L99
R14
L82""".trimIndent().split("\n")


    fun positions(input: List<String>): List<Int> {
        val seq = input.runningFold(50) { pos, op ->
            val multiple = if (op[0] == 'L') {
                -1
            } else {
                1
            }
            val move = op.substring(1).toInt() * multiple
            (pos + move) % 100
        }
        return seq
    }

    fun part1(input: List<String>): Long {

        val seq = positions(input)

        return seq.count { it == 0}.toLong()
    }


    fun part2(input: List<String>): Long {
        var zeroCount = 0
        val seq = input.fold(50) { pos, op ->
            var count = 0
            val multiple = if (op[0] == 'L') {
                -1
            } else {
                1
            }
            val distance = op.substring(1).toInt()
            val move = distance * multiple

            var newPos = (pos + move)
            if (pos == 0 && multiple == -1) { newPos += 100 }
            while (newPos < 0) { newPos += 100; count++ }
            while (newPos > 100) { newPos -= 100; count++ }
            newPos %= 100
            if (newPos == 0) count++
            val slowcount = generateSequence { multiple }.take(distance).runningFold(pos) { p, m -> (p + m) % 100 }.drop(1).count { it == 0 }
            if (count != slowcount) { print("-->") } else { print("   ") }
            println("Moving ${multiple * distance} from $pos to $newPos (count $count, $slowcount)")
            zeroCount += slowcount
            newPos
        }
        return zeroCount.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 3L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 1)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}

private fun toPairs(input: List<String>): List<Pair<Long, Long>> = input.asSequence()
    .map { it.split("\\s+".toRegex(), 2).map { it.toLong() }.zipWithNext()[0] }
    .toList()
