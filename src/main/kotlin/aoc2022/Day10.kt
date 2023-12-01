package aoc2022

import utils.InputUtils
import kotlin.math.absoluteValue

data class CPUState(val x: Int)

sealed interface Instructions
object Noop : Instructions
class AddX(val amt: Int) : Instructions


fun main() {
    val testInput = """addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop""".split("\n")

    fun registerValues(input: List<String>): Sequence<Int> {
        val instructions: List<Instructions> = input.map {
            val cmd = it.substringBefore(' ')
            when (cmd) {
                "noop" -> Noop
                "addx" -> AddX(it.substringAfter(' ').toInt())
                else -> throw IllegalArgumentException(it)
            }
        }
        return sequence {
            var cpu = CPUState(1)
            instructions.forEach { inst ->
                when (inst) {
                    Noop -> yield(cpu.x)
                    is AddX -> {
                        yield(cpu.x); yield(cpu.x);
                        cpu = cpu.copy(x = cpu.x + inst.amt)
                    }
                }
            }

        }
    }

    fun part1(input: List<String>): Int {
        val vals = registerValues(input)
        println(vals.filterIndexed { index, i -> ((index + 1) in listOf(20, 60, 100, 140, 180, 220)) }.toList())
        return vals.mapIndexed { index, i ->
            if ((index + 1) in listOf(
                    20,
                    60,
                    100,
                    140,
                    180,
                    220
                )
            ) (index + 1) * i else 0
        }.sum()
    }


    fun part2(input: List<String>): String {
        val vals = registerValues(input)
        return vals.mapIndexed { index, pos ->
            if (((index % 40) - pos).absoluteValue < 2) '*' else ' '
        }.chunked(40).map { it.toCharArray().concatToString() }.joinToString("\n")
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 13140)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 10).toList()


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
