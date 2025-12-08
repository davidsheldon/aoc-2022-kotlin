package aoc2025

import aoc2024.DisjointSets
import aoc2024.product
import utils.InputUtils
import utils.Point3d
import utils.toPoint3d
import kotlin.time.measureTime

fun main() {
        val testInput = """
162,817,812
57,618,57
906,360,560
592,479,940
352,342,300
466,668,158
542,29,236
431,825,988
739,650,466
52,470,668
216,146,977
819,987,18
117,168,530
805,96,715
346,949,466
970,615,88
941,993,340
862,61,35
984,92,344
425,690,689""".trimIndent().split("\n")
    fun Int.square(): Long = this.toLong()*this

    fun distance(a: Point3d, b: Point3d): Long =
        (a.x - b.x).square() + (a.y-b.y).square() + (a.z-b.z).square()


    fun parse(input: List<String>): Pair<List<Point3d>, List<Map.Entry<Pair<Point3d, Point3d>, Long>>> {
        val points = input.map { it.toPoint3d() }
        val distances: Map<Pair<Point3d, Point3d>, Long> = points.indices.flatMap { a ->
            (a + 1..<points.size).map { b ->
                (points[a] to points[b]) to distance(points[a], points[b])
            }
        }.toMap()

        val allJoints = distances.entries.sortedBy { it.value }
        return Pair(points, allJoints)
    }

    fun part1(input: List<String>, jointCount: Int): Long {
        val (points, allJoints) = parse(input)
        val joints = allJoints.take(jointCount).map { it.key }

        val clusters = DisjointSets<Point3d>()
        clusters.addAll(points)

        joints.forEach {
            //println("Joining $it")
            clusters.merge(it.first, it.second)
        }
//        println(clusters.toSets().map { it.size.toLong() }.sorted())
        return clusters.toSets().map { it.size.toLong() }.sorted().takeLast(3).product()

    }


    fun part2(input: List<String>): Long {
        val (points, allJoints) = parse(input)

        val clusters = DisjointSets<Point3d>()
        clusters.addAll(points)

        allJoints.map { it.key}.forEach {
            if (clusters.merge(it.first, it.second)) {
                if (clusters.toSets().size == 1) {
                    return it.first.x.toLong() * it.second.x
                }
            }
        }
        return -1L
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput, 10)
    println(testValue)
    println(part2(testInput))
    check(testValue == 40L)

    val puzzleInput = InputUtils.downloadAndGetLines(2025, 8)
    val input = puzzleInput.toList()

    println(measureTime { println(part1(input, 1000)) })
    println(measureTime { println(part2(input)) })

}
