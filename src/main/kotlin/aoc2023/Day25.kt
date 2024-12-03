package aoc2023

import utils.InputUtils

data class Link(val e1: String, val e2: String)

fun main() {
    val testInput = """jqt: rhn xhk nvd
rsh: frs pzl lsr
xhk: hfx
cmg: qnr nvd lhk bvb
rhn: xhk bvb hfx
bvb: xhk hfx
pzl: lsr hfx nvd
qnr: nvd
ntq: jqt hfx bvb xhk
nvd: lhk
lsr: lhk
rzs: qnr cmg lsr rsh
frs: qnr lhk lsr""".trimIndent().split("\n")

    // https://en.wikipedia.org/wiki/Spanning_tree
    // Deletion-contraction
    // t(G) = t(G − e) + t(G/e)


    fun part1(input: List<String>): Int {
        val links = input.flatMap {
            val nodes = it.split(": ")
            nodes.drop(1).map { n -> Link(nodes.first(), n)  }
        }
        val allNodes = links.flatMap { listOf(it.e1, it.e2) }.toSet().toList()
        

        return input.size
    }


    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 54)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 25)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
