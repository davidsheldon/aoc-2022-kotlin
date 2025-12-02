package aoc2024

import utils.InputUtils


fun main() {

    val testInput = """#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####""".trimIndent().split("\n")


    open class LK(heights: List<Int>) {}
    data class Lock(val heights: List<Int>): LK(heights) {}
    data class Key(val heights: List<Int>): LK(heights) {}

    fun part1(input: List<String>): Long {
        val all = input.toBlocksOfLines().map { block ->
            val heights = block[0].indices.map { i ->
                block.count { it[i] == '#' } - 1
            }
            if (block[0].all { it == '#' }) { // lock
                Lock(heights)
            } else Key(heights)
        }
        println(all)
        val locks = all.filterIsInstance<Lock>()
        val keys = all.filterIsInstance<Key>()

        return locks.sumOf { lock ->
            keys.count { key ->
               lock.heights.indices.all { i -> lock.heights[i] + key.heights[i] <= 5 }
            }
        }.toLong()
    }


    fun part2(input: List<String>): Long {
        return part1(input)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 3L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 25)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
