package aoc2024

import utils.Coordinates
import utils.InputUtils
import kotlin.math.roundToLong

private val buttonParser = "Button ([AB]): X\\+(\\d+), Y\\+(\\d+)".toRegex()
private val prizeParser = "Prize: X=(\\d+), Y=(\\d+)".toRegex()

private data class Button(val delta: Coordinates, val cost: Int=1)

private fun String.toButton(): Button =
    buttonParser.matchEntire(this)?.let { match ->
        val (label, x, y) = match.destructured
        Button(Coordinates(x.toInt(), y.toInt()), if (label == "A") 3 else 1)
    } ?: throw IllegalArgumentException("Invalid button: $this")

private fun String.toPrize(): Coordinates =
    prizeParser.matchEntire(this)?.let { match ->
        val (x, y) = match.destructured
        Coordinates(x.toInt(), y.toInt())
    } ?: throw IllegalArgumentException("Invalid prize: $this")

private data class Problem(val buttonA: Button, val buttonB: Button, val prize: Coordinates) {
    fun cost(): Long {
        return LongRange(0, minOf(100, (prize.x/buttonB.delta.x)).toLong())
            .map { b -> costAt(b, prize.x.toLong(), prize.y.toLong()) }
            .filter { it != 0L}
            .minOrNull() ?: 0L

    }

    fun colinear(): Boolean = buttonA.delta.crossProduct(buttonB.delta) == 0

    fun cost2(delta: Long = 10000000000000): Long {
        if (colinear()) { // Find the lowest costing version
            println("Colinear")
            return 0
        }
        else { // There's only 1 (integral) solution., 2 unknowns, 2 formulae
            // (1) a(A.x) + b(B.x) = P.x
            // (2) a(A.y) + b(B.y) = P.y
            // (1) - (Bx/By)(2)
            //  a(Ax - BxAy/By) = Px - BxPy/By
            // a = (Px-(BxPy/By))/(Ax-BxAy/By)
            // b = (Px-(aAx))/Bx

            val px = prize.x + delta
            val py = prize.y + delta
            val (a,b) = solve(px.toDouble(), py.toDouble(),
                buttonA.delta.x.toDouble(), buttonA.delta.y.toDouble(),
                buttonB.delta.x.toDouble(), buttonB.delta.y.toDouble()
                )


            val al = a.roundToLong()
            val bl = b.roundToLong()
            if ((al *buttonA.delta.x + bl *buttonB.delta.x == px)  &&
                (al *buttonA.delta.y + bl *buttonB.delta.y == py)) {
                return (al* 3) + bl
            }
            return 0
        }
    }

    // a = (Px-(BxPy/By))/(Ax-BxAy/By)
    // b = (Px-(aAx))/Bx
    fun solve(pX: Double, pY: Double, aX: Double, aY: Double, bX: Double, bY: Double): Pair<Double, Double> {
        val a = (pX - (bX*pY/bY))/(aX - (bX*aY/bY))
        val b = (pX - (a * aX))/bX
        return a to b
    }

    private fun costAt(b: Long, x: Long, y: Long): Long {
        val bx = b * buttonB.delta.x
        val remaining = x - bx
        if (remaining % buttonA.delta.x == 0L) {
            val a = remaining / buttonA.delta.x
            if (a * buttonA.delta.y + b * buttonB.delta.y == y) {
                //println("$a $b ${((3*a) + b)}")
                return (3 * a) + b
            }
        }
        return 0
    }
}

fun main() {

    val testInput = """Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val problems = input.toBlocksOfLines().map {
            val (b1, b2, p) = it
            Problem(b1.toButton(), b2.toButton(), p.toPrize())
        }

        return problems
            .onEach { println("${it.cost()} - ${it.cost2(delta = 0)}") }
            .map { problem ->

            problem.cost() 
        }.sumOf { it.toLong() }
    }


    fun part2(input: List<String>): Long {
        val problems = input.toBlocksOfLines().map {
            val (b1, b2, p) = it
            Problem(b1.toButton(), b2.toButton(), p.toPrize())
        }

        return problems.map { problem -> problem.cost2() }.sumOf { it.toLong() }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 480L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 13)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
