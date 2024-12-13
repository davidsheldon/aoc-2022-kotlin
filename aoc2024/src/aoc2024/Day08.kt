package aoc2024

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils

private class City(input: List<String>): ArrayAsSurface(input) {
    fun getTransmitters(): Map<Char, List<Coordinates>> = indexed()
        .filter { (_, c) -> c.isLetter() || c.isDigit() }
        .groupBy({ it.second }, { it.first })

}


fun main() {

    val testInput = """............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val city = City(input)
        val transmitters = city.getTransmitters()

        return transmitters.flatMap { (_,locs) ->
            val ret = allPairs(locs)
                .filter { it.first != it.second }
                .flatMap { (a,b) ->
                val diff = a.minus(b)
                listOf(a.plus(diff), b.minus(diff))
            }.filter { loc -> city.inBounds(loc) }.toList()
            ret
        }
            .distinct()
            .count().toLong()


    }


    fun part2(input: List<String>): Long {
        val city = City(input)
        val transmitters = city.getTransmitters()

        return transmitters.flatMap { (c,locs) ->
            val ret = allPairs(locs)
                .filter { it.first != it.second }
                .flatMap { (a,b) ->
                    val diff = a.minus(b).normalise()
                    generateSequence(a) { it.plus(diff) }
                        .takeWhile { coordinates -> city.inBounds(coordinates) } +
                    generateSequence(a) { it.minus(diff) }
                        .takeWhile { coordinates -> city.inBounds(coordinates) }
                }.toList()
            ret
        }
            .distinct()
            .count().toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 14L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 8)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}

