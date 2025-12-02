package aoc2024

import utils.ArrayAsSurface
import utils.Direction
import utils.InputUtils

private fun dirToButton(dir: Direction) = when (dir) {
    utils.N -> '^'
    utils.S -> 'v'
    utils.E -> '>'
    utils.W -> '<'
}

class Keypad(layout: List<String>) {
    val map = ArrayAsSurface(layout)
    // Map from key to coordinates
    val keys = map.indexed()
        .filter { (_, c) -> c != ' ' }
        .map { (l, c) -> c to l }.toMap()
    val paths: Map<Char, Map<Char, List<String>>> = keys.map { (from, fromLoc) ->
        from to keys
            .map { (to, toLoc) ->
                val route = toLoc - fromLoc
                val moves = (if (route.x > 0) List(route.x) {utils.E} else List(-route.x) {utils.W}) +
                        (if (route.y > 0) List(route.y) {utils.S} else List(-route.y) {utils.N} )
                val routes = moves.permutations()
                //println(" $from -> $to : $moves")
                to to if (from == to) listOf("A") else routes.filter {
                   it.runningFold(fromLoc) { c, m -> c.move(m)}.none { map.checkedAt(it) == ' ' }
                }.map { rt -> rt.map { dirToButton(it) }.joinToString("") + "A" }.distinct()
            }.toMap()
    }.toMap()
    // from, to, depth -> ways
    val cache = mutableMapOf<Pair<Pair<Char, Char>, Int>, Long>()


    fun moves(toType: String): List<String> {
        return moves2(toType).combinations()
    }

    fun moves2(toType: String) = "A$toType".zipWithNext()
        // .onEach { (a,b) -> println("$a->$b ${paths[a]!![b]!![0]}") }
        .map { (from, to) -> paths[from]!![to]!! }

    fun keypresses(depth: Int, text: String): Long =
        "A$text".zipWithNext().sumOf { (from, to) -> ways(depth, from, to) }

    fun ways(depth: Int, from: Char, to: Char): Long {
        if (depth == 0) return 1
        val key = from to to to depth
        if (cache.containsKey(key)) return cache[key]!!

        val ret = paths[from]!![to]!!.minOf { keypresses(depth -1, it) }
        cache[key] = ret
        return ret
    }

}

fun main() {

    val testInput = """029A
980A
179A
456A
379A""".trimIndent().split("\n")


    val numeric = Keypad(listOf("789","456","123"," 0A"))
    println(numeric.map)
    println(numeric.paths['1']!!['A']!!)

    println(numeric.moves(testInput[0]))
    val arrows = Keypad(listOf(" ^A", "<v>"))
    println(arrows.paths)


    fun part1(input: List<String>): Long {
        return input.sumOf { row ->
            val num = row.subSequence(0, row.length - 1).toString().toLong()
            val shortest = numeric.moves(row)
                .flatMap { arrows.moves(it) }
                .flatMap { arrows.moves(it) }.minOf { it.length}
            // check part2
            val ways = numeric.moves2(row).sumOf { it.minOf { p -> arrows.keypresses(2,  p) } }
            println("$shortest - $ways")
            num * shortest
        }
    }



    fun part2(input: List<String>): Long {

        return input.sumOf { row ->
            val num = row.subSequence(0, row.length - 1).toString().toLong()
            val shortest = numeric.moves2(row).sumOf { it.minOf { p -> arrows.keypresses(25,  p) } }
            println("$num - $shortest")
            num * shortest
        }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 126384L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 21)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
