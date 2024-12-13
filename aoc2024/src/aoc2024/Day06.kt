package aoc2024

import utils.*

data class Guard(val loc: Coordinates, val direction: Direction) {
    fun walk() = loc.heading(direction)
    fun inFront(map: Map06) = map.checkedAt(loc.move(direction), ' ')
    fun move() = Guard(loc.move(direction), direction)
    fun turn() = Guard(loc, direction.turnRight())
}

class Map06(val input: List<String>): ArrayAsSurface(input) {
    fun start(): Coordinates = find { c -> c == '^'}

    fun withObstacleAt(loc: Coordinates): Map06 = Map06(replace(loc, '#').points)
}

fun main() {

    val testInput = """....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...""".trimIndent().split("\n")

    class LoopException: Exception()

    fun walkFrom(map: Map06, start: Coordinates): Sequence<Guard> {
        val seen = mutableSetOf<Guard>()
        return generateSequence(Guard(start, N)) { guard ->
            when(guard.inFront(map)) {
                '#' -> guard.turn()
                ' ' -> null;
                else -> guard.move()
            }
        }.onEach { guard ->
            if (seen.contains(guard)) {
                throw LoopException()
            }
            seen.add(guard)
        }
    }

    fun walkLengthFrom(map: Map06, start: Coordinates): Int {
        return try {
            walkFrom(map, start).distinctBy { it.loc }.count()
        } catch (e: LoopException) {
            -1
        }
    }


    fun part1(input: List<String>): Long {
        val map = Map06(input)
        return walkLengthFrom(map, map.start()).toLong()
    }


    fun part2(input: List<String>): Long {
        val map = Map06(input)

        val start = map.start()

        return walkFrom(map, start).drop(1).filter {
            val len = walkLengthFrom(map.withObstacleAt(it.loc), start)
            len == -1
        }.distinctBy { it.loc}.count().toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 41L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 6)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
