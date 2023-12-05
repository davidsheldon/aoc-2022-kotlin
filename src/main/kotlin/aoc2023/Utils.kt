package aoc2023

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

fun blocksByBlankLines(lineSeq: Iterable<String>): Sequence<IndexedValue<String>> = sequence {
    var blockId = 0
    lineSeq.forEach { line ->
        if (line.isBlank()) {
            blockId++
        } else {
            yield(IndexedValue(blockId, line))
        }
    }
}

fun blocksOfLines(lineSeq: Iterable<String>): Collection<List<String>> =
    blocksByBlankLines(lineSeq)
        .groupBy({ it.index }, { it.value })
        .values

fun Iterable<String>.toBlocksOfLines() = blocksOfLines(this)

fun <R> parseMatchOrThrow(line: String, pattern: Regex, onMatch: (MatchResult) -> R): R {
    val match = pattern.matchEntire(line)
    if (match != null) {
        return onMatch(match)
    }
    throw IllegalArgumentException(line)
}

fun listOfNumbers(list: String): List<Int> = list.trim().split(',').map { it.trim().toInt() }

fun Iterable<String>.parsedBy(pattern: Regex): Sequence<MatchResult> = parsedBy(pattern) { it }

fun <R> String.parsedBy(pattern: Regex, onMatch: (MatchResult) -> R): R =
    onMatch(pattern.matchEntire(this) ?: throw IllegalArgumentException(this))

fun <R> Iterable<String>.parsedBy(pattern: Regex, onMatch: (MatchResult) -> R): Sequence<R> =
    asSequence().map { line ->
        line.parsedBy(pattern, onMatch)
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

fun <T> Sequence<T>.takeWhilePlusOne(predicate: (T) -> Boolean): Sequence<T> {
    var lastSeen: T? = null
    return takeWhile {
        val shouldTake = predicate(it)
        if (!shouldTake) lastSeen = it
        shouldTake
    } + sequence {
        if (lastSeen != null) yield(lastSeen!!)
    }
}


fun Iterable<Int>.product() = reduce(Int::times)

fun <T> Sequence<Set<T>>.intersectAll() = reduce(Set<T>::intersect)
fun <T> Sequence<Set<T>>.unionAll() = reduce(Set<T>::union)

fun <T> Sequence<T>.repeatForever() = generateSequence(this) { it }.flatten()
