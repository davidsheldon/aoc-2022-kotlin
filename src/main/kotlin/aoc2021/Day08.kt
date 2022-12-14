package aoc2021


import aoc2022.utils.InputUtils

fun main() {
    val testInput = """be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce""".split("\n")

    fun parse(input: List<String>) = input.map { line ->
        val (inputList, output) = line
            .split("|")
            .map { it.trim().split(' ') }
        inputList to output
    }

    fun part1(input: List<String>): Int {
        val unique = listOf(2,3,4,7)
        return parse(input).sumOf { (_, output) -> output.count { it.length in unique }}
    }

    fun deduceNumbers(input: List<String>): Map<String, Int> {
        val asSets = input.map { it.toCharArray().toSet() }
        val byLength = asSets.groupBy { it.size }
        val knownNumbers = mutableMapOf(
            1 to byLength[2]!!.first(),
            4 to byLength[4]!!.first(),
            7 to byLength[3]!!.first(),
            8 to byLength[7]!!.first(),
        )
        val four = knownNumbers[4]!!
        val fiveLong = byLength[5]!!
        val digitA = (knownNumbers[7]!! - knownNumbers[1]).first()
        val digitD = fiveLong.reduce { a,b -> a.intersect(b) }.intersect(four).first()
        val digitG = fiveLong.reduce { a,b -> a.intersect(b) }.intersect(knownNumbers[8]!!.minus(four).minus(digitA)).first()

        val sixLong = byLength[6]!!
        val zero = sixLong.first { digitD !in it }
        knownNumbers[0] = zero
        val sixOrNine = sixLong.minusElement(zero)
        knownNumbers[6] = sixOrNine.first {
            it.intersect(four).size == 3
        }
        knownNumbers[9] = sixOrNine.minusElement(knownNumbers[6]!!).first()
        val digitC = (knownNumbers[8]!! - knownNumbers[6]!!).first()
        val digitE = (knownNumbers[8]!! - knownNumbers[9]!!).first()

        knownNumbers[2] = fiveLong.first { digitE in it }
        knownNumbers[5] = fiveLong.first { digitC !in it }
        knownNumbers[3] = fiveLong.minusElement(knownNumbers[5]!!).minusElement(knownNumbers[2]!!).first()

        return knownNumbers.entries.associateBy({ it.value.sorted().joinToString("") }, { it.key })
    }

    fun part2(input: List<String>): Int {
        return parse(input).map { (key, out) ->
            val decoder = deduceNumbers(key)
            out.map { decoder[it.toCharArray().sorted().joinToString("")] ?: '?' }
        }.sumOf { it.joinToString("").toInt() }
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 26)

    val puzzleInput = InputUtils.downloadAndGetLines(2021, 8)
    val input = puzzleInput.toList()

    println(part1(input))
    println("=== Part 2 ===")
    println(part2(testInput))
    println(part2(input))
}
