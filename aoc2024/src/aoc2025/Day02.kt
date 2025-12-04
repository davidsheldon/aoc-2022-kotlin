package aoc2025

import utils.InputUtils
import java.util.function.Predicate
import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.measureTime

fun String.toLongRange(): LongRange {
    val (start, end) = split('-')
    return start.toLong()..end.toLong()
}


fun main() {
    val testInput = "11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124"


    fun isValid(id: String): Boolean {
        if (id.length % 2 == 1) return true
        val half = id.length  / 2
        return (0..<half).any { id[it] != id[it + half]}
//        return id.take(half) != id.drop(half)
    }

    fun Long.length() = when(this) {
        0L -> 1
        else -> log10(toDouble()).toInt() + 1
    }


    fun tenToPow(i: Int): Long = 10.toDouble().pow(i).toLong()

    fun isInvalid(id: Long): Boolean {
        val len = id.length()
        if (len % 2 == 1) return false
        val half = len  / 2
        val factor = tenToPow(half)
        return (id % factor) == (id / factor)
    }


    fun runIt(input: String, filter: Predicate<String>): Long {
        val inputs: Sequence<String> = input.split(",").asSequence().flatMap {
            val range = it.toLongRange()

            range.asSequence().map { it.toString() }
        }
        return inputs.filter { filter.test(it) }.sumOf { it.toLong() }
    }


    fun sumInvalidInRangeUsingFilter(filter: Predicate<Long>): (LongRange) -> Long = { range : LongRange ->
        range.filter { filter.test(it)}.sum()
    }

    fun splitRange(range: LongRange, split: Long): List<LongRange> = listOf(range.first..<split, split..range.last )

    fun LongRange.intersectRange(lr: LongRange): LongRange {
        val l = first.coerceAtLeast(lr.first)
        val r = last.coerceAtMost(lr.last)
        if (r < l) return LongRange.EMPTY
        return l..r
    }


    fun splitRange(range: LongRange): List<LongRange> {
        return (range.first.length()..range.last.length())
            .map { tenToPow(it - 1) }
            .map { range.intersectRange(it..<(10*it))}
            .filter { !it.isEmpty()}

    }

    fun duplicate(prefix: Long, factor: Long, times: Int): Long {
        var total = 0L
        repeat(times) { total = (total * factor) + prefix }
        return total
    }

    fun sumInvalidInRangeP2(range: LongRange, debug: Boolean = false): Long {
        if(debug) println(range)
        if (range.first.length() != range.last.length()) {
            return splitRange(range).sumOf { sumInvalidInRangeP2(it, debug)}
        }

        val len = range.first.length()
        val invalid = (1..(len / 2))
            .filter { n -> len % n == 0 }
            .flatMap { size ->
                val factor = tenToPow(size)
                val times = len / size
                (tenToPow(size - 1)..<factor).map { duplicate(it, factor, times) }
                    .filter { range.contains(it) }
            }.distinct()
        if (debug) println(invalid)
        return invalid
            .sum()

    }





    fun runLongSum(input: String, sumFunction: (LongRange) -> Long): Long {
        val inputs: List<Long> = input.split(",").map {
            sumFunction(it.toLongRange())
        }
        return inputs.sum()
    }

    fun runItLong(input: String, filter: Predicate<Long>): Long =
        runLongSum(input, sumInvalidInRangeUsingFilter(filter))



    fun isInvalid2(id: Long): Boolean {
        val len = id.length()
        return (1..len/2)
            .filter { n -> len % n == 0 }
            .any { n ->
                val factor = tenToPow(n)
                val first = id % factor

                var remainder = id / factor
                while (remainder > 0) {
                    if (remainder % factor != first) return@any false
                    remainder /= factor
                }
                true
            }
    }

    fun isValid2(id: String): Boolean {
        return !
           (1..id.length/2)
            .filter { id.length % it == 0 }
            .any { n ->
            (0..<n).all { i ->
                (i ..< id.length step n).all { j -> id[i] == id[j]}
            }
            /* Equivalent to:
             val a = id.chunked(n)
             a.all { it == a[0] }
             */
        }
    }


    fun part1(input: String): Long {
        return runItLong(input) { isInvalid(it) }
    }




    fun part2(input: String): Long {
        return runItLong(input) { isInvalid2(it) }
//        return runIt(input) { !isValid2(it) }
    }

    fun part2Fast(input: String): Long {
        return runLongSum(input) {
            sumInvalidInRangeP2(it)
        }
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 1227775554L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 2)
    val input = puzzleInput.toList()


    println(measureTime { println(part1(input[0])) })
    println(measureTime { println(part2Fast(input[0])) })
    println(measureTime { println(part2(input[0])) })
}
