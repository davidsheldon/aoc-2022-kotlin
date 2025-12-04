package aoc2025

import utils.ArrayAsSurface
import utils.Coordinates
import utils.InputUtils
import kotlin.time.measureTime

fun main() {
        val testInput = """
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.""".trimIndent().split("\n")


    fun canMove(coord: Coordinates, map: ArrayAsSurface): Boolean =
        coord.adjacentIncludeDiagonal().count { map.checkedAt(it) == '@' } < 4

    fun part1(input: List<String>): Long {
        val map = ArrayAsSurface(input)
        return map.findAll { it == '@' }
            .filter { coord -> canMove(coord, map) }.count().toLong()

    }

    fun removeEntry(
        key: Coordinates,
        adjacent: MutableMap<Coordinates, MutableSet<Coordinates>>,
    ) {
        val adjacency = adjacent.remove(key) ?: return
        adjacency.forEach { k ->
            val entry = adjacent[k]
            if (entry != null) {
                entry.remove(key)
                if (entry.size < 4) {
                    removeEntry(k, adjacent)
                }
            }
        }
        return
    }

    fun part2(input: List<String>): Long {
        val map = ArrayAsSurface(input)
        val rolls = map.findAll { it == '@' }.toSet()
        val adjacent =
            rolls.associateWith { it.adjacentIncludeDiagonal().filter { c -> rolls.contains(c) }.toMutableSet() }.toMutableMap()

        do {
            val toRemove = adjacent.entries.firstOrNull { (_, v) -> v.size < 4 }
            if (toRemove == null) break;

            removeEntry(toRemove.key, adjacent)
//            println("Removed ${toRemove.key}, ${adjacent.size} remaining")
        } while (true)
        return rolls.size - adjacent.size.toLong()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 13L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 4)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
