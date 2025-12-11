package aoc2025

import aoc2024.listOfNumbers
import aoc2024.parsedBy
import utils.InputUtils
import java.util.*
import kotlin.time.measureTime

val wiringRegex = "\\(([^)]*)\\)".toRegex()
val puzzleRegex = "\\[(.*)] (($wiringRegex ?)+) \\{([\\d,]+)}".toRegex()

// {.....} = 0
// each wiring = 1
//



data class LightPuzzle(
    val target: String,
    val wirings: List<List<Int>>,
    val joltages: List<Int>
) {
    var targetBits: Long =
        target.mapIndexed { index, bit ->
            if (bit=='#') { 1L shl index } else {0L}
        }
            .sum()

    val wiringsBits = wirings.map {
        it.sumOf { idx -> 1L shl idx }
    }


    override fun toString(): String = "[$targetBits] $wirings {$joltages}"


    fun minSwitchesForBits(): Int {
        if (targetBits == 0L) { return 0 }

        var currentSet = setOf(0L)
        var currentCount = 0
        val cache = mutableMapOf(0L to 0)

        while(!cache.contains(targetBits)) {
            currentCount++
            currentSet = currentSet.flatMap { bitSet ->
                wiringsBits.map { w -> w.xor(bitSet) } }
                .filter { cache.putIfAbsent(it, currentCount) == null }
                .toSet()
        }

        return cache[targetBits] ?: -1
    }

    fun addCounts(jolts: List<Int>, indiciesToIncrement: List<Int>): List<Int> {
        return jolts.mapIndexed { i, j ->
            if (i in indiciesToIncrement) {
                j + 1
            } else {
                j
            }
        }
    }

    private fun generateCombinations(target: Int, size: Int): List<List<Int>> {
        fun generate(remaining: Int, length: Int, current: List<Int>): List<List<Int>> {
            if (length == 0) return if (remaining == 0) listOf(current) else emptyList()
            return (0..remaining).flatMap { i ->
                generate(remaining - i, length - 1, current + i)
            }
        }
        return generate(target, size, emptyList())
    }

    fun Int?.toOptionalInt() = if (this == null) OptionalInt.empty() else OptionalInt.of(this )
    fun OptionalInt.toInt(): Int? = if (isEmpty) null else this.asInt

    val cache = mutableMapOf<Pair<Set<Set<Int>>,List<Int>>, OptionalInt>()
    fun cachedFewest(switches: Set<Set<Int>>, jolts: List<Int>): Int? {
        if (cache.contains(switches to jolts)) return cache[switches to jolts]?.toInt()
        val ret = fewestPresses(switches, jolts)

        cache[switches to jolts] = ret.toOptionalInt()
        return ret
    }

    // Nasty hack, took 40 minutes, mostly on problem 16, and apparently too low
    fun fewestPresses(switches: Set<Set<Int>>, jolts: List<Int>): Int? {
        val maxJolt = jolts.max()
        if (maxJolt == 0) return 0
        if (switches.isEmpty()) return null
        val influencesForEach = jolts.indices.associateWith { ji -> // JoltIndex to Switch
            switches.filter { sw -> sw.contains(ji) }
        }

        val (k, sws) = influencesForEach.entries
            .filter { it.value.isNotEmpty() }
            .minByOrNull { it.value.size }!!

        val presses = jolts[k]

        if (sws.size == 1) {
            val sw = sws.first()
            val newJolts = jolts.mapIndexed { i, j -> if (i in sw) {j - presses } else {j } }
            if (newJolts.any { it < 0 }) return null
            return fewestPresses(switches.minusElement(sw), newJolts)?.plus(presses)
        }
        else {
            //println("Generating for ${sws.size} ${jolts}")
            val combinations = generateCombinations(presses, sws.size)
            var minTotal: Int? = null

            val newSwitches = switches.minus(sws)
            for (combo in combinations) {
                val newJolts = jolts.toMutableList()
                sws.forEachIndexed { index, sw ->
                    sw.forEach { i ->
                        newJolts[i] = newJolts[i] - combo[index]
                    }
                }
                if (newJolts.any { it < 0 }) continue
                val subResult = fewestPresses(newSwitches, newJolts)
                if (subResult != null) {
                    val total = subResult + combo.sum()
                    if (minTotal == null || total < minTotal) {
                        minTotal = total
                    }
                }
            }
            return minTotal
        }


    }

    fun minSwitchesForJoltagesLinearAlg(): Int {
        // Convert to matrix
        // For each switch, i, it will be pressed a_i times.
        // s_ij is 1 if switch i increases joltage j

        // We want to minimise \sum a_i
        // Whilst fulfilling the equations
        // \sum{i} \suma{j} a_i * s_{i}{j}

        // Find any joltage influenced by a single switch, subtract.
        // repeat
        // Now the max switch presses is the highest remaining joltage
        // iterate all the combinations of switch presses remaining

        // Solve!

        return fewestPresses(
            wirings.map { it.toSet() }.toSet(), joltages.toList()
        )!!
    }
    fun minSwitchesForJoltageBruteForce(): Int {
        // OOMs after 5 mins
        val initial = joltages.map { 0 }
        var currentSet = setOf(initial)
        var currentCount = 0
        val cache = mutableMapOf(initial to 0)

        while(!cache.contains(joltages)) {
            currentCount++
            currentSet = currentSet.flatMap { joltage ->
                wirings.map {
                    addCounts(joltage, it)
                }.filter { counts ->
                    counts.indices.all { counts[it] <= joltages[it] }
                }
            }
                .filter { cache.putIfAbsent(it, currentCount) == null }
                .toSet()
            println("  Count: $currentCount cache size: ${cache.size}")
        }

        return cache[joltages] ?: -1
    }
}



fun String.toWirings(): List<List<Int>> {
    return split(' ').parsedBy(wiringRegex) {
        it.groupValues[1].split(",").map { it.toInt() }
    }.toList()
}

fun String.toPuzzle(): LightPuzzle {
    return parsedBy(puzzleRegex) {
        val (t,w,_,_,j) = it.destructured
        LightPuzzle(t, w.toWirings(), listOfNumbers(j))
    }
}

fun main() {

    val testInput = """
[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
[...#.] (0,2,3,4) (2,3) (0,4) (0,1,2) (1,2,3,4) {7,5,12,7,2}
[.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}""".trimIndent().split("\n")


    fun part1(input: List<String>): Long {
        val puzzles = input.map { it.toPuzzle() }
        //println(puzzles)

        return puzzles.sumOf { it.minSwitchesForBits().toLong() }
    }

    fun part2(input: List<String>): Long {
        val puzzles = input.map { it.toPuzzle() }
        //println(puzzles)
        var count = 0
        return puzzles.asSequence().map { it.minSwitchesForJoltagesLinearAlg().toLong() }
            .onEach { println("Count $it (${++count}/${input.size})") }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 7L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 10)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
