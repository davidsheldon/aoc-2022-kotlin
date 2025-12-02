package aoc2024

import utils.InputUtils


private class Graph(val input: List<String>) {
    val edges = input.map {
        val (a,b) = it.split('-')
        a to b
    }.toSet()
    val nodes = edges.flatMap { listOf(it.first, it.second) }.distinct()

    fun isLinked(node1:String, node2:String) = edges.contains(node2 to node1) || edges.contains(node1 to node2)
    fun neighbours(node:String) = edges
        .mapNotNull { if (it.first == node) it.second else if (it.second==node) it.first else null }

    fun adjacency(): Matrix {
        val adjacency: Matrix = Array(nodes.size) { IntArray(nodes.size) }
        edges.forEach { (a,b) ->
            adjacency[nodes.indexOf(a)][nodes.indexOf(b)] = 1
            adjacency[nodes.indexOf(b)][nodes.indexOf(a)] = 1
        }
        return adjacency
    }

    fun largestClique(): Set<String> {
        val alg = BronKerbosch(nodes.associateWith { neighbours(it).toSet() })
        return alg.largestClique()

    }


}

fun main() {

    val testInput = """kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val graph = Graph(input)
        println(graph.edges.size)

        val adjacency = graph.adjacency()
        println(adjacency.trace())
        val matrixPower = matrixPower(adjacency, 3)
        println(matrixPower.trace() / 6)

        val triples = mutableSetOf<Set<String>>()

        graph.nodes.forEach { base ->
            graph.neighbours(base).toList().allPairs().forEach { (a,b) ->
              if (graph.isLinked(a,b)){
                  triples.add(setOf(base,a,b))
              }
            }
        }
        println(triples.size)



        return triples.count { it.any { it[0] == 't' }}.toLong()
    }

    fun part2(input: List<String>): String {
        val graph = Graph(input)
        val clique = graph.largestClique()


        return clique.sorted().joinToString(",")
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 7L)
    println(part2(testInput))
    val puzzleInput = InputUtils.downloadAndGetLines(2024, 23)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
