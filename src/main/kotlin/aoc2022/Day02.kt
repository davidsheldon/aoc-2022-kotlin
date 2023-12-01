package aoc2022

import utils.InputUtils

enum class Action(val score : Int) { ROCK(1), PAPER(2), SCISSORS(3) }

private val parseLine = "(\\w) (\\w)".toRegex()
typealias Play = Pair<Action, Action>

fun main() {
    val testInput = """A Y
B X
C Z""".split("\n")

    fun Char.toAction() = when (this) {
        'A','X' -> Action.ROCK
        'B','Y' -> Action.PAPER
        'C','Z' -> Action.SCISSORS
        else -> throw IllegalArgumentException("Unexpected char $this")
    }

    fun Action.beats(): Action =
        when (this) {
            Action.ROCK -> Action.SCISSORS
            Action.PAPER -> Action.ROCK
            Action.SCISSORS -> Action.PAPER
        }

    fun Action.beatenBy(): Action =
        when (this) {
            Action.ROCK -> Action.PAPER
            Action.PAPER -> Action.SCISSORS
            Action.SCISSORS -> Action.ROCK
        }



    fun Action.beats(other: Action): Boolean = beats() == other

    fun scoreFight(opp: Action, my: Action): Int {
        if (opp == my) return 3
        if (my.beats(opp)) return 6
        return 0
    }

    fun scorePlay(their: Action, my: Action) = scoreFight(their, my) + my.score

    fun <T> List<T>.toPair() = Pair(this[0], this[1])

    fun Play.score() = scorePlay(first, second)

    fun parseLines(input: List<String>) = input.parsedBy(parseLine).map { match -> match.destructured.toList().map { it[0]}.toPair() }

    fun part1(input: List<String>): Int {
        return parseLines(input).map {
            Play(it.first.toAction(), it.second.toAction())
        }.map(Play::score).sum()
    }

    fun part2(input: List<String>): Int {

        return parseLines(input).map { line ->
            val theirAction = line.first.toAction()
            val myAction = when (line.second) {
                'X' -> theirAction.beats()
                'Y' -> theirAction
                else -> theirAction.beatenBy()
            }
            Play(theirAction, myAction)
        }.map(Play::score).sum()
    }



    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 15)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 2)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
