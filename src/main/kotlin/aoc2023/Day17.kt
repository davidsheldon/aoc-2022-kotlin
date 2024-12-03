package aoc2023

import utils.*
import utils.Direction
import java.util.*

private data class State(val loc: Coordinates, val dir: Direction, val distance: Int) {
    fun nextStates(): List<State> = listOf(dir.turnRight(), dir.turnLeft())
        .map { d -> State(loc.move(d, 1), d, 1) } +
            if (distance < 3) { listOf(State(loc.move(dir), dir, distance + 1)) } else emptyList()

    fun nextStates2(): List<State> = (if (distance >= 4) {
        listOf(dir.turnRight(), dir.turnLeft())
     .map { d -> State(loc.move(d, 1), d, 1) }} else {emptyList()}) + (if (distance < 10) { listOf(State(loc.move(dir), dir, distance + 1))
    } else emptyList())

}

private class Day17(input: List<String>): ArrayAsSurface(input) {
    // A*
    fun costs(
        nextFn: (State) -> List<State>,
        endState: (State) -> Boolean = {state -> state.loc == bottomRight()}
    ): Int {
        val queue = PriorityQueue<Pair<Int, State>>(compareBy { it.first })
        queue.add(0 to State(Coordinates(0, 0), S, 1))
        queue.add(0 to State(Coordinates(0, 0), E, 1))
        val seen = mutableSetOf<State>()
        while (queue.isNotEmpty()) {
            val(score, state) = queue.poll()
            //println(score to state)
            if (endState(state)) return score

            val nextStates = nextFn(state)
                .filter { inBounds(it.loc) }

            //println("Next: $nextStates Seen: ${seen.size}")
            val scoredNext = nextStates
                .filter { seen.add(it) }
                .map { score + costAt(it.loc) to it}
            //println("Scored: $scoredNext")
            queue.addAll(scoredNext)
        }
        return -1
    }

    fun costAt(loc: Coordinates) = at(loc) - '0'
    // Dijkstra
    fun shortest(minStep: Int, maxStep: Int) {
        // Edges are Coord/Direction -> Coord/direction



    }

    private fun edgesFrom(
        loc: Coordinates, dir: Direction,
        minStep: Int,
        maxStep: Int
    ) {

        var edgeCost = 0
        for (step in loc.heading(dir).take(maxStep).filter { inBounds(it) }.withIndex()) {
            edgeCost += costAt(step.value)
            if (step.index >= minStep) {

            }
        }
    }


}

private class Solution {
    private val LEFT = 0
    private val RIGHT: Int = 1
    private val UP: Int = 2
    private val DOWN: Int = 3
    private val INF = 1000000000
    private val dir = arrayOf(intArrayOf(0, -1), intArrayOf(0, 1), intArrayOf(-1, 0), intArrayOf(1, 0))

    private fun valid(G: Array<IntArray>, i: Int, j: Int): Boolean {
        return i >= 0 && i < G.size && j >= 0 && j < G[0].size
    }

    private fun relax(
        G: Array<IntArray>,
        dist: Array<Array<IntArray>>,
        pq: PriorityQueue<IntArray>,
        xx: Int,
        yy: Int,
        d: Int,
        curDist: Int,
        minSteps: Int,
        maxSteps: Int
    ) {
        var x = xx
        var y = yy
        var edge = 0
        for (step in 1..maxSteps) {
            x += dir[d][0]
            y += dir[d][1]
            if (valid(G, x, y)) {
                edge += G[x][y]
                if (step >= minSteps) {
                    val alt = curDist + edge
                    if (alt < dist[x][y][d]) {
                        dist[x][y][d] = alt
                        pq.add(intArrayOf(x, y, d, alt))
                    }
                }
            }
        }
    }

    fun solve(G: Array<IntArray>, minSteps: Int, maxSteps: Int): Int {
        val R = G.size
        val C = G[0].size
        val dist = Array(R) {
            Array(C) {
                IntArray(
                    4
                )
            }
        }
        for (i in 0 until R) for (j in 0 until C) for (d in 0..3) dist[i][j][d] = INF
        val pq = PriorityQueue { a: IntArray, b: IntArray ->
            a[3] - b[3]
        }
        relax(G, dist, pq, 0, 0, RIGHT, 0, minSteps, maxSteps)
        relax(G, dist, pq, 0, 0, DOWN, 0, minSteps, maxSteps)
        while (!pq.isEmpty()) {
            val t = pq.poll()
            val i = t[0]
            val j = t[1]
            val d = t[2]
            val curDist = t[3]
            if (i == R - 1 && j == C - 1) return curDist
            if (d == LEFT || d == RIGHT) {
                relax(G, dist, pq, i, j, UP, curDist, minSteps, maxSteps)
                relax(G, dist, pq, i, j, DOWN, curDist, minSteps, maxSteps)
            } else {
                relax(G, dist, pq, i, j, LEFT, curDist, minSteps, maxSteps)
                relax(G, dist, pq, i, j, RIGHT, curDist, minSteps, maxSteps)
            }
        }
        return -1
    }


}


fun main() {
    val testInput = """2413432311323
3215453535623
3255245654254
3446585845452
4546657867536
1438598798454
4457876987766
3637877979653
4654967986887
4564679986453
1224686865563
2546548887735
4322674655533""".trimIndent().split("\n")

    val testInput2 = """111111111111
999999999991
999999999991
999999999991
999999999991""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        return Day17(input).costs(State::nextStates)
    }


    fun part2(input: List<String>): Int {
//        val day17 = Day17(input)
//        return day17.costs(State::nextStates2) { state: State -> state.distance >= 4 && state.loc == day17.bottomRight() }
        val s = ArrayAsSurface(input)

        val g = s.rows().map { row -> row.map { s.at(it) - '0' }.toList().toIntArray() }.toList().toTypedArray()

        return Solution().solve(g, 4, 10)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 102)

    println(part2(testInput))
    println(part2(testInput2))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 17)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("1106 == x")
    println("Time: ${System.currentTimeMillis() - start}")
}
