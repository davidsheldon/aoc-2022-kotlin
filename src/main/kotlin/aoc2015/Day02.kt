package aoc2015

import aoc2022.product
import utils.InputUtils

fun main() {

    fun sides(len: List<Int>) = listOf((len[0]*len[1]), ( len[1]*len[2]),( len[0]*len[2]))
    fun area(len: List<Int>) = 2*sides(len).sum() + sides(len).min()

    fun boxes(input: List<String>) = input.map { it.split("x").map { it.toInt() } }

    fun part1(input: List<String>): Int {
        return boxes(input).sumOf { area(it)}
    }

    fun bow(len: List<Int>) = len.product()
    fun ribbon(len: List<Int>) = 2 * listOf((len[0]+len[1]), ( len[1]+len[2]),( len[0]+len[2])).min()

    fun part2(input: List<String>): Int {
        return boxes(input).sumOf {
            ribbon(it) + bow(it)
        }
    }

    // test if implementation meets criteria from the description, like:
    val input = InputUtils.downloadAndGetLines(2015, 2).toList()

    println(part1(input))
    println(part2(input))
}
