package aoc2021

import aoc2022.parsedBy
import utils.InputUtils
import utils.Coordinates

private sealed class Command {
    data class Up(val distance: Int) : Command()
    data class Down(val distance: Int) : Command()
    data class Forward(val distance: Int) : Command()
}

private val parser = "(up|down|forward) (\\d+)".toRegex()

fun main() {
    val testInput = """forward 5
down 5
forward 8
up 3
down 8
forward 2""".split("\n")

    fun MatchResult.parseCommand() :Command {
        val command = groupValues[1]
        val distance = groupValues[2].toInt()
        return when (command) {
            "up" -> Command.Up(distance)
            "down" -> Command.Down(distance)
            "forward" -> Command.Forward(distance)
            else -> error(this)
        }
    }

    fun part1(input: List<String>): Int {
        return input.parsedBy(parser, MatchResult::parseCommand).fold(Coordinates()) { coord, command ->
            when (command) {
                is Command.Up -> coord.dY(-command.distance)
                is Command.Down -> coord.dY(command.distance)
                is Command.Forward -> coord.dX(command.distance)
            }
        }.let { it.x * it.y }
    }

    data class State(val pos: Coordinates = Coordinates(0, 0), val aim : Int = 0) {
        fun apply(command: Command) =
            when(command) {
                is Command.Up -> copy(aim = aim - command.distance)
                is Command.Down -> copy(aim = aim + command.distance)
                is Command.Forward -> copy(pos = pos.dX(command.distance).dY(command.distance * aim))
            }

    }

    fun part2(input: List<String>): Int =
        input.parsedBy(parser, MatchResult::parseCommand)
            .fold(State()) { state, command -> state.apply(command) }
            .let { it.pos.x * it.pos.y }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 150)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 2)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
