package aoc2023

import utils.*

class EngineSchematic(val parts: List<String>): ArrayAsSurface(parts) {
    fun symbols() = indexed()
        .filter { it.second.isSymbol() }

    fun numbersTouching(c: Coordinates): Sequence<Int> =
         c.adjacentIncludeDiagonal()
             .filter { at(it).isDigit() }
            .map { expandNumber(it) }
            .distinct()
            .map { it.second }


    fun expandNumber(initial: Coordinates): Pair<Coordinates, Int> {
        val start = initial.heading(W).takeWhile { checkedAt(it).isDigit() }.last()
        val number = start.heading(E)
            .map { checkedAt(it)}
            .takeWhile { it.isDigit() }
            .joinToString("")
            .toInt()
        return Pair(start, number)
    }

    fun Char.isSymbol() = !(isDigit() || (this == '.'))
}

fun main() {
    val testInput = """467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...${'$'}.*....
.664.598..""".trimIndent().split("\n")


    fun part1(input: List<String>): Int {
        val schematic = EngineSchematic(input)
        return schematic.symbols()
            .flatMap { schematic.numbersTouching(it.first) }
            .sum()
    }


    fun part2(input: List<String>): Int {
        val schematic = EngineSchematic(input)
        return schematic.symbols()
            .filter { it.second == '*' }
            .map {
                val numbers = schematic.numbersTouching(it.first).toList()
                if (numbers.size == 2) {
                    numbers[0] * numbers[1]
                } else {
                    0
                }

            }
            .sum()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 4361)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 3)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
