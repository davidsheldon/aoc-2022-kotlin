package aoc2021


import aoc2022.blocksOfLines
import aoc2022.utils.InputUtils

class Day04(val calls: List<Int>, private val boards: List<Board>) {

    fun makeCalls(): Sequence<Pair<Int, Board>> = calls.asSequence().flatMap { n ->
        boards.filter { !it.isWinner() }.filter {
            it.callNumber(n)
            it.isWinner()
        }.map { n to it }
    }

    fun callNumber(n: Int) {
        boards.filter { !it.isWinner() }.forEach { it.callNumber(n) }
    }

    fun winningBoard() : Board? = boards.find { it.isWinner() }

    class Board(private val cells: IntArray) {
        private val marked = BooleanArray(5*5)
        override fun toString(): String {
            return cells.asSequence()
                .chunked(5)
                    .map { it.joinToString(" ") { it.toString().padStart(2) }}
                .joinToString("\n")
        }

        fun callNumber(n: Int) {
            val index = cells.indexOf(n)
            if (index >= 0) marked[index] = true
        }

        fun isWinner(): Boolean {
            return markedRows().any { row -> row.all { it } } ||
                    markedCols().any {col -> col.all { it } }
        }

        fun sumOfUnmarkedCells(): Int = cells.filterIndexed { index, _ -> !marked[index] }.sum()

        private fun markedRows() = marked.asSequence().chunked(5)
        private fun markedCols() = marked.indices
            .groupBy({it % 5}, {marked[it]}).values.toList()


    }
}



fun main() {
    val testInput = """7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7""".split("\n")

    fun parseBoard(input: List<String>): Day04.Board {
        val board = input
            .flatMap {
                it.trim().split("\\s+".toRegex()).map(String::toInt)
            }
            .toIntArray()
        return Day04.Board(board)
    }

    fun parse(input: List<String>): Day04 {
        val calls = input[0].split(",").map { it.toInt() }
        val boards = blocksOfLines(input.drop(2))
            .map(::parseBoard).toList()
        return Day04(calls, boards)
    }


    fun part1(input: List<String>): Int {
        val state = parse(input)
        val (call, board) = state.makeCalls().first()
        return call * board.sumOfUnmarkedCells()
    }


    fun part2(input: List<String>): Int {
        val state = parse(input)
        val (call, board) = state.makeCalls().last()
        return call * board.sumOfUnmarkedCells()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 4512)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 4)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
