package aoc2024

import utils.InputUtils
import kotlin.math.log10
import kotlin.math.pow


private val cache = mutableMapOf<Pair<Int, Long>, Long>()

private class Stones(val stones: List<Long>) {
    fun blink(): Stones {
        return Stones(
            stones.flatMap { s -> split(s) }
        )
    }

    fun countAfterBlinks(blinks: Int): Long =
        stones.sumOf { s -> stoneAfterBlinks(blinks, s) }


    fun stoneAfterBlinks(blinks: Int, stone: Long): Long {
        val key = blinks to stone
        if (cache.contains(key)) return cache[key]!!
        val ret = stonesImpl(blinks, stone)
        cache[key] = ret
        return ret
    }

    private fun stonesImpl(b: Int, s: Long): Long {
        if (b <= 0) return 1
        if (s == 0L) {
            return stoneAfterBlinks(b - 1, 1)
        }
        val len = log10(s.toDouble()).toInt() + 1
        if (len % 2 == 1) {
            return stoneAfterBlinks(b - 1, s * 2024L)
        }
        val factor = 10.0.pow(len / 2).toLong()
        return stoneAfterBlinks(b - 1, s / factor) + stoneAfterBlinks(b - 1, s % factor)
    }

    fun split(s: Long): Sequence<Long> {
        if (s == 0L) { return sequenceOf(1) }
        val len = log10(s.toDouble()).toInt() + 1
        if (len % 2 == 1) { return sequenceOf(s * 2024L) }
        val factor = 10.0.pow(len/2).toLong()
        return sequenceOf(s/factor, s%factor)
    }

    override fun toString() = stones.joinToString(" ")

}

fun main() {

    val testInput = """125 17""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val stones = Stones(input[0].listOfLongs())

        val end = IntRange(1, 25).runningFold(stones) { s, _ -> s.blink()}
            //.onEach(::println)
            .last()

        //return end.stones.size.toLong()
        return stones.countAfterBlinks(25)
    }


    fun part2(input: List<String>): Long {
        val stones = Stones(input[0].listOfLongs())

        return stones.countAfterBlinks(75)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 55312L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 11)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
