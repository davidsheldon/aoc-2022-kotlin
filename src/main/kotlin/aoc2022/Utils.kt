package aoc2022

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to aoc2022.md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)


fun <T> Sequence<T>.splitBy(predicate: (T) -> Boolean): Sequence<List<T>> {
    val seq = this;
    if (!seq.iterator().hasNext())
        return emptySequence()
    return sequence {
        val current = mutableListOf<T>()
        seq.forEach { element ->
            val split = predicate(element)
            if (split && current.isNotEmpty()) {
                yield(current)
                current.clear()
            }
            else {
                current.add(element)
            }
        }
        if (current.isNotEmpty()) yield(current)
    }
}

fun blocksByBlankLines(lineSeq: Sequence<String>): Sequence<IndexedValue<String>> = sequence {
        var blockId = 0
        lineSeq.forEach { line ->
            if (line.isBlank()) {
                blockId++
            } else {
                yield(IndexedValue(blockId, line))
            }
        }
    }

fun blocksOfLines(lineSeq: Sequence<String>): Collection<List<String>> =
    blocksByBlankLines(lineSeq)
        .groupBy({ it.index }, { it.value })
        .values

fun <R> parseMatchOrThrow(line: String, pattern: Regex, onMatch: (MatchResult) -> R): R {
    val match = pattern.matchEntire(line)
    if (match != null) {
        return onMatch(match)
    }
    throw IllegalArgumentException(line)

}

fun <A> applyN(dir: A, count: Int, func: (A) -> A): A =
    (1..count).fold(dir) { acc, i -> func(acc) }

fun <T: Comparable<T>> Iterable<T>.topN(n: Int) = this.fold(ArrayList<T>()) { acc, item ->
    if (acc.size < n || item > acc.last()) {
        acc.add(item)
        acc.sortDescending()
        if (acc.size > n) acc.removeAt(n)
    }
    acc
}