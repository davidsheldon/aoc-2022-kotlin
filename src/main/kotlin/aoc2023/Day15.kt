package aoc2023

import utils.InputUtils
import java.lang.IllegalArgumentException

val parseInstruction = "([a-z]+)(-)?(=(\\d))?".toRegex()
private fun String.day15Hash() = fold(0) { acc, c ->
    ((acc + c.code) * 17) % 256
}

private class HashTable {
    val buckets = Array(256) { mutableListOf<Pair<String, Int>>() }

    fun score() = buckets.flatMapIndexed { index, pairs ->
        pairs.mapIndexed { i, pair -> (index + 1) * (i + 1) * pair.second }
    }.sum()

    fun add(key: String, lense: Int) {
        val bucket = bucket(key)

        val index = bucket.indexOfFirst { it.first == key }
        if (index == -1) bucket.add(key to lense)
        else bucket[index] = key to lense
    }

    fun remove(key: String) {
        bucket(key).removeIf { it.first == key }
    }

    private fun bucket(key: String) = buckets[key.day15Hash()]
}


fun main() {
    val testInput = """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        return input.joinToString("").split(",")
            .sumOf { it.day15Hash() }
    }


    fun part2(input: List<String>): Int {
        val h = HashTable()

        input.joinToString("").split(",")
            .map { instruction ->
                parseMatchOrThrow(instruction, parseInstruction) {
                val (key, remove, _, lense) = it.destructured
                //println("$key, $remove, $lense")
                if (remove.isNotEmpty()) {
                    h.remove(key)
                } else if (lense.isNotEmpty()) {
                    h.add(key, lense.toInt())
                } else throw IllegalArgumentException("Invalid instruction $instruction")

            } }

        return h.score()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 1320)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 15)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
