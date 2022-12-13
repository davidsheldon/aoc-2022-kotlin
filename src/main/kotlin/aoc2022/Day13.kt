package aoc2022

import aoc2022.utils.InputUtils
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private val objectMapper = jacksonObjectMapper()

sealed class Comp {
    abstract operator fun compareTo(right: Comp): Int

    data class Integer(val n: Int): Comp() {
        override operator fun compareTo(right: Comp) = when(right) {
            is Integer -> n.compareTo(right.n)
            is Array -> Array(listOf(this)).compareTo(right)
        }

        override fun toString() = n.toString()
    }
    data class Array(val contents: List<Comp>): Comp() {
        override fun compareTo(right: Comp): Int = when (right) {
            is Integer -> this.compareTo(Array(listOf(right)))
            is Array -> when {
                right.contents.isEmpty() && this.contents.isEmpty() -> 0
                right.contents.isEmpty() -> 1
                contents.isEmpty() -> -1
                contents.first().compareTo(right.contents.first()) == 0 -> tail().compareTo(right.tail())
                else -> contents.first().compareTo(right.contents.first())
            }
        }

        private fun tail() = Array(contents.drop(1))

        override fun toString() = contents.toString()
    }
}

fun main() {
    val testInput = """[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]""".split("\n")




    fun parseRow(it: String): Comp {
        val arr: ArrayNode = objectMapper.readValue(it)
        return arr.toComp()
    }



    fun part1(input: List<String>): Int {
        return blocksOfLines(input).withIndex().sumOf { (index, block) ->
            val (left, right) = block.map { parseRow(it) }
            if (left < right) index + 1 else 0
        }
    }


    fun part2(input: List<String>): Int {
        val specials = listOf("[[2]]", "[[6]]")
        val sorted = (input + specials)
            .filter(String::isNotBlank)
            .map(::parseRow)
            .sortedWith { x, y -> x.compareTo(y) }

        val dividers = sorted.indices.filter { sorted[it].toString() in specials }
            .map { it + 1}

        return dividers.reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 13)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 13).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

private fun JsonNode.toComp(): Comp = when (this) {
    is ArrayNode -> toComp()
    is IntNode -> toComp()
    else -> throw UnsupportedOperationException("Unexpected node: $this")
}

private fun ArrayNode.toComp(): Comp {
    return Comp.Array(this.map { it.toComp() })
}
private fun IntNode.toComp(): Comp {
    return Comp.Integer(intValue())
}

