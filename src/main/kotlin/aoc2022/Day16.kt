package aoc2022

import utils.InputUtils
import aoc2022.utils.MazeToTarget
import java.lang.Integer.min


val parseValve = "Valve (\\S+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)".toRegex()

data class LocationAndBusy(val valve: Valve, val time: Int)

data class Valve(val id: String, val rate: Int, val links: List<String>)

class Tunnels(private val valves: Map<String, Valve>) {
    fun distance(from: Valve, to: Valve) : Int {
        return distances(listOf(from), to).first().second
    }
    fun distances(froms: List<Valve>, to: Valve) : List<Pair<Valve, Int>> {
        val maze = MazeToTarget(to) { pos -> pos.links.map { valves[it]!!}}

        return froms.map {
            it to maze.distanceFrom(it)
        }
    }

    fun calculateDistances(
        nonZeroValves: List<Valve>,
        start: Valve
    ): HashMap<Pair<String, String>, Int> {
        val distances = HashMap<Pair<String, String>, Int>()
        for (v in nonZeroValves) {
            distances(nonZeroValves + listOf(start), v).forEach { (valve, distance) ->
                distances[valve.id to v.id] = distance - 1
            }
        }
        return distances
    }

}

fun main() {
    val testInput = """Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II""".split("\n")

    fun best_very_slow(start: Valve, valves: Map<String, Valve>, time: Int): Int {
        if (time <= 0) return 0
        return sequence {
            if (start.rate > 0) {
                val newVales = valves + (start.id to start.copy(rate=0))

                start.links.forEach { linkId ->
                   yield((start.rate * (time-1)) +  best_very_slow(valves[linkId]!!, newVales, time - 2))
                }
            }
            else {
                start.links.forEach { linkId ->
                    yield( best_very_slow(valves[linkId]!!, valves, time - 1))
                }
            }

        }.max()
    }

    fun best(start: Valve, nonZeroValves: Set<Valve>, distances: Map<Pair<String, String>, Int>, time: Int): Int {
        return nonZeroValves.maxOfOrNull {
            val timeTaken = distances[start.id to it.id]!!
            val newTime = time - (timeTaken + 1)
            if (newTime > 0)
                (newTime * it.rate) + best(it, nonZeroValves - it, distances, newTime)
            else 0
        } ?: 0
    }



    fun bestTwoPeople(positions: List<LocationAndBusy>,
                      nonZeroValves: Set<Valve>, distances: Map<Pair<String, String>, Int>, time: Int): Int {
        val (start1, start2) = positions.sortedBy { it.time }
        if (start1.time == 0) {
            return nonZeroValves.maxOfOrNull {
                val timeTaken = distances[start1.valve.id to it.id]!! + 1
                val endTime = time - timeTaken
                val timeStep = min(timeTaken, start2.time)
                val newTime = time - timeStep
                if (endTime > 0)
                    (endTime * it.rate) + bestTwoPeople(
                        listOf(
                            LocationAndBusy(it, timeTaken - timeStep),
                            start2.copy(time = start2.time - timeStep)
                        ), nonZeroValves - it, distances, newTime
                    )
                else 0
            } ?: 0
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val valves = input.parsedBy(parseValve) {
            val (id, rate, links) = it.destructured
            Valve(id, rate.toInt(), links.split(", "))
        }.toList()
        val valveMap = valves.associateBy { it.id }

        val nonZeroValves = valves.filter { it.rate > 0 }


        val tunnels = Tunnels(valveMap)
        val start = valveMap["AA"]!!
        val distances = tunnels.calculateDistances(nonZeroValves, start)

        return best(start, nonZeroValves.toSet(), distances, 30)
    }

    fun part2(input: List<String>): Int {
        val valves = input.parsedBy(parseValve) {
            val (id, rate, links) = it.destructured
            Valve(id, rate.toInt(), links.split(", "))
        }.toList()
        val valveMap = valves.associateBy { it.id }

        val nonZeroValves = valves.filter { it.rate > 0 }


        val tunnels = Tunnels(valveMap)
        val start = valveMap["AA"]!!
        val distances = tunnels.calculateDistances(nonZeroValves, start)

        return bestTwoPeople(listOf(LocationAndBusy(start, 0), LocationAndBusy(start, 0)), nonZeroValves.toSet(), distances, 26)
    }



    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 1651)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 16).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

