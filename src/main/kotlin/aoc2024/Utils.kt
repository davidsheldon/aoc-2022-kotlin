package aoc2024

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
fun Iterable<Long>.product() = reduce(Long::times)

fun <T> Sequence<Set<T>>.intersectAll() = reduce(Set<T>::intersect)
fun <T> Sequence<Set<T>>.unionAll() = reduce(Set<T>::union)

fun <T> Sequence<T>.repeatForever() = generateSequence(this) { it }.flatten()

fun gcd(a: Long, b: Long): Long {
    var x = a
    var y = b
    while (x != y) {
        if (x>y) { x -= y } else { y -= x }
    }
    return x
}
fun lcm(a: Long, b: Long): Long = a * (b / gcd(a, b))

fun Iterable<Long>.lcm(): Long = reduce { a, b -> lcm(a,b)}

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

data class Edge<T>(val start: T, val end: T, val cost: Int)

fun <T> shortestPath(edges: List<Edge<T>>, start: T, end: T): ShortestPathResult<T> {
    val q = findDistinctNodes(edges)
    val edgesLeaving = edges.groupBy { it.start }
    val dist = mutableMapOf(*q.map { it to Int.MAX_VALUE}.toTypedArray())
    val prev = mutableMapOf<T, T?>(*q.map { it to null}.toTypedArray())
    fun getDist(x: T) = dist[x] ?: 0
    dist[start] = 0
    while(q.isNotEmpty()) {
        val u = q.minByOrNull { getDist(it) }!!
        q.remove(u)

        if(u == end) break

        edgesLeaving[u]
            ?.forEach { edge ->
                val v = edge.end
                val alt = getDist(u) + edge.cost
                if (alt < getDist(v)) {
                    dist[v] = alt
                prev[v]=u
            }
        }
    }
    return ShortestPathResult(prev, dist, start, end)
}

fun <T> findDistinctNodes(edges: List<Edge<T>>): MutableSet<T> =
    edges.flatMap { edge -> listOf(edge.start, edge.end) }.toMutableSet()

class ShortestPathResult<T>(val prev: Map<T, T?>, val dist: Map<T, Int>, val source: T, val target: T) {

    fun shortestPath(from: T = source, to: T = target, list: List<T> = emptyList()): List<T> {
        val last = prev[to] ?: return if (from == to) {
            list + to
        } else {
            emptyList()
        }
        return shortestPath(from, last, list) + to
    }

    fun shortestDistance(): Int? {
        val shortest = dist[target]
        if (shortest == Integer.MAX_VALUE) {
            return null
        }
        return shortest
    }
}



