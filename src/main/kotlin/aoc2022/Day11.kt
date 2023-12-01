package aoc2022

import utils.InputUtils

sealed interface Operation {
    fun apply(old: Long): Long
}

object Square : Operation {
    override fun apply(old: Long): Long {
        return old * old
    }
}

class Add(private val add: Int) : Operation {
    override fun apply(old: Long): Long {
        return old + add
    }

}

class Mul(private val mul: Int) : Operation {
    override fun apply(old: Long): Long {
        return old * mul
    }
}

data class Test(val divisor: Long, val whenTrue: Int, val whenFalse: Int) {
    fun apply(n: Long) = if (n.rem(divisor) == 0L) whenTrue else whenFalse
}


data class Monkey(
    val id: Int,
    val items: MutableList<Long>,
    val operation: Operation,
    val test: Test
)

fun main() {
    val testInput = """Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1""".split("\n")

    fun String.lastNumber(): Long = substringAfterLast(' ').toLong()

    fun parseMonkey(lines: List<String>): Monkey {
        val id = lines[0].substringAfter(' ')[0].digitToInt()
        val items = listOfNumbers(lines[1].substringAfter(':')).map { it.toLong() }
        val op = lines[2].substringAfter('=').trim()
        val operation = when (op[4]) {
            '*' -> {
                when (val param = op.substring(6)) {
                    "old" -> Square
                    else -> Mul(param.toInt())
                }
            }

            '+' -> Add(op.substringAfter('+').trim().toInt())
            else -> throw IllegalArgumentException("Unsupported operation $op")
        }
        val test = Test(lines[3].lastNumber(), lines[4].lastNumber().toInt(), lines[5].lastNumber().toInt())

        return Monkey(id, items.toMutableList(), operation, test)
    }

    fun runRound(monkeys: List<Monkey>, counts: MutableMap<Int, Int>, divisor: Long) {
        val modulus = monkeys.map { it.test.divisor }.reduce(Long::times)
        monkeys.forEach { monkey ->
            counts.compute(monkey.id) { _, old -> (old ?: 0) + monkey.items.size }
            for (worry in monkey.items) {
                val exitWorry = (monkey.operation.apply(worry) / divisor).rem(modulus)
                val targetMonkey = monkey.test.apply(exitWorry)
                monkeys[targetMonkey].items.add(exitWorry)
            }
            monkey.items.clear()
        }
    }

    fun part1(input: List<String>): Int {
        val monkeys = blocksOfLines(input).map { parseMonkey(it) }.sortedBy { it.id }
        val counts = mutableMapOf<Int, Int>()
        repeat(20) {
            runRound(monkeys, counts, 3)
        }
        println(counts)
        return counts.values.sortedDescending().take(2).reduce(Int::times)
    }


    fun part2(input: List<String>): Long {
        val monkeys = blocksOfLines(input).map { parseMonkey(it) }.sortedBy { it.id }
        val counts = mutableMapOf<Int, Int>()
        repeat(10_000) {
            runRound(monkeys, counts, 1)
            if ((it + 1) in listOf(1, 20, 1000)) {
                println(counts)
            }
        }
        println(counts)
        return counts.values.sortedDescending().take(2).map { it.toLong() }.reduce(Long::times)
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 10605)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 11).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}
