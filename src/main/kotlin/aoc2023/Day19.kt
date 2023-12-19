package aoc2023

import utils.InputUtils

val parseRule = "(([xmas])([><])(\\d+):)?([a-zAR]+)".toRegex()
val parseWorkflow = "([a-z]+)\\{(($parseRule,?)+)}".toRegex()
val parsePresent = "\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)}".toRegex()

private data class Rule(val field: Char?, val test: Char?, val comparison: Int?, val target: String, val inverted:Boolean=false) {
    val testFn: (Int) -> Boolean = when(test) {
        '<' -> {{ it < (comparison ?: Int.MAX_VALUE)}}
        '>' -> {{ it > (comparison ?: 0)}}
        else -> {{true}}
    }

    val matcher: (Present) -> Boolean = when(field) {
        'x' -> {{ testFn(it.x) }}
        'm' -> {{ testFn(it.m) }}
        'a' -> {{ testFn(it.a) }}
        's' -> {{ testFn(it.s) }}
        null -> {{true}}
        else -> throw IllegalStateException("Bad rule $field")
    }

    fun match(present: Present): Boolean = matcher(present)

    fun inRange(i: Int): Boolean {
        val ret = testFn(i)
        return if (inverted) !ret else ret
    }

    override fun toString(): String {
        return (if (field != null) { "$field$test$comparison" }
        else { "true" }) + "${if(inverted) "!" else ""}:$target"
    }
}
private data class Workflow(val label:String, val rules: List<Rule>) {
    fun applyTo(present: Present): String =
        rules.first { it.match(present)}.target

}
private data class Present(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun score(): Int {
        return x+m+a+s
    }
}

private fun createRule(matcher: MatchResult): Rule {
    val (_, field, test, comp, target) = matcher.destructured
    return Rule(field.getOrNull(0),
        test.getOrNull(0),
        if (comp.isNotBlank()) comp.toInt() else null,
        target)
}

private enum class Action { ACCEPT, REJECT}
private fun createWorkflow(matcher: MatchResult): Workflow {
    val (label, rules) = matcher.destructured
    val parsedRules = rules.split(",").parsedBy(parseRule, ::createRule).toList()
    return Workflow(label, parsedRules)
}
private fun parsePresent(matcher: MatchResult): Present {
    val (x,m,a,s) = matcher.destructured.toList().map { it.toInt() }
    return Present(x,m,a,s)
}

private val codeResult = mapOf("R" to Action.REJECT, "A" to Action.ACCEPT)
private fun processPresent(present: Present, sorters: Map<String, Workflow>): Action {
    return generateSequence("in") {
      sorters[it]?.applyTo(present)
    }.takeWhilePlusOne { it !in codeResult  }
        .last().let { codeResult[it] ?: throw IllegalStateException("Bad result $it") }


}


private data class Workflows(val workflows: Map<String, Workflow>) {

    fun combinations(rules: List<Rule>): Long {
        //println(rules)
        val valid = "xmas".map { ch ->
            val r = rules.filter { it.field == ch }
            (1..4000).count { x -> r.all { it.inRange(x) } }.toLong()
        }
        //println("$valid (${valid.product()})")
        return valid.product()
    }
    fun dfs() = dfs("in", listOf())

    fun dfs( flow: String, soFar: List<Rule>): Long {
        val tests = mutableListOf<Rule>()
        return workflows[flow]?.rules?.map { rule ->
           val ret = when(rule.target) {
               "R" -> 0L
               "A" -> combinations(soFar + tests + listOf(rule))
               else -> dfs(rule.target, soFar + tests + listOf(rule))
           }
            tests.addLast(rule.copy(inverted = true))
            ret
       }?.sum() ?: 0
    }

}

fun main() {
    val testInput = """px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        val (listOfInstructions, parts) = input.toBlocksOfLines().toList()

        val sorters = listOfInstructions.parsedBy(parseWorkflow, ::createWorkflow)
            .associateBy { it.label }

        val presents = parts.parsedBy(parsePresent, ::parsePresent)
        return presents.filter {
            processPresent(it, sorters) == Action.ACCEPT
        }
            .map { it.score() }
            .sum()
    }


    fun part2(input: List<String>): Long {
        val listOfInstructions = input.toBlocksOfLines().first()

        val workflows = listOfInstructions.parsedBy(parseWorkflow, ::createWorkflow)
            .associateBy { it.label }

        return Workflows(workflows).dfs()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 19114)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 19)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
