package aoc2024

import utils.InputUtils

data class Rule(val left: Int, val right: Int)
data class Update(val pages: List<Int>) {
    fun middle() = pages[pages.size / 2]

    fun isValid(
        groupedByRight: Map<Int, List<Int>>
    ): Boolean = !IntRange(0, pages.size - 2).any { index ->
        val page = pages[index]
        val rest = pages.subList(index + 1, pages.size)
        val rs = groupedByRight[page] ?: emptyList()
        rest.any { p -> rs.contains(p) }
    }

    fun sort(rules: Map<Pair<Int,Int>, Int>): Update {
        return Update(pages.sortedWith { l, r -> rules[l to r] ?: 0 })
    }

}

fun main() {

    val testInput = """47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47""".trimIndent().split("\n")

    fun parse(input: List<String>): Pair<List<Rule>,List<Update>> {
        val (unparsedRules,unparsedUpdates) = input.toBlocksOfLines().toList()
        val rules = unparsedRules.map {line ->
            val (left, right) = line.split("|").map { it.toInt() }
            Rule(left, right)
        }
        val updates = unparsedUpdates.map {line -> Update(listOfNumbers(line)) }
        return rules to updates
    }



    fun part1(input: List<String>): Long {
        val (rules, updates) = parse(input)
        val groupedRules = rules.groupBy { it.left }
        val groupedByRight = rules.groupBy({ it.right }, {it.left})

        return updates.filter { u -> u.isValid(groupedByRight) }
//            .onEach(::println)
            .sumOf { it.middle()}.toLong()
    }


    fun part2(input: List<String>): Long {
        val (rules, updates) = parse(input)
        val groupedByRight = rules.groupBy({ it.right }, {it.left})
        val compareRules = rules.associate { rule -> (rule.left to rule.right) to -1 } +
                rules.associate { rule -> (rule.right to rule.left) to 1 }

        return updates.filter { u -> !u.isValid(groupedByRight) }
            .map { it.sort(compareRules)}
            .sumOf { it.middle()}.toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 143L)
    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 5)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
