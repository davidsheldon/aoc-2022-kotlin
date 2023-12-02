package aoc2023

import utils.InputUtils

enum class Colours {
    red, blue, green
}

typealias BallCount = Map<Colours, Int>

fun BallCount.fitsIn(limits: BallCount) = limits.all { (col, limit) ->
    (this[col] ?: 0) <= limit
}

data class Game(val id: Int, val contents: List<BallCount>) {
    fun fitsIn(limits: BallCount): Boolean = contents.all { it.fitsIn(limits) }
    fun minimums(): BallCount {
        return Colours.entries.associateWith { colour ->
            contents.maxOf { it[colour] ?: 0 }
        }
    }
}

fun String.parseSample(): BallCount {
    return this.split(", ").parsedBy("^(\\d+) (\\w+)".toRegex()) {
        val (count, col) = it.destructured
        Colours.valueOf(col) to count.toInt()
    }.toMap()
}

fun main() {
    val testInput = """Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green""".split("\n")

    val parseSample = "((\\d+) (red|green|blue)(, )?)+".toRegex()
    val parseSensor = "Game ([\\d]+): (($parseSample(; )?)+)".toRegex()


    fun List<String>.toGames() = parsedBy(parseSensor) { match ->
        val (id, sample) = match.destructured
            Game(id.toInt(), sample.split("; ").map { it.parseSample() })
        }

    fun part1(input: List<String>): Int {
        return input.toGames()
            .filter { it.fitsIn(mapOf(
            Colours.red to 12, Colours.green to 13, Colours.blue to 14
        )) }
            .sumOf { it.id }
    }


    fun part2(input: List<String>): Int {
        return input.toGames()
            .map { it.minimums() }
            .map { it.values.product() }
            .sum()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 8)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 2)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
