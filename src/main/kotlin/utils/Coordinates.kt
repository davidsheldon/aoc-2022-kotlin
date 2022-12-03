package utils

data class Coordinates(val x: Int = 0, val y : Int = 0) {
    fun dY(delta : Int) = copy(y = y + delta)
    fun dX(delta : Int) = copy(x = x + delta)
}