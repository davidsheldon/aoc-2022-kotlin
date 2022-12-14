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
        return (tl.y..br.y).map { y ->
            (tl.x..br.x).map { x ->
                state[Coordinates(x,y)] ?: '.'
            }.joinToString("")
        }.joinToString("\n")
    }

    fun dropSand(coordinates: Coordinates): Boolean {
        if (coordinates.y > maxY) return false
        val nextMove = listOf(coordinates.dY(1), coordinates.dY(1).dX(-1), coordinates.dY(1).dX(1))
            .firstOrNull { it !in state }
        if (nextMove == null) {
            state[coordinates] = 'o'
            return true
        }
        else {
            return dropSand(nextMove)
        }
    }
}


fun main() {
    val testInput = """498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9""".split("\n")





    fun part1(input: List<String>): Int {
        val cave = input.map { line ->
            line.splitToSequence(" -> ")
                .map(String::toCoordinates)
                .zipWithNext { a, b ->
                    a.lineTo(b)
                }.flatten()
        }.flatMap { it }.associateWith { '#' }
            .let { Cave(it) }

        var count = 0
        while(cave.dropSand(Coordinates(500, 0))) {
count++;
        }

        println(cave)

        return count
    }


    fun part2(input: List<String>): Int {
        return input.size
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

