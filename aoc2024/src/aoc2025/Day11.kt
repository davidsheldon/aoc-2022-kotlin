package aoc2025

import utils.InputUtils
import kotlin.time.measureTime

data class Link(val e1: String, val e2: String)

fun main() {

    val testInput = """aaa: you hhh
you: bbb ccc
bbb: ddd eee
ccc: ddd eee fff
ddd: ggg
eee: out
fff: out
ggg: out
hhh: ccc fff iii
iii: out""".trimIndent().split("\n")


    fun part1(input: List<String>): Long {
        val links = input.associate {
            val nodes = it.split(':', ' ')
            nodes.first() to nodes.drop(1)
        }
        return waysFrom(links, "you", "out")
    }

    fun part2(input: List<String>): Long {
        val links = input.associate {
            val nodes = it.split(':', ' ')
            nodes.first() to nodes.drop(1)
        }

        val svr_dac = waysFrom(links, "svr", "dac", "fft")
        val svr_fft = waysFrom(links, "svr", "fft", "dac")
        val dac_fft = waysFrom(links, "dac", "fft", "out")
        val fft_dac = waysFrom(links, "fft", "dac", "out")
        val dac_out = waysFrom(links, "dac", "out", "fft")
        val fft_out = waysFrom(links, "fft", "out", "dac")


        return (svr_dac * dac_fft * fft_out) + (svr_fft * fft_dac * dac_out)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    val testInput2 = """svr: aaa bbb
aaa: fft
fft: ccc
bbb: tty
tty: ccc
ccc: ddd eee
ddd: hub
hub: fff
eee: dac
dac: fff
fff: ggg hhh
ggg: out
hhh: out""".trimIndent().split("\n")
    println(part2(testInput2))
    check(testValue == 5L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 11)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}

fun waysFrom(
    links: Map<String, List<String>>,
    start: String,
    end: String,
    exclude: String? = null,
    cache: MutableMap<Pair<String, String>, Long> = mutableMapOf()
): Long {
    if (cache.contains(start to end)) return cache[start to end]!!
    if (start == end) return 1
    if (start == exclude) return 0
    val ret = links[start]?.sumOf { waysFrom(links, it, end, exclude, cache) } ?: 0
    cache[start to end] = ret
    return ret
}
