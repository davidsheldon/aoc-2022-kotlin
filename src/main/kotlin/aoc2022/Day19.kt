package aoc2022

import utils.InputUtils

private val bluePrintRegex = "Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

data class BluePrint(
    val id: Int,
    val oreCost: Int,
    val clayCost: Int,
    val obsidianOreCost: Int,
    val obsidianClayCost: Int,
    val geodeOreCost: Int,
    val geodeObsidianCost: Int
) {
    override fun toString(): String = "Blueprint $id: Each ore robot costs $oreCost ore. Each clay robot costs $clayCost ore. Each obsidian robot costs $obsidianOreCost ore and $obsidianClayCost clay. Each geode robot costs $geodeOreCost ore and $geodeObsidianCost obsidian."

    val geodeRobot = Resources(ore = geodeOreCost, obsidian = geodeObsidianCost)
    val obsidianRobot = Resources(ore = obsidianOreCost, clay = obsidianClayCost)
    val robots = mapOf(
        Resources(ore = oreCost) to Resources(ore = 1),
        Resources(ore = clayCost) to Resources(clay = 1),
        obsidianRobot to Resources(obsidian = 1),
        geodeRobot to Resources(geode = 1),
        Resources() to Resources() // Do nothing
    )

    fun run(mine: Mine, turns: Int) : Sequence<Mine> {
        if (turns == 0) return sequenceOf(mine)
        var possibleRobots = robots
            .filter { (cost, _) -> mine.goods.canAfford(cost) }
            .filter { (_, generates) -> turns > 5 || !(generates.ore > 0 || generates.clay > 0) }

        if (geodeRobot in possibleRobots) { possibleRobots = possibleRobots.filterKeys { it == geodeRobot } }
        if (obsidianRobot in possibleRobots) {possibleRobots = possibleRobots.filterKeys { it == obsidianRobot }}

        return possibleRobots.asSequence().flatMap { (cost, generates) ->
            val goods = mine.goods + mine.robots - cost

            run(Mine(robots = mine.robots + generates, goods = goods), turns - 1)
        }
    }

}

fun createBluePrint(matchResult: MatchResult): BluePrint {
    val values = matchResult.groupValues.drop(1).map { it.toInt() }
    val (id, oreCost, clayCost, obsidianOreCost, obsidianClayCost) = values
    val (geodeOreCost, geodeObsidianCost) = values.drop(5)
    return BluePrint(id, oreCost, clayCost, obsidianOreCost, obsidianClayCost, geodeOreCost, geodeObsidianCost)
}

data class Resources(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
) {
    operator fun plus(other: Resources): Resources = Resources(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)
    operator fun minus(other: Resources): Resources = Resources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
    fun canAfford(cost: Resources): Boolean {
        return ore >= cost.ore && clay >= cost.clay && obsidian >= cost.obsidian && geode >= cost.geode
    }
}

data class Mine(
    val robots: Resources,
    val goods: Resources,
)

fun main() {
    val testInput = """Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.""".split("\n")


    fun runBluePrint(bluePrint: BluePrint, turns: Int): Int {
        val initialMine = Mine(robots = Resources(ore = 1), goods = Resources())

        return bluePrint.run(initialMine, turns).map { it.goods.geode }.max() * bluePrint.id
    }

    fun part1(input: List<String>): Int {
        return input.parsedBy(bluePrintRegex).map(::createBluePrint).map {
            runBluePrint(it, 24)
        }.onEach { println(it) }.sum()
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 33)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 19).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

