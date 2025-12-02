package aoc2024

import utils.Coordinates
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Collections.swap

fun <T> bfs(
    start: T,
    neighbours: (T) -> Sequence<T>
): Sequence<T> {
    val queue = ArrayDeque<T>()
    val seen = mutableSetOf<T>()
    queue.add(start)
    return sequence {
        while (!queue.isEmpty()) {
            val pos = queue.removeFirst()
            if (pos !in seen) {
                yield(pos)
                seen.add(pos)
                queue.addAll(neighbours(pos))
            }
        }
    }
}

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


data class Edge<T>(val start: T, val end: T, val cost: Int) {
    fun reversed() = copy(start=end, end=start)
}

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

fun <V> List<V>.permutations(): List<List<V>> {
    val retVal: MutableList<List<V>> = mutableListOf()

    fun generate(k: Int, list: List<V>) {
        // If only 1 element, just output the array
        if (k == 1) {
            retVal.add(list.toList())
        } else {
            for (i in 0 until k) {
                generate(k - 1, list)
                if (k % 2 == 0) {
                    swap(list, i, k - 1)
                } else {
                    swap(list, 0, k - 1)
                }
            }
        }
    }

    generate(this.count(), this.toList())
    return retVal
}


fun Coordinates.distanceTo(other: Coordinates) = (other - this).length()

fun <T> List<T>.allPairs(): Sequence<Pair<T,T>> = asSequence().flatMapIndexed { a, first ->
    (a..<size).mapNotNull { b -> get(b).let { first to it } }
}


fun String.listOfLongs(): List<Long> = trim().split("\\D+".toRegex()).map { it.trim().toLong() }

typealias Matrix=Array<IntArray>

fun multiplyMatrices(matrix1: Matrix, matrix2: Matrix): Matrix {
    val row1 = matrix1.size
    val col1 = matrix1[0].size
    val col2 = matrix2[0].size
    val product = Array(row1) { IntArray(col2) }

    for (i in 0 until row1) {
        for (j in 0 until col2) {
            for (k in 0 until col1) {
                product[i][j] += matrix1[i][k] * matrix2[k][j]
            }
        }
    }

    return product
}
fun matrixPower(matrix1: Matrix, n:Int) = (1..<n).fold(matrix1) { acc, _ -> multiplyMatrices(acc, matrix1) }
fun Matrix.trace() = (0..<size).sumOf { get(it)[it] }

class DisjointSets<T> {
    val parents = mutableMapOf<T,T>()
    fun add(item:T) {
        parents.putIfAbsent(item, item)
    }
    fun root(a:T):T {
        val y = parents[a]!!
        if (y==a) { return a }
        val ret = root(y)
        parents[a] = ret
        return ret
    }
    fun merge(a:T, b: T) {
        val aRoot = root(a)
        val bRoot = root(b)
        if (aRoot == bRoot) { return }
        parents[aRoot] = bRoot
    }
}

fun List<List<String>>.combinations(): List<String> {
    if (isEmpty()) return emptyList()
    if (size == 1) return first()

    val tail = drop(1).combinations()
    return first().flatMap { h ->
        tail.map { t -> h + t }
    }
}


// Clique finding - https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
class BronKerbosch<T>(private val neighbours: Map<T, Set<T>>) {

    private var bestR: Set<T> = emptySet()

    fun largestClique(): Set<T> {
        execute(neighbours.keys)
        return bestR
    }

    private fun execute(
        p: Set<T>,
        r: Set<T> = emptySet(),
        x: Set<T> = emptySet()
    ) {
        if (p.isEmpty() && x.isEmpty()) {
            // We have found a potential best R value, compare it to the best so far.
            if (r.size > bestR.size) bestR = r
        } else {
            val mostNeighboursOfPandX: T = (p + x).maxBy { neighbours[it]!!.size }!!
            val pWithoutNeighbours = p.minus(neighbours[mostNeighboursOfPandX]!!)
            pWithoutNeighbours.forEach { v ->
                val nV = neighbours[v]!!
                execute(
                    p.intersect(nV),
                    r + v,
                    x.intersect(nV)
                )
            }
        }
    }
}