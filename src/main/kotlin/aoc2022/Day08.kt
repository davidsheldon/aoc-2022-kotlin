package aoc2022

import utils.InputUtils


fun main() {
    val testInput = """30373
25512
65332
33549
35390""".split("\n")

    fun countTrue(it: BooleanArray) = it.count { b -> b }

    fun extracted(values: Iterable<IndexedValue<Char>>): Sequence<Int> {
        return sequence {
            var max: Char = 0.toChar()
            for ((x, c) in values) {
                if (c > max) {
                    max = c; yield(x)
                }
            }
        }
    }

    fun columns(input: List<String>): List<IndexedValue<List<Char>>> =
        input[0].indices.map { index -> IndexedValue(index, input.map { it[index] }) }

    fun List<Char>.countUntilFirst(target: Char): Int {
        forEachIndexed { i, c -> if (c >= target) return i + 1 }
        return size
    }

    fun scenicScore(
        input: List<String>,
        y: Int,
        x: Int,
        width: Int,
        height: Int
    ): Int {
        val house = input[y][x]

        val line1 = (x + 1 until width).map { input[y][it] }.countUntilFirst(house)
        val line2 = (0 until x).reversed().map { input[y][it] }.countUntilFirst(house)
        val line3 = (y + 1 until height).map { input[it][x] }.countUntilFirst(house)
        val line4 = (0 until y).reversed().map { input[it][x] }.countUntilFirst(house)
        val score = line1 * line2 * line3 * line4
        return score
    }


    fun part1(input: List<String>): Int {
        val visible = Array(input.size) { i -> BooleanArray(input[i].length) }

        for ((y, row) in input.withIndex()) {
            extracted(row.withIndex()).forEach { visible[y][it] = true }
            extracted(row.withIndex().reversed()).forEach { visible[y][it] = true }
        }
        for ((x, col) in columns(input)) {
            extracted(col.withIndex()).forEach { visible[it][x] = true }
            extracted(col.withIndex().reversed()).forEach { visible[it][x] = true }
        }

        println(visible.joinToString("\n") { row -> row.map { if (it) '1' else '.' }.toCharArray().concatToString() })
        return visible.sumOf(::countTrue)
    }


    fun part2(input: List<String>): Int {
        val width = input[0].length
        val height = input.size
        return sequence {
            for (x in 1 until width - 1) {
                for (y in 1 until height - 1) {
                    val score = scenicScore(input, y, x, width, height)
                    yield(score)
                }
            }
        }.max()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 21)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 8).toList()


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
