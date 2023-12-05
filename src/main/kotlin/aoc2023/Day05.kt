package aoc2023
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import utils.InputUtils

val PARALLEL = true
data class SeedRange(val destStart: Long, val sourceStart: Long, val size: Int) {
    fun contains(x: Long) = (x >= sourceStart) && (x < (sourceStart + size))
    fun endInclusive() = sourceStart + size - 1
    fun endExclusive() = sourceStart + size
    fun apply(x: Long) = if (contains(x)) { x - sourceStart + destStart } else { x }
    fun applyAndSplit(range: LongRange): List<LongRange> = if (intersect(range)) {
        if (range.contains(sourceStart) && range.contains(endInclusive())) {
            listOf(destStart..< (destStart + size))
        } else { emptyList() }
    } else { emptyList() }
    fun intersect(range: LongRange): Boolean = (sourceStart <= range.last) xor (endInclusive() <= range.first)
}
data class SeedMap(val from: String, val to: String, val ranges: List<SeedRange>) {
    fun apply(x: Long) = ranges.firstOrNull { it.contains(x) }?.apply(x) ?: x
    fun apply(range: LongRange): List<LongRange> = ranges.flatMap { it.applyAndSplit(range)}
}
data class Day5(val seeds: List<Long>, val maps: List<SeedMap>) {
    fun mapSeed(seed: Long) = maps.fold(seed) { acc, map -> map.apply(acc)}
    fun minOfRange(range: LongRange) = maps.fold(listOf(range)) { acc, map -> acc

    }
}
fun String.listOfLongs(): List<Long> = trim().split("\\D+".toRegex()).map { it.trim().toLong() }


fun main() {
    val testInput = """seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4""".trimIndent().split("\n")

    val titleParser = "(\\w+)-to-(\\w+) map:".toRegex()
    fun <T> Iterable<T>.tail() = drop(1)

    fun List<String>.parseSeedMap(): SeedMap {
        val (from, to) = titleParser.matchEntire(first())!!.destructured
        val ranges = tail().map {
            val (dest, source, size) = it.listOfLongs()
            SeedRange(dest, source, size.toInt())
        }
        return SeedMap(from, to, ranges)
    }

    fun List<String>.parseDay5(): Day5 {
        val blocks = toBlocksOfLines()
        val seeds =
            blocks.first().first().substringAfter(":").listOfLongs()

        return Day5(seeds, blocks.drop(1).map { it.parseSeedMap()})
    }

    fun part1(input: List<String>): Long {
        val problem = input.parseDay5()
        return problem.seeds.minOf { problem.mapSeed(it) }
    }
    suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
        map { async { f(it) } }.awaitAll()
    }
    fun LongRange.chunked(size: Long) = sequence {
        var current = first
        while(current <= last) {
            val end = kotlin.math.min(current + size, last + 1)
            yield(current ..< end)
            current = end
        }
    }

    fun part2(input: List<String>): Long {
        val problem = input.parseDay5()
        val seedRanges = problem.seeds.chunked(2).map { it[0]..<(it[0] + it[1]) }
        val totalSize = problem.seeds.chunked(2).sumOf { it[1] }
        println("Total size: $totalSize")
        return if (PARALLEL) {
                seedRanges.asSequence().map { range ->
                    runBlocking(Dispatchers.IO) {
                    val min = range.chunked(5_000_000).toList()
                        .pmap { subRange -> subRange.minOf { problem.mapSeed(it) }}
                        .min()
                    println("Min: $min")
                    min
                }}
                    .min()
        } else {
            var complete = 0L
            seedRanges
                .onEach { complete += (1 + it.last - it.first) }
                .map { range -> range.minOf { problem.mapSeed(it) } }
                .onEach { println("Min: $it, $complete/$totalSize") }
                .min()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 35L)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 5)
    val input = puzzleInput.toList()

    println(part1(input))

    val start = System.currentTimeMillis()
    println(part2(input))
    println(System.currentTimeMillis() - start)
}
