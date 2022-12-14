package utils

import aoc2022.listOfNumbers
import aoc2022.takeWhilePlusOne
import java.lang.Integer.max
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sign

data class Coordinates(val x: Int = 0, val y : Int = 0) {
    fun dY(delta : Int) = copy(y = y + delta)
    fun dX(delta : Int) = copy(x = x + delta)

    fun isTouching(other: Coordinates) =
        (x-other.x).absoluteValue <= 1 && (y-other.y).absoluteValue <= 1

    operator fun minus(other: Coordinates) = Coordinates(x - other.x, y - other.y)
    operator fun plus(other: Coordinates) = Coordinates(x + other.x, y + other.y)
    fun sign() = Coordinates(x.sign, y.sign)

    fun directionTo(other: Coordinates) = Coordinates(other.x - x, other.y - y).sign()


    fun move(d: Direction, distance: Int = 1): Coordinates =
        when(d) {
            N -> dY(-distance)
            S -> dY(distance)
            E -> dX(distance)
            W -> dX(-distance)
        }

    fun adjacent() = sequence {
            yield(dX(1))
            yield(dX(-1))
            yield(dY(1))
            yield(dY(-1))
        }

    fun lineTo(end: Coordinates): Sequence<Coordinates> {
        val direction = this.directionTo(end)
        return generateSequence(this) {
            it + direction
        }.takeWhilePlusOne { it != end }
    }
}

fun String.toCoordinates(): Coordinates {
    val (x,y) = listOfNumbers(this)
    return Coordinates(x,y)
}

fun List<Coordinates>.boundingBox() : Pair<Coordinates, Coordinates> {
    val head = get(0)
    val tail = drop(1)
    return tail.fold(head to head) { (tl, br), coord ->

        Coordinates(min(tl.x, coord.x), min(tl.y, coord.y)) to
                Coordinates(max(br.x, coord.x), max(br.y, coord.y))
    }
}

data class Bounds(val tl: Coordinates, val br: Coordinates) {
    fun contains(c:Coordinates) = c.x >= tl.x && c.x <= br.x && c.y >= tl.y && c.y <= br.y
}

sealed class Direction {
}

object N: Direction()
object S: Direction()
object E: Direction()
object W: Direction()