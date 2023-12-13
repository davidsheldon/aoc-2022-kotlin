package aoc2023

import utils.InputUtils


fun columns(input: List<String>): List<String> =
    input[0].indices.map { index -> String(input.map { it[index] }.toCharArray()) }


fun main() {
    val testInput = """#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#""".trimIndent().split("\n")


    fun findReflection(map: List<String>): List<Int> {
        val possibles = map.zipWithNext()
            .mapIndexed { index, pair -> index to pair }
            .filter { (_, x) -> x.first == x.second }
            .map { it.first }
        // n where the fold is between n and n+1
        val filtered = possibles.filter { it.downTo(0).zip(it + 1..<map.size).all { (a, b) -> map[a] == map[b] } }
        return filtered
    }

    fun findReflection2(map: List<String>, errors: Int = 0): List<Int> {
        return map.indices.filter {
            it.downTo(0).zip(it + 1..<map.size)
                .map { (a, b) -> map[a] to map[b]}
                .sumOf { (a, b) -> a.zip(b).count { (x,y) -> x != y } } == errors }
    }

    fun part1(input: List<String>): Int {
        return input.toBlocksOfLines()
            //.onEach { println(columns(it)) }
            .map { map ->
                100 * findReflection(map).sumOf { it+1 } + findReflection(columns(map)).sumOf { it+1}
            }
            .sum()
    }


    fun part2(input: List<String>): Int {
        return input.toBlocksOfLines()
            //.onEach { println(columns(it)) }
            .map { map ->
                100 * findReflection2(map,1).sumOf { it+1 } + findReflection2(columns(map),1).sumOf { it+1}
            }
            .sum()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 405)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 13)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
