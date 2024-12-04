package aoc2024

import utils.*

class WordSearch(val grid: List<String>):ArrayAsSurface(grid) {
    fun each(): Sequence<Pair<Char, Coordinates>> =
        allPoints().map { it -> at(it) to it }

    fun charsFrom(start: Coordinates, direction: CompassPoint, n: Int = 4): String =
        start.heading(direction).takeWhile { coordinates -> inBounds(coordinates) }.take(n).map { at(it) }.joinToString("")

}

fun main() {

    val testInput = """MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val wordSearch = WordSearch(input)
        return wordSearch.each().filter { (c, _) -> c == 'X' }
            .sumOf { (_, coord) ->
                CompassPoint.entries
                  //  .onEach { println (" $it - ${wordSearch.charsFrom(coord, it)}")  }
                    .map { wordSearch.charsFrom(coord, it) }
                    .count { it == "XMAS" }
            }.toLong()
    }


    fun part2(input: List<String>): Long {
        val wordSearch = WordSearch(input)
        val possibleWords = listOf("MAS", "SAM")
        val possibleBox = Bounds(Coordinates(1,1), wordSearch.bottomRight().dX(-1).dY(-1))
        return wordSearch.each()
            .filter { (c, _) -> c == 'A' }
            .filter { (_, coord) ->  possibleBox.contains(coord) }
            .filter { (_, coord) ->
                var word1 = wordSearch.charsFrom(coord.move(CompassPoint.NW), CompassPoint.SE, 3)
                var word2 = wordSearch.charsFrom(coord.move(CompassPoint.SW), CompassPoint.NE, 3)
                possibleWords.containsAll(listOf(word1, word2))
            }
            .count().toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 18L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 4)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
