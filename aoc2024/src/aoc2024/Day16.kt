package aoc2024

import utils.*
import java.util.*


private data class GridPosition(val loc: Coordinates, val dir: Direction)
private class Map16(input: List<String>): ArrayAsSurface(input) {
    val end = find { it == 'E'}
    val start = find { it == 'S'}

    fun calculateCosts(// A*
        nextFn: (GridPosition, Int) -> List<Pair<GridPosition, Int>>
    ): Map<GridPosition, Int> {
        val seenCosts = mutableMapOf<GridPosition, Int>() // Stores lowest score we've had at that position
        val queue = PriorityQueue<Pair<Int, GridPosition>>(compareBy { it.first })
        queue.add(0 to GridPosition(start, E))

        while (queue.isNotEmpty()) {
            val(score, state) = queue.poll()
            //println(score to state)
//                if (endState(state) && score < minCost) {
//                    println(score)
//                    minCost = score
//                }

            val nextStates = nextFn(state, score)
                .filter { inBounds(it.first.loc) }

            //println("Next: $nextStates Seen: ${seen.size}")
            val scoredNext = nextStates
                .filter { (state, cost) ->
                    seenCosts.getOrDefault(state, Int.MAX_VALUE) > cost
                }
                .map { it.second to it.first}
            scoredNext.forEach { seenCosts[it.second] = it.first }
            //println("Scored: $scoredNext")
            queue.addAll(scoredNext)
        }
        return seenCosts
    }

    fun endState(state: GridPosition): Boolean = state.loc == end

    fun Map<GridPosition, Int>.atEnd() = filter { endState(it.key)}

    fun minimumCost(
        seenCosts: Map<GridPosition, Int>
    ): Int {
        return seenCosts.atEnd().minOf { it.value }
    }

    fun routeToEnd(
        end: GridPosition,
        start: GridPosition,
        distances: Map<GridPosition, Int>
    ): Sequence<Coordinates> = sequence {
//        var pos = end
//        while (pos != start) {
//            val height = maze.heightAt(pos)
//            pos = pos.adjacent().filter { it in distances && maze.heightAt(it) <= height + 1 }
//                .sortedBy { distances[it] }.first()
//            yield(pos)
//        }
    }

    fun routesToEnd(
        seenCosts: Map<GridPosition, Int>,
        previousFunc: (GridPosition, Int) -> List<Pair<GridPosition, Int>>
    ): Sequence<GridPosition> =
        sequence {
        val ends = seenCosts.atEnd()
        val minCost = ends.minOf { it.value }

        var front = ends.filter { it.value == minCost }
        while(!front.isEmpty()) {
            yieldAll(front.map { it.key })
            front = front.flatMap { previousFunc(it.key, it.value) }
                .filter { (pos, cost) -> seenCosts.getOrDefault(pos, -1) == cost}
                .toMap()
        }

    }

}


fun main() {

    val testInput = """###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val map = Map16(input)

        val costs = map.calculateCosts({ pos, score ->
            listOf(
                pos.copy(loc = pos.loc.move(pos.dir)) to score + 1,
                pos.copy(dir = pos.dir.turnLeft()) to score + 1000,
                pos.copy(dir = pos.dir.turnRight()) to score + 1000
            ).filter { map.checkedAt(it.first.loc) != '#' }
        })
        return map.minimumCost(costs).toLong()
    }


    fun part2(input: List<String>): Long {
        val map = Map16(input)

        val costs = map.calculateCosts({ pos, score ->
            listOf(
                pos.copy(loc = pos.loc.move(pos.dir)) to score + 1,
                pos.copy(dir = pos.dir.turnLeft()) to score + 1000,
                pos.copy(dir = pos.dir.turnRight()) to score + 1000
            ).filter { map.checkedAt(it.first.loc) != '#' }
        })
        val previousFunc = { pos: GridPosition, score: Int ->
            listOf(
                pos.copy(loc = pos.loc.move(pos.dir.opposite())) to score - 1,
                pos.copy(dir = pos.dir.turnLeft()) to score - 1000,
                pos.copy(dir = pos.dir.turnRight()) to score - 1000
            )
        }

        val route = map.routesToEnd(costs, previousFunc).map { it.loc }.distinct()

        //println(route.fold(map.replace(Coordinates(0,0), 'X')) { m, loc ->  m.replace(loc, '+') })

        return route.count().toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 7036L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 16)
    val input = puzzleInput.toList()

    println(part1(input)) // 89472 too high
    println(part2(input))
}
