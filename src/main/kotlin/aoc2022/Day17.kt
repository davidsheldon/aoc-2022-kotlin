package aoc2022

import aoc2022.utils.InputUtils
import utils.Coordinates
import java.lang.Integer.max

typealias Rock = List<String>
fun Rock.width() = get(0).length

class Room(val jets: Iterator<Char>, val rockSupply: Iterator<Rock>) {
    val rows : MutableList<CharArray> = arrayListOf()
    var highest = -1

    fun isCollision(topLeft:Coordinates, rock:Rock) : Boolean {
        if (topLeft.x + rock.width() > 7 || topLeft.x<0)
            return true
        if (topLeft.y - rock.size < -1)
            return true
        for(y in rock.indices)
            for (x in rock[y].indices)
                if (rock[y][x] == '#'  && at(topLeft.x + x, topLeft.y - y) == '#')
                    return true
        return false
    }

    fun ensureRows(size: Int) {
        if (size > rows.size) repeat(size-rows.size) { rows.add(CharArray(7) { '.' })}

    }

    fun at(x: Int, y: Int): Char {
        ensureRows(y+1)
        return rows[y][x]
    }

    fun setRock(x: Int, y:Int) {
        ensureRows(y+1)
        rows[y][x] = '#'
    }

    fun startingLocation(rock: Rock): Coordinates = Coordinates(2, highest + rock.size + 3)

    fun stop(topLeft: Coordinates, rock: Rock) {
        //println("Stopping at $topLeft")
        for(y in rock.indices)
            for (x in rock[y].indices)
                if (rock[y][x] == '#') setRock(topLeft.x + x, topLeft.y - y)
        highest = max(topLeft.y, highest)
    }

    fun render(size: Int): String {
        return (highest downTo max(0, highest - size)).asSequence().map {
            '|' + rows[it].concatToString() + '|'
        }.joinToString("\n") + "\n+-------+\n"

    }

    fun addRocks(n: Int) = repeat(n) { addRock() }

    fun addRock() = addRock(rockSupply.next())

    fun addRock(rock: Rock) {
        var pos = startingLocation(rock)
        do {
            val jet = jets.next()
            var nextPos = pos.dX(if (jet == '<') -1 else 1)
            if (!isCollision(nextPos, rock)) pos = nextPos
            nextPos = pos.dY(-1)
            if (!isCollision(nextPos, rock)) {
                pos = nextPos
            } else {
                break;
            }
        } while (true)
        stop(pos, rock)

    }

}

fun main() {
    val testInput = """>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>""".split("\n")

    val rocks = listOf(
        listOf("####"),
        listOf(
            ".#.",
            "###",
            ".#.",
        ),
        listOf(
            "..#",
            "..#",
            "###",
        ), listOf(
            "#",
            "#",
            "#",
            "#",
        ), listOf(
            "##",
            "##",
        )
    )
    fun <T> Sequence<T>.repeatForever() = generateSequence(this) { it }.flatten()

    fun supplyRocks(jetString: String, count: Int): Room {
        val rockSupply = rocks.asSequence().repeatForever().iterator()
        val jets = jetString.asSequence().repeatForever().iterator()
        val room = Room(jets, rockSupply)

        repeat(count) {
            room.addRock()
        }
        return room
    }

    fun part1(input: List<String>): Int {
        val room = supplyRocks(input[0], 2022)
        return room.highest + 1
    }

    fun part2(input: List<String>): Int {
        println(rocks.size)
        val jetString = input[0]
        println(jetString.length)

        val supplyLoop = rocks.size * jetString.length


        val room = supplyRocks(jetString, supplyLoop)
        sequence {
            while(true) {
                yield(room.highest)
                room.addRocks(supplyLoop)
            }
        }.zipWithNext { a,b -> b-a}.take(100).forEach { println(it) }

        return -1;
    }



    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 3068)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 17).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

