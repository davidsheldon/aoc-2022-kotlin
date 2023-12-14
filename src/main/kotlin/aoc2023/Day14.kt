package aoc2023

import utils.ArrayAsSurface
import utils.Coordinates
import utils.Direction
import utils.E
import utils.InputUtils
import utils.N
import utils.S
import utils.W

private fun getRocks(input: List<String>) = ArrayAsSurface(input)
    .indexed()
    .filter { (_, c) -> c == 'O' }
    .map { it.first }.toSet()

private fun withoutRocks(input: List<String>) =
    ArrayAsSurface(input.map { it.replace('O', '.') })

class Day14Rocks(val surface: ArrayAsSurface, val rolling: Set<Coordinates>) {
    constructor(input: List<String>):
        this(withoutRocks(input), getRocks(input)) {
    }

    val height = surface.points.size

    fun weights() = rolling.map { height - it.y }.sum()

    private fun comparatorFor(dir: Direction): (Coordinates) -> Int = when(dir) {
        N -> { { it.y } }
        S -> { { -it.y } }
        W -> { { it.x } }
        E -> { { -it.x } }
    }
    fun tip(dir: Direction): Day14Rocks {
        val stopped = mutableSetOf<Coordinates>()


        rolling.sortedBy(comparatorFor(dir)).forEach {old ->
            stopped.add(old.heading(dir).takeWhile {
                !stopped.contains(it) && surface.checkedAt(it, '#') == '.'
            }.last())
        }
        return Day14Rocks(surface, stopped)
    }

    fun cycle() = tip(N).tip(W).tip(S).tip(E)

    override fun toString(): String {
        return surface.rows().map {row ->
            row.map { if (rolling.contains(it)) 'O' else surface.at(it) }
                .joinToString("")
        }.joinToString("\n")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Day14Rocks

        return rolling == other.rolling
    }

    override fun hashCode(): Int {
        return rolling.hashCode()
    }

}


fun main() {
    val testInput = """O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        return Day14Rocks(input).tip(N).weights()
    }


    fun part2(input: List<String>): Int {
        val seen = mutableMapOf<Day14Rocks, Int>()
        generateSequence(Day14Rocks(input)) { it.cycle() }
            .mapIndexed { i, r -> r to i }
            .forEach { (rocks, index) ->
                val old = seen.put(rocks, index)
                if (old != null) { // We have a loop
                    seen.put(rocks, old) // Put it back incase we need it
                    val loopSize = index - old
                    println("Loop: ${loopSize} elements from $old to $index")

                    val target = 1_000_000_000
                    val i = old + ((target - old) % loopSize)
                    return seen.entries.first { it.value == i }.key.weights()
                }
            }
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 136)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 14)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
