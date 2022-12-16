package aoc2021


import aoc2022.utils.InputUtils

typealias Int2d = Array<IntArray>
fun Int2d.copy() = map { it.clone() }.toTypedArray()
fun Int2d.countZeros() = sumOf { it.count { i -> i == 0 } }
fun Int2d.allZeros() = all { it.all { it == 0 } }
class Cave(val energy: Int2d) {

    fun step(): Cave {
        val copy = energy.map { it.map { o -> o + 1 }.toIntArray() }.toTypedArray()

        do {
            var flashes = 0
            for (y in copy.indices) {
                for (x in copy[y].indices) {
                    if (copy[y][x] > 9) {
                        flashes++
                        copy[y][x] = 0
                        flash(x, y, copy)
                    }
                }
            }
        } while (flashes > 0)

        return Cave(copy)
    }

    private fun flash(x: Int, y: Int, copy: Array<IntArray>) {
        for (dX in -1..1) {
            for (dY in -1..1) {
                val nY = y + dY
                val nX = x + dX
                if (nY in copy.indices &&
                    nX in copy[nY].indices &&
                    copy[nY][nX] != 0
                ) {
                    copy[nY][nX]++
                }
            }
        }
    }
}
fun List<String>.toCave() =  Cave(
    Array(size) { i ->
        get(i).let {
                row -> IntArray(row.length) { row[it].digitToInt() }
        } })

fun main() {
    val testInput = """5483143223
2745854711
5264556173
6141336146
6357385478
4167524645
2176841721
6882881134
4846848554
5283751526""".split("\n")


    fun part1(input: List<String>): Int {

        val cave = input.toCave()


        return (1..100).runningFold(cave) { acc, _ -> acc.step()}.sumOf { it.energy.countZeros() }
    }


    fun part2(input: List<String>): Int {
        return generateSequence(input.toCave(), Cave::step)
            .withIndex()
            .first { (_, cave) -> cave.energy.allZeros() }
            .index
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 1656)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 11)
    val input = puzzleInput.toList()

    println(part1(input))
    println("=== Part 2 ===")
    println(part2(testInput))
    println(part2(input))
}





