package aoc2025

import aoc2024.allPairs
import aoc2024.size
import utils.Coordinates
import utils.InputUtils
import utils.boundingBox
import utils.toCoordinates
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime
typealias Edge = Pair<Coordinates, Coordinates>
typealias Scanlines = Map<Int, List<LongRange>>

fun main() {

    val testInput = """
7,1
11,1
11,7
9,7
9,5
2,5
2,3
7,3""".trimIndent().split("\n")

    fun Pair<Coordinates, Coordinates>.area(): Long =
        first.minus(second).let { diff -> (diff.x.absoluteValue + 1).toLong() * (diff.y.absoluteValue + 1) }

    fun Scanlines.contains(c: Coordinates): Boolean =
        (this[c.y]?: emptyList()).any { it.contains(c.x) }

    fun Edge.isVertical() = first.x == second.x
    fun Edge.isHorizontal() = first.y == second.y

    fun Scanlines.containsAllPoints(edge: Edge): Boolean {
        return if (edge.isHorizontal()) {
            val line = this[edge.first.y]!!
            val size = line.sumOf { it.size() }
            val edgeSize = (line.asSequence() + sequenceOf(edge.first.x.toLong()..edge.second.x)).simplify().sumOf { it.size() }
            size == edgeSize
        } else {
            edge.first.lineTo(edge.second).all { !this.contains(it.y) || this.contains(it) }
        }
    }
    fun Pair<Coordinates, Coordinates>.edgePoints(): Sequence<Coordinates> {
        val tl = Coordinates(min(first.x, second.x), min(first.y, second.y))
        val br = Coordinates(max(first.x, second.x), max(first.y, second.y))
        val bl = Coordinates(tl.x, br.y)
        val tr = Coordinates(br.x, tl.y)
        return tl.lineTo(tr) + tr.lineTo(br) + br.lineTo(bl) + bl.lineTo(tl)
    }
    fun Pair<Coordinates, Coordinates>.edges(): List<Edge> {
        val tl = Coordinates(min(first.x, second.x), min(first.y, second.y))
        val br = Coordinates(max(first.x, second.x), max(first.y, second.y))
        val bl = Coordinates(tl.x, br.y)
        val tr = Coordinates(br.x, tl.y)
        return listOf(
            tl to tr,
            bl to br,
            tr to br,
            bl to tl
        )
    }



    fun part1(input: List<String>): Long {
        val points = input.map { it.toCoordinates() }

        val bb = points.boundingBox()

        return points.allPairs().map { pair -> pair.area() }.max()
    }

    fun part2(input: List<String>): Long {
        val points = input.map { it.toCoordinates() }

        val edges = points.zipWithNext() + listOf(points.last() to points.first())

        val activeEdges = mutableSetOf<Edge>()

        val possibleY = points.map { it.y }.toSet().toList().sorted()

        val scanlines = possibleY.associateWith { y ->
            activeEdges += edges.filter { min(it.first.y, it.second.y) == y }

            val lines = mutableListOf<LongRange>()
            var lastX = -1
            activeEdges.sortedBy { min(it.first.x, it.second.x) }.forEach { edge ->
                val (x1, x2) = listOf(edge.first.x, edge.second.x).sorted()
                if (lastX > -1 && lastX < x1) {
                    lines.add(lastX.toLong()..x1.toLong())
                }
                if (edge.isHorizontal()) {
                    lines.add(x1.toLong()..x2.toLong())
                }
                lastX = x2
            }
            val toRemove = activeEdges.filter { max(it.first.y, it.second.y) == y }.toSet()
            activeEdges.removeAll(toRemove)
            lines.asSequence().simplify().toList()
        }
        if (input.size < 100) {
            println(scanlines)
        }

        return points.allPairs().sortedBy { pair -> -pair.area() }//.take(100)
            .first { pair ->
                pair.edges().all { edge -> scanlines.containsAllPoints(edge) }
            }
            .area()

    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    println(part2(testInput))
    check(testValue == 50L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 9)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input)) })
    println(measureTime { println(part2(input)) })

}
