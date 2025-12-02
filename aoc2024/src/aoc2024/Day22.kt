package aoc2024

import utils.InputUtils

fun nextRandom(r: Long): Long {
    var newR = (r xor (r shl 6)) % 16777216
    newR = (newR xor (newR shr 5)) % 16777216
    newR = (newR xor (newR shl 11)) % 16777216

    return newR
}

fun secrets(seed: Long) = generateSequence(seed) { nextRandom(it) }
fun prices(seed: Long) = secrets(seed).map { it % 10 }

fun bits(seed: Long) = prices(seed).windowed(5).map {
    it.zipWithNext().map { (a,b) -> (b-a).toInt() } to it.last()
}

fun nthSecret(seed: Long, n:Int) = secrets(seed).drop(n).first()

fun main() {

    val testInput = """1
10
100
2024""".trimIndent().split("\n")

    println(secrets(123).take(10).toList())
    println(bits(123).take(10).toList())

    fun part1(input: List<String>): Long {
        return input
            .map { it.toLong()}
            .map {nthSecret(it, 2000)}
//            .onEach(::println)
            .sum()
    }


    fun part2(input: List<String>): Long {
        val inputs = input
            .map { it.toLong()}

        val scores = mutableMapOf<List<Int>,Int>()
        inputs.forEach {
            val thisScore = mutableMapOf<List<Int>,Int>()
            bits(it).take(2000 - 4)
                .forEach { thisScore.putIfAbsent(it.first, it.second.toInt()) }
            thisScore.forEach { scores.compute(it.key) { _, acc -> (acc?: 0).plus(it.value)} }
        }
        println(scores.values.max())
        val seq = scores.maxBy { it.value }.key
        println(seq)
        return inputs.sumOf { monkey ->
            bits(monkey).take(2000 - 4).firstOrNull { it.first == seq }?.second ?: 0
        }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 37327623L)
    println(part2(listOf("1", "2", "3", "2024")))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 22)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
