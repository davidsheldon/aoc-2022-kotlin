package aoc2022

import aoc2022.utils.InputUtils

object Day05 {
    data class Command(val n: Int, val from: Int, val to: Int)

    data class Stacks(private val state: List<String>) {
        override fun toString(): String {
            val seq = sequence<List<String>> {
                yield(state.indices.map { " ${it + 1} " })
                val maxLength: Int = state.maxOf { it.length }
                for (x in 0 until maxLength) {
                    yield(state.map {
                        if (x < it.length) "[${it[x]}]" else "   "
                    })
                }
            }
            return seq.map { it.joinToString(" ") }.toList().reversed().joinToString("\n")
        }

        fun tops() = state.map { it.last() }

        fun <T> List<T>.replace(n: Int, new: T) = mapIndexed { i, v -> if (i == n) new else v }

        fun moveSingle(from: Int, to: Int): Stacks = moveN(1, from, to)

        fun moveN(n: Int, from: Int, to: Int): Stacks {
            val c = state[from].takeLast(n)
            val newState = state
                .replace(from, state[from].dropLast(n))
                .replace(to, state[to] + c)
            return Stacks(newState)
        }

        fun applyPart1(c: Command) = applyN(this, c.n) { it.moveSingle(c.from - 1, c.to - 1) }
        fun applyPart2(c: Command) = moveN(c.n, c.from - 1, c.to - 1)
    }


}

fun parseToStack(s: List<String>): Day05.Stacks {
    val r = s.reversed()
    val maxIndex = r[0].max().digitToInt()
    val tail = r.drop(1)

    val stacks = (0 until maxIndex).map { col ->
        val index = (col * 4) + 1
        tail.takeWhile { index < it.length }
            .map { it[index] }
            .filter { it in 'A'..'Z' || it in 'a'..'z' }
            .joinToString("")
    }.toList()
    return Day05.Stacks(stacks)
}


fun main() {
    val testInput = """
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2""".split("\n")

    fun parse(input: List<String>): Pair<Day05.Stacks, Sequence<Day05.Command>> {
        val (rawState, rawCommands) = blocksOfLines(input.asSequence()).toList()
        val state = parseToStack(rawState)
        val commands = rawCommands.parsedBy("move (\\d+) from (\\d+) to (\\d+)".toRegex()) {
            val (count, from, to) = it.destructured
            Day05.Command(count.toInt(), from.toInt(), to.toInt())
        }
        return state to commands
    }

    fun part1(input: List<String>): String {
        val (state, commands) = parse(input)
        return commands.fold(state) { s, command -> s.applyPart1(command) }.tops().joinToString("")
    }

    fun part2(input: List<String>): String {
        val (state, commands) = parse(input)
        return commands.fold(state) { s, command -> s.applyPart2(command) }.tops().joinToString("")
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == "CMZ")

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 5)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
