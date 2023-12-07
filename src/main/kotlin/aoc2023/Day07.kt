package aoc2023

import utils.InputUtils

enum class HandTypes {
    FIVE_KIND, FOUR_KIND, FULL_HOUSE, THREE_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
}
val scores = "23456789TJQKA".mapIndexed { index, c -> c to index }.toMap()
val jScore = scores['J']!!
fun typeForHand(cards: String): HandTypes {
    val counts = cards.groupingBy { it }.eachCount().values.sortedDescending()
    if (counts[0] == 5) { return HandTypes.FIVE_KIND }
    val (c1, c2) = counts
    return if (c1 == 4) { HandTypes.FOUR_KIND }
    else if (c1 == 3 && c2 == 2) { HandTypes.FULL_HOUSE }
    else if (c1 == 3) { HandTypes.THREE_KIND }
    else if (c1 == 2 && c2 == 2) { HandTypes.TWO_PAIR }
    else if (c1 == 2) { HandTypes.ONE_PAIR }
    else HandTypes.HIGH_CARD
}

fun wildTypeForHand(cards: String): HandTypes {
    val counts = cards.groupingBy { it }.eachCount()
    val jCount = counts['J'] ?: 0
    return if (jCount in 1..4) {
        val highest = (counts - 'J').entries.maxByOrNull { it.value }!!.key
        typeForHand(cards.replace('J', highest))
    } else {
        typeForHand(cards)
    }
}

fun String.asListOfScores() = map { scores[it]!! }
fun sortHands(a: String, b: String) = a.asListOfScores().zip(b.asListOfScores()).map { compareValues(it.first, it.second) }.first { it != 0 }


class Hand(val cards: String, val bid: Long): Comparable<Hand> {
    val type = typeForHand(cards)

    val sortScore = cards.asListOfScores()
        .fold(0) { acc, x -> (acc * 16) + x}



    override fun compareTo(other: Hand): Int = (compareByDescending<Hand> { it.type }.thenBy { it.sortScore }).compare(this, other)
    override fun toString(): String {
        return "Hand(cards='$cards', bid=$bid, type=$type)"
    }

}


class Hand2(val cards: String, val bid: Long): Comparable<Hand2> {
    val type = wildTypeForHand(cards)

    val sortScore = cards.asListOfScores()
        .map { if (it == jScore) { 0 } else { it + 1 } }
        .fold(0) { acc, x -> (acc * 16) + x}



    override fun compareTo(other: Hand2): Int = (compareByDescending<Hand2> { it.type }.thenBy { it.sortScore }).compare(this, other)
    override fun toString(): String {
        return "Hand(cards='$cards', bid=$bid, type=$type)"
    }

}


fun main() {
    val testInput = """32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483""".trimIndent().split("\n")


    fun part1(input: List<String>): Long {
        val hands = input.map { Hand(it.substringBefore(' '), it.substringAfter(' ').toLong()) }
        return hands
            .sorted()
            .mapIndexed { index, hand -> index + 1 to hand.bid }
            .onEach { println(it) }
            .sumOf { it.first * it.second }

    }


    fun part2(input: List<String>): Long {
        val hands = input.map { Hand2(it.substringBefore(' '), it.substringAfter(' ').toLong()) }
        return hands
            .sorted()
            .mapIndexed { index, hand -> index + 1 to hand.bid }
            .onEach { println(it) }
            .sumOf { it.first * it.second }

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 6440L)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 7)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
