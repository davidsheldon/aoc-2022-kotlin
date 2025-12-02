package utils

import aoc2024.listOfNumbers
import aoc2024.takeWhilePlusOne
import java.util.function.Predicate
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun String.replaceAt(index: Int, c: Char): String {
    val sb = StringBuilder(this)
    sb.setCharAt(index, c)
    return sb.toString()
}

fun gcd(a: Int, b: Int): Int {
    var num1 = a
    var num2 = b
    while (num2 != 0) {
        val temp = num2
        num2 = num1 % num2
        num1 = temp
    }
    return num1
}

data class Coordinates(val x: Int = 0, val y : Int = 0): Comparable<Coordinates> {
    fun dY(delta : Int) = copy(y = y + delta)
    fun dX(delta : Int) = copy(x = x + delta)

    fun isTouching(other: Coordinates) =
        (x-other.x).absoluteValue <= 1 && (y-other.y).absoluteValue <= 1

    operator fun minus(other: Coordinates) = Coordinates(x - other.x, y - other.y)
    operator fun plus(other: Coordinates) = Coordinates(x + other.x, y + other.y)
    fun sign() = Coordinates(x.sign, y.sign)
    fun normalise(): Coordinates {
        val divisor = gcd(x, y)
        return Coordinates(x/divisor, y/divisor)
    }

    fun directionTo(other: Coordinates) = Coordinates(other.x - x, other.y - y).sign()
    fun compassTo(other: Coordinates) = when(directionTo(other)) {
        Coordinates(0,-1) -> N
        Coordinates(0, 1) -> S
        Coordinates( 1,0) -> E
        Coordinates(-1,0) -> W
        else -> null
    }

    fun move(d: Direction, distance: Int = 1): Coordinates =
        when(d) {
            N -> dY(-distance)
            S -> dY(distance)
            E -> dX(distance)
            W -> dX(-distance)
        }

    fun move(d: CompassPoint, distance: Int = 1): Coordinates =
        when(d) {
            CompassPoint.N -> dY(-distance)
            CompassPoint.NE -> move(N).move(E)
            CompassPoint.NW -> move(N).move(W)
            CompassPoint.SE -> move(S).move(E)
            CompassPoint.SW -> move(S).move(W)
            CompassPoint.S -> dY(distance)
            CompassPoint.E -> dX(distance)
            CompassPoint.W -> dX(-distance)
        }

    fun adjacent() = sequence {
            yield(dX(1))
            yield(dX(-1))
            yield(dY(1))
            yield(dY(-1))
        }
    fun adjacentIncludeDiagonal() = sequence {
            yieldAll(adjacent())
            yield(dX(-1).dY(1))
            yield(dX(-1).dY(-1))
            yield(dX(1).dY(1))
            yield(dX(1).dY(-1))
        }

    fun lineTo(end: Coordinates): Sequence<Coordinates> {
        val direction = this.directionTo(end)
        return generateSequence(this) {
            it + direction
        }.takeWhilePlusOne { it != end }
    }

    fun length(): Int = x.absoluteValue + y.absoluteValue
    fun heading(d: Direction): Sequence<Coordinates> =
        generateSequence(this) { it.move(d) }

    fun heading(d: CompassPoint): Sequence<Coordinates> =
        generateSequence(this) { it.move(d) }

    fun crossProduct(other: Coordinates) = x*other.y - y*other.x

    override fun compareTo(other: Coordinates): Int = compareBy(Coordinates::y, Coordinates::x).compare(this, other)

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

fun List<Coordinates>.bounds() : Bounds {
    val (tl, br) = this.boundingBox()
    return Bounds(tl, br)
}

sealed class Direction {
    abstract fun opposite(): Direction
    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
}

data object N: Direction() {
    override fun opposite() = S
    override fun turnLeft() = W
    override fun turnRight() = E
}

data object S: Direction(){
    override fun opposite() = N
    override fun turnLeft() = E
    override fun turnRight() = W
}
data object E: Direction(){
    override fun opposite() = W
    override fun turnLeft() = N
    override fun turnRight() = S
}
data object W: Direction(){
    override fun opposite() = E
    override fun turnLeft() = S
    override fun turnRight() = N
}

enum class CompassPoint {
    N,NE,E,SE,S,SW,W,NW
}


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

open class ArrayAsSurface(val points: List<String>) {
    private val bounds = Bounds(Coordinates(0,0), Coordinates(points[0].length - 1, points.size-1))
    fun allPoints(): Sequence<Coordinates> =
        points.indices.asSequence().flatMap { y -> row(y) }

    fun row(y: Int) = points[0].indices.asSequence().map { x -> Coordinates(x,y)}
    fun rows() = points.indices.asSequence().map { y -> row(y) }

    fun indexed(): Sequence<Pair<Coordinates, Char>> =
        points.asSequence().flatMapIndexed { y, row ->
            row.asSequence().mapIndexed { x: Int, c: Char ->  Coordinates(x,y) to c}
        }

    fun find(c: Char) = find { it == c}
    fun find(test: Predicate<Char>): Coordinates =
        indexed()
            .first { (_, c) -> test.test(c)}
            .first


    fun inBounds(c: Coordinates) = bounds.contains(c)
    open fun at(c: Coordinates): Char = points[c.y][c.x]
    fun get(x: Int, y: Int) = points[y][x]
    fun get(c: Coordinates) = at(c)
    fun checkedAt(c: Coordinates, default: Char = ' ') = if(inBounds(c)) { at(c) } else { default }

    fun getHeight() = points.size
    fun getWidth() = points[0].length


    fun bottomRight() = bounds.br
    override fun toString() = points.joinToString("\n")
    fun replace(c: Coordinates, char: Char) = ArrayAsSurface(
        points.mapIndexed { y, line -> if (y==c.y) line.replaceAt(c.x,char) else line }
    )
}