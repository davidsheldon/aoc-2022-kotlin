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
}