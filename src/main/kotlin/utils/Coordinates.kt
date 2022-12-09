package utils

import kotlin.math.absoluteValue
import kotlin.math.sign

data class Coordinates(val x: Int = 0, val y : Int = 0) {
    fun dY(delta : Int) = copy(y = y + delta)
    fun dX(delta : Int) = copy(x = x + delta)

    fun isTouching(other: Coordinates) =
        (x-other.x).absoluteValue <= 1 && (y-other.y).absoluteValue <= 1

    operator fun minus(other: Coordinates) = Coordinates(x - other.x, y - other.y)
    operator fun plus(other: Coordinates) = Coordinates(x + other.x, y + other.y)
    fun sign() = Coordinates(x.sign, y.sign)

    fun move(d: Direction, distance: Int = 1): Coordinates =
        when(d) {
            N -> dY(-distance)
            S -> dY(distance)
            E -> dX(distance)
            W -> dX(-distance)
        }
}

sealed class Direction {
}

object N: Direction()
object S: Direction()
object E: Direction()
object W: Direction()