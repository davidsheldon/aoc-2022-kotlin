package aoc2023

import aoc2022.parsedBy
import utils.InputUtils

class Card(val id: Int, val winners: Set<Int>, val contents: Set<Int>) {
    fun matches() = winners.intersect(contents)
    fun score(): Int {
        val power = matches().size
        return if (power > 0) { 1 shl (power -1) } else 0
    }
}
fun String.listOfNumbers(): List<Int> = trim().split(" +".toRegex()).map { it.trim().toInt() }
fun String.setOfNumbers(): Set<Int> = listOfNumbers().toSet()

fun String.parseCard(): Card =
    parsedBy("Card +(\\d+): (.*) \\| (.*)".toRegex()) {
       val (idStr, w, c) = it.destructured
       Card(idStr.toInt(), w.setOfNumbers(), c.setOfNumbers())
    }


fun main() {
    val testInput = """Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11""".trimIndent().split("\n")


    fun part1(input: List<String>): Int {
        return input.sumOf { it.parseCard().score() }
    }


    fun part2(input: List<String>): Int {
        val copies = IntArray(input.size + 1) { if(it>0) { 1 } else{0}}
        input.map { it.parseCard() }
            .forEach { card ->
                val matches = card.matches().size
                val score = copies[card.id]
                (1..matches).forEach { i -> copies[card.id+i] += score }
            }
        return copies.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 13)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 4)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
