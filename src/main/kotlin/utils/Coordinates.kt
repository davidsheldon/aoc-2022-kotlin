package utils

import aoc2022.listOfNumbers
import aoc2022.takeWhilePlusOne
import kotlin.math.absoluteValue
import kotlin.math.max
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

    fun length(): Int = x.absoluteValue + y.absoluteValue
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
    operator fun contains(c:Coordinates) = c.x >= tl.x && c.x <= br.x && c.y >= tl.y && c.y <= br.y
}

sealed class Direction {
}

object N: Direction()
object S: Direction()
object E: Direction()
object W: Direction()


data class Point3d(val x: Int, val y: Int, val z: Int) {
    fun around() = sequence {
        for(xx in -1..1)
            for (yy in -1..1)
                for(zz in -1 .. 1)
                    if (!(xx==0&&yy==0&&zz==0))
                        yield(copy(x=x+xx, y=y+yy, z=z+zz))
    }

    fun connected() = sequence {
        for(xx in listOf(-1,1)) {
            yield(copy(x = x + xx))
            yield(copy(y = y + xx))
            yield(copy(z = z + xx))
        }
    }

    operator fun minus(other: Point3d) = Point3d(x - other.x, y - other.y, z-other.z)
    operator fun plus(other: Point3d) = Point3d(x + other.x, y + other.y, z+other.z)
    fun sign() = Point3d(x.sign, y.sign,z.sign)

    fun times(len: Int) = Point3d(x*len, y*len,z*len)
    companion object {
        fun unit() = Point3d(1,1,1)
    }
}

fun String.toPoint3d(): Point3d { val (x,y,z) = listOfNumbers(this); return Point3d(x,y,z)}

data class Cube(val min: Point3d, val max: Point3d) {
    fun expand(point: Point3d) = copy(min=Point3d(
        x=min(min.x, point.x),
        y=min(min.y, point.y),
        z=min(min.z, point.z)
    ), max=Point3d(
        x=max(max.x, point.x),
        y=max(max.y, point.y),
        z=max(max.z, point.z)
    )
    )

    fun padded(n: Int) = Cube(min=min-Point3d.unit().times(n), max=max+Point3d.unit().times(n))
    operator fun contains(p: Point3d): Boolean {
        return p.x in min.x..max.x &&
                p.y in min.y..max.y &&
                p.z in min.z..max.z
    }
}