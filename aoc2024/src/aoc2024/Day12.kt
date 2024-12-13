package aoc2024

import utils.*

private class Map01(input: List<String>): ArrayAsSurface(input) {
    val connectedAreas = mutableMapOf<Coordinates, Set<Coordinates>>()
    init {
        allPoints().map {it to setOf(it)}.toMap(connectedAreas)
    }

    fun score(): Long {
        return (connectedAreas.values.distinct().sumOf {
            score(it)
        })
    }

    /*
......CC..  1
......CCC.  1
.....CC...  2
...CCC....  2
....C.....  2
....CC....  1
.....C....  1
..........  1
..........
..........
     */
    
    
    fun score(coords: Set<Coordinates>): Long {
        val area = coords.size
        val perimeter = coords.sumOf { c -> c.adjacent().count { !coords.contains(it)  } }
        //println("Scoring ($area * $perimeter) - $coords")
        return (area * perimeter).toLong()
    }

    fun scorePart2(): Long {
        return (connectedAreas.values.distinct().sumOf {
            scorePart2(it)
        })
    }

    fun scorePart2(coords: Set<Coordinates>): Long {
        val area = coords.size
        if (area == 1) return 4

        val edgesPerCoordinate = coords.associate { c -> c to listOf(N, S, E, W).filter { !coords.contains(c.move(it))  } }

        val sides = edgesPerCoordinate.entries.sumOf { (c, dirs) ->
            dirs.sumOf { d ->

                val leftInBorder = (edgesPerCoordinate[c.move(d.turnLeft())] ?: emptyList()).contains(d)
                val rightInBorder = (edgesPerCoordinate[c.move(d.turnRight())] ?: emptyList()).contains(d)
                var score = 0
                if (leftInBorder xor rightInBorder) { score += 1 }
                if (!leftInBorder && !rightInBorder) { score += 2 }
                score
            }
        } / 2

        val letter = at(coords.first())

        println("Scoring $letter ($area * $sides) ")
        return (area * sides).toLong()
    }

    fun scorePart2Attempt2(coords: Set<Coordinates>): Long {
        val area = coords.size
        if (area == 1) return 4
        val letter = at(coords.first())
        val bounds = coords.toList().bounds()
        // Get all the horizontal edges
        val horizontal = IntRange(bounds.tl.y,bounds.br.y+1).sumOf { y ->
            val wallPieces = IntRange(bounds.tl.x,bounds.br.x + 1).filter { x ->
                // Is an edge if
                coords.contains(Coordinates(x, y - 1)) xor coords.contains(Coordinates(x, y))
            }

            val count = if (wallPieces.isEmpty()) 0 else (1+wallPieces.zipWithNext().count { (l,r) -> r!= l+1 })
              //  if (letter == 'C') println("Wall at y=$y is $wallPieces ($count)")
            // merge connected ones
            count
        }

        // Get all the vertical edges and merge them
        val vertical = IntRange(bounds.tl.x,bounds.br.x+1).sumOf{ x ->
            val wallPieces = IntRange(bounds.tl.y,bounds.br.y + 1).filter { y ->
                    // Is an edge if
                    val left = coords.contains(Coordinates(x-1,y))
                    val right = coords.contains(Coordinates(x,y))
                    left xor right
                }
            val count = if (wallPieces.isEmpty()) 0 else (1+wallPieces.zipWithNext().count { (l,r) -> r!= l+1 })
            //if (letter == 'C')  println("Wall at x=$x is $wallPieces ($count)")
            // merge connected ones
            count

        }
        val sides = vertical + horizontal

        println("Scoring $letter ($area * $sides) ($horizontal $vertical) ")//- $coords")
        return (area * sides).toLong()
    }

    fun scorePart2noHoles(coords: Set<Coordinates>): Long {
        val area = coords.size
        if (area == 1) return 4
        var sides = 0
        // Find a corner, walk around until we're back at the start, counting corners
        val topLeft = coords.minOf { it }
        val letter = at(topLeft)
        var heading: Direction = utils.E
        var location = topLeft

        do {
            if (checkedAt(location.move(heading)) != letter) {
                // can't carry on, turn right
                heading = heading.turnRight()
                sides += 1
            } else if (checkedAt(location.move(heading).move(heading.turnLeft())) == letter) {
                location = location.move(heading)
                heading = heading.turnLeft()
                location = location.move(heading)
                sides += 1
            }
            else {
                location = location.move(heading)
            }
        } while (heading != utils.E || location != topLeft)
        // TODO: Deal with holes in the middle
        println("Scoring $letter ($area * $sides) ")//- $coords")
        return (area * sides).toLong()
    }



    fun mergeAreas() {
        allPoints().forEach { coordinates ->
            coordinates.adjacent().filter { inBounds(it) }.forEach { c -> tryMerge(coordinates, c)  }
        }
    }
    fun tryMerge(a: Coordinates, b: Coordinates) {
        if (at(a) == checkedAt(b, ' ')) merge(a,b)
    }


    fun merge(a: Coordinates, b: Coordinates) {
        if (connectedAreas[a]?.contains(b) != false) return
        val merged = (connectedAreas[a] ?: emptySet<Coordinates>()) + (connectedAreas[b] ?: emptySet())
        merged.forEach { m -> connectedAreas[m] = merged  }
    }

}

fun main() {

    val testInput = """RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE""".trimIndent().split("\n")


    val testInput2 = """AAAA
ABBA
AAAA""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val map = Map01(input)
        map.mergeAreas()
        return map.score()
    }


    fun part2(input: List<String>): Long {
        val map = Map01(input)
        map.mergeAreas()
        return map.scorePart2()
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 1930L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 12)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input)) // 878118 is too low, 903286 too high, 894810 wrong
}
