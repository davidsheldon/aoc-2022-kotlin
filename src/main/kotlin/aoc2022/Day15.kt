package aoc2022

import utils.InputUtils
import utils.Bounds
import utils.Coordinates
import kotlin.math.absoluteValue
import kotlin.system.measureTimeMillis


val parseSensor = "Sensor at x=([-\\d]+), y=([-\\d]+): closest beacon is at x=([-\\d]+), y=([-\\d]+)".toRegex()

class Sensor(val pos: Coordinates, val beacon: Coordinates) {
    val distance = (beacon - pos).length()
    fun projectToRow(y: Int): IntRange? {
        val distanceToRow = (pos.y - y).absoluteValue
        val projectedWidth = distance - distanceToRow
        if (projectedWidth < 0) { return null }
        return IntRange(pos.x - projectedWidth, pos.x + projectedWidth)
    }

    fun covers(other: Coordinates) =
        (other - pos).length() <= distance

    fun justOutOfRange(): Sequence<Coordinates> = sequence {
        val extent = distance + 1
        for(x in 0..extent) {
            yield(pos + Coordinates(x, extent - x))
            yield(pos + Coordinates(x, x- extent ))
            yield(pos + Coordinates(-x, extent - x))
            yield(pos + Coordinates(-x, x- extent ))
        }
    }
}


fun main() {
    val testInput = """Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3""".split("\n")


    fun part1(input: List<String>, row: Int = 10): Int {
        val sensors = input.parsedBy(parseSensor) {
            val (x1, y1, x2, y2) = it.destructured.toList().map { it.toInt() }
            Sensor(Coordinates(x1, y1), Coordinates(x2, y2))
        }
        val ret: Int
        val time = measureTimeMillis {

            val beacons = sensors.filter { it.beacon.y == row }.map { it.beacon.x }.toSet()
            val ranges: List<IntRange>
            val projectTime = measureTimeMillis{
            ranges = sensors
                .map { it.projectToRow(row) }
                .filterNotNull().toList()
            }

            println("Projecting took $projectTime ms")

            val sorted = ranges.sortedBy { it.first }
            var x = sorted[0].first
            var count = 0
            for (range in sorted) {
                if (x < range.first) x = range.first
                    while(x<=range.last) {
                        if (x !in beacons) count++
                        x++
                    }
            }
            ret = count
        }
        println("Took $time ms")
        return ret
    }


    fun part2(input: List<String>, size: Int = 20): Long {
        val sensors = input.parsedBy(parseSensor) {
            val (x1, y1, x2, y2) = it.destructured.toList().map { it.toInt() }
            Sensor(Coordinates(x1, y1), Coordinates(x2, y2))
        }

        val bounds = Bounds(Coordinates(0,0), Coordinates(size, size))
        val beacon = sensors.flatMap { it.justOutOfRange() }
            .filter { it in bounds }
            .filter { sensors.none { sensor -> sensor.covers(it) } }.first()
        println(beacon)
        return beacon.y + (beacon.x * 4_000_000L)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 26)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 15).toList()


    println(part1(puzzleInput, 2000000))
    println(part2(testInput))
    println(part2(puzzleInput,4000000))
}

