package aoc2022

import aoc2022.utils.InputUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import utils.Coordinates
import utils.boundingBox
import utils.toCoordinates

class Cave(initialState: Map<Coordinates, Char>) {
    val state = initialState.toMutableMap()
    val maxY = state.keys.maxOf { it.y }
    override fun toString(): String {
        val (tl, br) = state.keys.toList().boundingBox()
        return (tl.y..br.y)
            .joinToString("\n") { y ->
            (tl.x..br.x).map { x ->
                state[Coordinates(x, y)] ?: '.'
            }.joinToString("")
        }
    }

    private val newPositions = listOf(Coordinates(0, 1), Coordinates(-1, 1), Coordinates(1,1))

    private fun fallFrom(start: Coordinates) = generateSequence(start) { last ->
        newPositions.map { last + it }.firstOrNull { it !in state }
    }.takeWhile { it.y < maxY + 2 }.last()

    fun dropSand(coordinates: Coordinates): Boolean {
        val end = fallFrom(coordinates)
        if (end.y <= maxY) { state[end] = 'o'; return true;}
        return false
    }

    fun dropSand2(coordinates: Coordinates) {
        state[fallFrom(coordinates)] = 'o'
    }
}


fun main() {
    val testInput = """498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9""".split("\n")


    fun getCave(input: List<String>) = input.map { line ->
        line.splitToSequence(" -> ")
            .map(String::toCoordinates)
            .zipWithNext { a, b ->
                a.lineTo(b)
            }.flatten()
    }.flatMap { it }.associateWith { '#' }
        .let { Cave(it) }

    val startPosition = Coordinates(500, 0)
    fun part1(input: List<String>): Int {
        val cave = getCave(input)

        var count = 0
        while(cave.dropSand(startPosition)) {
count++;
        }

        println(cave)

        return count
    }


    fun part2(input: List<String>): Int {
        val cave = getCave(input)

        var count = 0
        do {
            cave.dropSand2(startPosition)
            count++;

        } while (startPosition !in cave.state)

        println(cave)

        return count
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 24)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 14).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

private fun JsonNode.toComp(): Comp = when (this) {
    is ArrayNode -> toComp()
    is IntNode -> toComp()
    else -> throw UnsupportedOperationException("Unexpected node: $this")
}

private fun ArrayNode.toComp(): Comp {
    return Comp.Array(this.map { it.toComp() })
}
private fun IntNode.toComp(): Comp {
    return Comp.Integer(intValue())
}

