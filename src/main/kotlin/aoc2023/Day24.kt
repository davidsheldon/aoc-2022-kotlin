package aoc2023

import utils.InputUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

typealias MC = Pair<Double, Double>

operator fun MC.minus(other: MC) = MC(first-other.first, second-other.second)

class Day24 {
    data class PointLong(val x: Long, val y: Long, val z: Long) {
        operator fun minus(other: PointLong) = PointLong(x - other.x, y - other.y, z - other.z)
    }

    fun part1(input: List<String>, min: Long, max: Long): Int {
        val hailstones = parseHailstones(input)
        return allPairs(hailstones).count { (a, b) -> a.intersects2D(b, min, max)}
    }

    private fun parseHailstones(input: List<String>) = input.map { line ->
        val (start, vel) = line.split("@").map {
            val (x, y, z) = it.split(", ").map { it.trim().toLong() }
            PointLong(x, y, z)
        }
        Hailstone(start, vel)
    }

    fun part2(input: List<String>): Long {
        val hailstones = parseHailstones(input)

        // 6+n unknowns, 3n equations
        // need to eliminate cN, so we can solve
        // for the rest
        // 6 unknowns, 1.5n equations
        // 4 points? It should be doable with 2.

        // sx + c1 (vx - vx1) = x1
        // c1 = (x1-sx)/(vx-vx1)
        // sy + c1 vy = y1 + c1 vy1
        // c1 = (y1-sy)/(vy-vy1)

        // -> (y1-sy)/(vy-vy1) = (x1-sx)/(vx-vx1)
        // -> (y1-sy)(vx-vx1) = (x1-sx)(vy-vy1)

        // sz + c1 zx = z1 + c1 vz1

        // sx + c2 vx = x2
        // sy + c2 vy = y2
        // sz + c2 zx = z2

        // sx + c3 vx = x3
        // sy + c3 vy = y3
        // sz + c3 zx = z3

        // Or we can use the fact that we know an integer solution exists
        // velocity is likely to be up to 2x the max velocity of the stones


        return part2guessing(hailstones)

    }

    fun m(a: Long, b: Long) = max(abs(a), abs(b))
    private fun part2guessing(hailstones: List<Hailstone>): Long {
        // Use 2 examples, to derive location for a given speed.
        val (h1, h2) = hailstones
        val maxVelocity = hailstones.map { it.velocity }.reduce { a,b -> PointLong(m(a.x, b.x), m(a.y,b.y), m(a.z,b.z)) }
        val xSize = 500L// 2 * maxVelocity.x
        for(vx in -xSize..xSize) {
            println(vx)
            val ySize = 500L//(2*maxVelocity.y)
            for(vy in -ySize..ySize) {
                val zSize = 500L //(2*maxVelocity.z)
                for(vz in -zSize..zSize) {
                    if (vx == 0L || vy == 0L || vz == 0L) {
                        continue
                    }
                    val velocity = PointLong(vx, vy, vz)
                    // Given velocity vx,vy,vz - where does it have to start
                    // to intersect with h1 and h2 in 2d
                    val h1dv = h1.velocity - velocity
                    val h2dv = h2.velocity - velocity

                    // sx + (t1 * vx) = h1sx + (t1 * h1vx)
                    // sx = h1sx + t1 * (h1vx - vx)
                    // sy = h1sy + t1 * (h1vy - vy)
                    // t1 = (sx - h1sx) / (h1vx - vx)

                    // sx = h2sx + t2 * (h2vx - vx)
                    // sy = h2sy + t2 * (h2vy - vy)

                    val posDelta = h2.start - h1.start

                    val a = h1dv.x
                    val b = h1dv.y
                    val c = h2dv.x
                    val d = h2dv.y
                    val denom = (a * d) - (b * c)
                    if (denom == 0L) continue
                    val t1 = ((d * (h2.start.x - h1.start.x)) - (c * (h2.start.y - h1.start.y))) / denom

                    val pos = PointLong(
                        h1.start.x + (h1.velocity.x * t1) - (vx * t1),
                        h1.start.y + (h1.velocity.y * t1) - (vy * t1),
                        h1.start.z + (h1.velocity.z * t1) - (vz * t1),
                    )
                    val stone = Hailstone(pos, velocity)

                    if (hailstones.all { stone.intersects3D(it) }) {
                        println(stone)
                        return pos.x + pos.y + pos.z
                    }
                }
            }
        }
        return -1
    }

    fun part2linePlane(hailstones: List<Hailstone>): Long {
        // https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection
        val (h1, h2, h3) = hailstones
        // Adjust to the frame of reference of h1 (assume it stays at 0,0,0)
        val h2a = h2 - h1
        val h3a = h3 - h1


        return -1
    }


    // a x + b y + c = 0
    data class StraightLine(val a: Double, val b: Double, val c: Double)

    data class Hailstone(val start: PointLong, val velocity: PointLong) {

        // sx + c(vx) = x
        // c = (x-sx)/vx
        // y = sy + c vy
        fun yAt(x: Long): Long = (start.y) + (velocity.y * ((x - start.x)/velocity.x))


        // y = mx+c    m = vy/vx   c = y - mx
        fun mxc(): MC {
            val m = velocity.y.toDouble() / velocity.x.toDouble()
            val c = start.y - (m * start.x)
            return m to c
        }


        // There's no vertical and horizontal lines
        fun intersects2D(b: Hailstone, min: Long, max: Long): Boolean {
            // y = m1 x + c1 = m2 x + c2
            // (m1-m2) x = (c2-c1)
            // x = (c2 - c1)/(m1-m2)
            // y = m1 x + x1
            val (m1, c1) = mxc()
            val (m2, c2) = b.mxc()

            val slopeDiff = m1 - m2
            if (slopeDiff == 0.0) return false
            val x = (c2-c1)/ slopeDiff
            val y = m1*x + c1
            //println("x: $x y: $y")

            if ((min <= x && x <= max) && (min <= y && y <= max)) {
                // Now check it's not before either of them start
                // sy + d vy = y
                // d vy = y -sy
                // d = (y - sy) / vy
                if ((y-start.y).sign/velocity.y.sign < 0) return false
                if ((y-b.start.y).sign/b.velocity.y.sign < 0) return false;
                return true
            } else {
                return false
            }
    }

        fun intersects3D(b: Hailstone): Boolean {
            // y = m1 x + c1 = m2 x + c2
            // (m1-m2) x = (c2-c1)
            // x = (c2 - c1)/(m1-m2)
            // y = m1 x + x1
            val (m1, c1) = mxc()
            val (m2, c2) = b.mxc()

            val slopeDiff = m1 - m2
            if (slopeDiff == 0.0) return false
            val x = (c2-c1)/ slopeDiff
            val y = m1*x + c1

            val t1 = (y-start.y)/velocity.y
            if (t1 < 0) return false
            val z1 = start.z + (velocity.z * t1)

            val t2 = (y-b.start.y)/b.velocity.y
            if (t2 < 0) return false
            val z2 = b.start.z + (b.velocity.z * t2)

            return z2 == z1
        }

        operator fun minus(other: Hailstone) = Hailstone(start - other.start, velocity - other.velocity)


    }


}


fun main() {
    val testInput = """19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3""".trimIndent().split("\n")



    fun part1(input: List<String>, min: Long, max: Long): Int {
        return Day24().part1(input, min, max)
    }


    fun part2(input: List<String>): Long {
        return Day24().part2(input)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput, 7, 27)
    println(testValue)
    check(testValue == 2)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 24)
    val input = puzzleInput.toList()

    println(part1(input, 200_000_000_000_000, 400_000_000_000_000))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("1184325069403242 is too high")
    println("Time: ${System.currentTimeMillis() - start}")
}
