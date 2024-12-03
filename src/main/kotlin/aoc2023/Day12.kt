package aoc2023

import utils.InputUtils

val dots = "[^#]+".toRegex()
fun String.replaceAt(index: Int, c: Char): String {
    val sb = StringBuilder(this)
    sb.setCharAt(index, c)
    return sb.toString()
}

fun minSize(groups: List<Int>) = groups.sum() + groups.size

fun exactWays(blockSize: Int, size: Int): List<String> = (0..size-blockSize).map {
    val sb = StringBuilder(size)
    (0 until size).forEach { i ->
        sb.append(if (it <= i && i < it+blockSize) { '#' } else { '.'})
    }
    sb.toString()
}

fun ways(blockSize: Int, maxSize: Int): List<String> = (blockSize..maxSize).flatMap { exactWays(blockSize, it) }

data class Key(val pattern: String, val x: Int)

fun w2(pattern: String, groups: List<Int>, cache: MutableMap<Key, Int>): Int = cache.getOrPut(Key(pattern, groups.size)) {
    val ret = ways2(pattern, groups, cache)
   // println("'$pattern' ($groups) = $ret")
    return ret
}
fun ways2(pattern: String, groups: List<Int>, cache: MutableMap<Key, Int>): Int {
    if (groups.isEmpty()) {
        return if (pattern.contains('#')) { 0 } else { 1 }
    }
    if (pattern.isEmpty()) {
        return 0
    }
    if (pattern.length < minSize(groups) - 1) return 0
    val first = pattern.first()
    fun hash(): Int {
        val firstGroup = groups.first()
        val prefix = pattern.take(firstGroup)
        if (prefix.length != firstGroup || prefix.contains('.')) return 0  // Can't fit the first group
        if (prefix.length == pattern.length) { // Got to the end of the pattern.
            if (groups.size > 1) return 0
            return 1
        }
        if (pattern[firstGroup] == '#') return 0
        return w2(pattern.drop(firstGroup+1), groups.drop(1), cache)
    }

    return when(first) {
        '.' -> w2(pattern.drop(1), groups, cache)
        '#' -> hash()
        else -> {
            w2(pattern.drop(1), groups, cache) + hash()
        }// ?
    }
}

fun allWays(pattern: String, groups: List<Int>) = ways2(pattern, groups, mutableMapOf())


fun waysMatching(pattern: String, size: Int): List<String> {
    return exactWays(size, pattern.length).filter { matches(it, pattern) }
}

fun sm(groups: List<Int>, pattern: String): List<String> {
    if (groups.size == 1) {
        return waysMatching(pattern, groups[0])
    }
    val head = groups.first()
    val tail = groups.drop(1)
    val maxLength = pattern.length - minSize(tail)

 //   println("MaxLength: $maxLength")
    val prefixes = (head..maxLength)
        .filter { pattern[it] != '#' && pattern[it-1] != '.' }
        .map { pattern.substring(0, it) }


//    println(prefixes)
//    prefixes.forEach {
//        val ways = waysMatching(it, head)
//        println("Exact: (${ways.size}, $it) $ways")
//    }

    return prefixes
        .flatMap { patternHead ->
            val patternTail = pattern.substring(patternHead.length + 1)
//            println("$patternHead . $patternTail")

            val heads = waysMatching(patternHead, head)
            val tails = sm(tail, patternTail)
            heads.flatMap { h -> tails.map { "$h.$it"} }
        }

}

fun solutionsMatching(groups: List<Int>, pattern: String): Int {
    val ret = sm(groups, pattern).size
    println("SM: $groups $pattern = $ret")
    return ret
}

var matchCount: Long = 0
fun matches(arrangement: String, pattern: String): Boolean {
    return arrangement.zip(pattern).all { (a, p) ->
        a == p || p == '?'
    }
}

fun main() {
    val testInput = """???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1""".trimIndent().split("\n")

    println(allWays(".??..??..", listOf(1,1)))
    println(allWays(".??..###", listOf(1,3)))
    fun groups(arrangement: String) = arrangement.split(dots).map { it.length }.filter { it != 0 }

    fun impossible(arrangement: String, expected: List<Int>): Boolean {
        val actual = groups(arrangement)
        if (actual.isEmpty()) return false
        if (actual.size > expected.size) return true
        if ((0..<actual.size-1).any { actual[it] != expected[it] }) return true
        if (actual.last() > expected[actual.size - 1]) return true
        return false
    }

    fun matches(arrangement: String, expected: List<Int>): Boolean {
        matchCount++
        val actual = groups(arrangement)
        return actual == expected

    }



    fun validCombinations(pattern: String, maxLength: Int, groups: List<Int>) {
        //
    }

    fun combination(pattern: String, groups: List<Int>): Int {
        val firstQ = pattern.indexOf('?')
        if (firstQ == -1) {
            return if (matches(pattern, groups))
                1
            else 0
        }
        if (impossible(pattern.substring(0, firstQ), groups)) { return 0 }
        return combination(pattern.replaceAt(firstQ, '.'), groups) +
                combination(pattern.replaceAt(firstQ, '#'), groups)
    }

    fun getProblem(input: List<String>) = input.map {
        val (pattern, groupString) = it.split(" ")
        val groups = listOfNumbers(groupString)
        pattern to groups
    }

    println(solutionsMatching(listOf(1,1,3), ".??..??...?##."))

    fun part1(input: List<String>): Int {
        val problem = getProblem(input)
        return problem
            .asSequence()
            .map { allWays(it.first, it.second) }
              .onEach { println("Solutions: $it") }
              .sum()

        //}
    }

    fun String.repeatJoined(n: Int, joiner: CharSequence) = Array(n) { this}.joinToString(joiner)
    fun <T> List<T>.repeated(n: Int) = List(n) { this}.flatten()

    fun part2(input: List<String>): Int {
        val problem = getProblem(input)

        return problem.asSequence()
            .map { it.first.repeatJoined(5, "?") to it.second.repeated(5) }
            .map {  allWays(it.first, it.second) }
            .onEach { println("Solutions: $it") }
            .sum()
    }



    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 21)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 12)
    val input = puzzleInput.toList()

    matchCount =0
    println(part1(input))
    println("Matches: $matchCount")
//    if(true) return
    matchCount =0
    println(part2(input))
    println("Matches: $matchCount")
}
