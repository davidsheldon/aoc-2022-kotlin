package aoc2022

import aoc2022.utils.InputUtils
import java.lang.NumberFormatException
import java.util.concurrent.ConcurrentHashMap

private val parser = "(\\w+) ([-+*/]) (\\w+)".toRegex()
class Monkey20(val formula: String) {
    fun result(resolver: (String) -> Long): Long {
        val matcher = parser.matchEntire(formula)
        if (matcher != null) {
            val (left, op, right) = matcher.destructured

            val operation: (Long, Long) -> Long = when(op) {
                "+" -> { a,b -> a+b }
                "-" -> { a,b -> a-b }
                "*" -> { a,b -> a*b }
                else -> { a,b -> a/b }
            }
            return operation(resolver(left), resolver(right))
        }
        try {
            return formula.toLong()
        }
        catch (e: NumberFormatException) {
            throw IllegalArgumentException("Unable to parse $formula")
        }
    }
}

class Monkeys(val monkeys: Map<String, Monkey20>) {
    private val cache = ConcurrentHashMap<String, Long>()
    fun calculate(s: String): Long {
        if (cache.contains(s)) return cache[s]!!
        val done = monkeys[s]!!.result(this::calculate)
        cache[s] = done
        return done
    }
}

fun main() {
    val testInput = """root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32""".split("\n")


    fun parseMonkeys(input: List<String>) = input.associate {
        val (label, formula) = it.split(": ")
        label to Monkey20(formula)
    }

    fun part1(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        return Monkeys(monkeys).calculate("root")
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseMonkeys(input)

        // TODO - solve algabraically
        val root = Monkey20(monkeys["root"]!!.formula.replace("+","-"))

        return Monkeys(monkeys + mapOf("root" to root)).calculate("root")
    }



    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 152L)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 21).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

