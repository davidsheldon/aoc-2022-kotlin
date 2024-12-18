package aoc2024

import utils.InputUtils
import kotlin.math.pow

private data class State(val a: Long, val b: Long, val c: Long, val pc: Int = 0) {

    fun execute(opcode: Int, p: Int): Pair<State, Int?> =
        when(opcode) {
            0 -> /* ADV */ copy(a=aOverCombo(p), pc=pc + 2) to null
            1 -> /* BXL */ copy(b=b xor p.toLong(), pc=pc + 2) to null
            2 -> /* BST */ copy(b=decodeCombo(p) and 0x7, pc=pc + 2) to null
            3 -> /* JNZ */ (if (a!=0L) copy(pc=p) else copy(pc=pc+2)) to null
            4 -> /* BXC */ copy(b=b xor c, pc=pc+2) to null
            5 -> /* OUT */ copy(pc=pc + 2) to (decodeCombo(p) and 0x7).toInt()
            6 -> /* BDV */ copy(b=aOverCombo(p), pc=pc + 2) to null
            7 -> /* CDV */ copy(c=aOverCombo(p), pc=pc + 2) to null
            else -> throw IllegalArgumentException("Invalid opcode $opcode")
        }

    fun aOverCombo(p:Int): Long {
        val combo = decodeCombo(p)
        if (combo > 63) return 0
        return (a/(2.0.pow(combo.toDouble()))).toLong()
    }


    fun decodeCombo(combo: Int): Long =
        when(combo) {
            0,1,2,3 -> combo.toLong()
            4 -> a
            5 -> b
            6 -> c
            else -> throw IllegalArgumentException("Invalid combo $combo")
        }

}

private data class CPU(val state: State, val program: List<Int>, val output: List<Int> = listOf()) {
    fun execute() : CPU {
        val (opcode, oprand) = program.subList(state.pc, state.pc + 2)

        val (newState, out) = state.execute(opcode, oprand)

        return copy(state=newState, output = if (out!=null) output.plus(out) else output)
    }

    fun withA(a: Long) = copy(state=state.copy(a=a))

}

fun main() {

    val testInput = """Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0""".trimIndent().split("\n")
    val testInput2 = """Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0""".trimIndent().split("\n")



    fun part1(input: List<String>): String {
        var cpu = initialCpu(input)

        cpu = executeUntilEnd(cpu)

        return cpu.output.joinToString(",")
    }


    fun part2(input: List<String>): Long {
        val cpu = initialCpu(input)
        //  2,4,1,1,7,5,0,3,1,4,4,5,5,5,3,0
        //
        // b=(a&7) xor 1
        // c=a/(1 shl b)
        // a=a/8
        // b=b xor 4
        // b=b xor c
        // out b & 7
        // jnz 0
        // Output is 16 characters, a loses 3 bits for each time through the loop
        // there's no state
        //
        fun aOut(a: Long): Int {
            var b = (a and 7) xor 1
            var c = (a/(2.0.pow(b.toDouble()))).toLong()
            b = b xor 4
            b = b xor c
            return (b and 0x7).toInt()
        }
        var aList = listOf(0L)
        cpu.program.reversed().forEach { byte ->
            //202322348616234
            val newA = mutableListOf<Long>()
            (0..7).forEach { i ->
                aList.forEach { a ->
                    val t = (a * 8)  + i
                    if (aOut(t) == byte) {
                         newA.add(t)
                    }
                }
            }
            aList = newA
        }
        return aList.min()
    }

    fun part2BF(input: List<String>): Long {
        val cpu = initialCpu(input)

        for(i in 1..Int.MAX_VALUE) {
            if (i % 1_000_000 == 0) println(i)
            var c = cpu.withA(i.toLong())
            try {
                do {
                    c = c.execute()
//                    println(c)
//                    println(cpu.program.subList(0, c.output.size))
                    if (c.output == cpu.program) return i.toLong()
                } while (c.output.size < cpu.program.size && c.output == cpu.program.subList(0, c.output.size))
            }  catch(e: IndexOutOfBoundsException) {}
        }
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == "4,6,3,5,6,3,5,2,1,0")
    println(part2BF(testInput2))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 17)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}

private fun executeUntilEnd(cpu: CPU): CPU {
    var cpu1 = cpu
    try {
        while (true) {
            cpu1 = cpu1.execute()
        }
    } catch (e: IndexOutOfBoundsException) {
        println(cpu1)
    }
    return cpu1
}

private fun initialCpu(input: List<String>): CPU {
    val (regs, prog) = input.toBlocksOfLines().toList()
    val (a, b, c) = regs.map { it.split(": ")[1].toLong() }
    val state = State(a, b, c, 0)
    val program = prog[0].substringAfter(": ").split(",").map { it.toInt() }
    var cpu = CPU(state, program)
    return cpu
}
