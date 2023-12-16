package aoc2023

import utils.*

private data class Beam(val loc: Coordinates, val heading: Direction) {
    fun move(dir: Direction) = copy(loc=loc.move(dir), heading=dir)
}
private class Day16(input: List<String>): ArrayAsSurface(input) {

    fun rotate90_1(dir: Direction) = when(dir) {
        E -> N
        N -> E
        S -> W
        W -> S
    }
    fun rotate90_2(dir: Direction) = when(dir) {
        E -> S
        S -> E
        N -> W
        W -> N
    }
    fun nextBeams(current: Beam, seen: Set<Beam>): List<Beam> {
        val dir = current.heading
        val next = when (checkedAt(current.loc, 'X')) {
            '.' -> listOf(current.move(dir))
            '/' -> listOf(current.move(rotate90_1(dir)))
            '\\' -> listOf(current.move(rotate90_2(dir)))
            '|' -> when(dir) {
                N, S -> listOf(current.move(dir))
                E, W -> listOf(current.move(N), current.move(S))
            }
            '-' -> when(dir) {
                N, S -> listOf(current.move(E), current.move(W))
                E, W -> listOf(current.move(dir))
            }

            else -> emptyList()
        }

        return next.filter { it !in seen && inBounds(it.loc)}
    }

    fun allBeams(start: Beam): MutableSet<Beam> {
        var currentBeams = listOf(start)
        val seen = mutableSetOf(start)

        while (currentBeams.isNotEmpty()) {
            val nextBeams = currentBeams.flatMap { nextBeams(it, seen) }
            seen.addAll(nextBeams)
            currentBeams = nextBeams
        }
        return seen
    }

    private fun symbolFor(beams: List<Beam>): Char {
        return when(beams.size) {
            0 -> '.'
            1 -> when(beams[0].heading) {
                N -> '^'
                S -> 'v'
                E -> '>'
                W -> '<'
            }
            else -> '0' + beams.size
        }
    }

    fun starts() = (0..<getWidth()).flatMap { listOf(Beam(Coordinates(it,0),S),Beam(Coordinates(it,getHeight() - 1),N)) } +
            (0..getHeight()).flatMap { listOf(Beam(Coordinates(0,it),E),Beam(Coordinates(getWidth() -1,it),W)) }
    fun printBeams(beams: Set<Beam>) {
        rows().forEach {row ->
            println(row.map { loc ->
                val at = at(loc)
                when(at) {
                    '.' -> symbolFor(beams.filter { it.loc == loc })
                    else -> at
                }
            }.joinToString(""))
        }
    }

    fun printEnergised(beams: Set<Beam>) {
        val coordinates = beams.map { it.loc }
            .filter { inBounds(it) }.toSet()
        println(coordinates.size)
        rows().forEach {row ->
            println(row.map { loc ->
                if (loc in coordinates) '#' else '.'
            }.joinToString(""))
        }
    }

}


fun main() {
    val testInput = """.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        val start = Beam(Coordinates(0, 0), E)
        val problem = Day16(input)
        val beams = problem
            .allBeams(start)
//        problem.printBeams(beams)
//        println()
//        problem.printEnergised(beams)
        return beams
            .distinctBy { it.loc }
            .count()
    }


    fun part2(input: List<String>): Int {
        val problem = Day16(input)
        return problem.starts().map { start ->
            val beams = problem
                .allBeams(start)
            beams
            .distinctBy { it.loc }
            .count() }.max()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 46)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 16)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
