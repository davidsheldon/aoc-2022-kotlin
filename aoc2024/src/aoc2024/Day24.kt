package aoc2024

import utils.InputUtils

private enum class Op {
    OR,XOR,AND
}

private data class Instruction(val left: String, val op: Op, val right: String)

fun main() {

    val testInput = """x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj""".trimIndent().split("\n")

    fun getValue(key: String,
                 isInput: (String) -> Boolean,
                   input: (String) -> Int,
                   ops: (String) -> Instruction?,
                 cache: MutableMap<String, Int> = HashMap()): Int {
        cache[key]?.let { return it }
        val ret = when {
            isInput(key) -> input(key)
            else -> {
                val op = ops(key) ?: throw IllegalArgumentException("Missing $key")
                val l = getValue(op.left, isInput, input, ops, cache)
                val r = getValue(op.right, isInput, input, ops, cache)

                when (op.op) {
                    Op.XOR -> l xor r
                    Op.AND -> l and r
                    Op.OR -> l or r
                }
            }
        }
        cache[key] = ret
        return ret
   }


    fun execute(input: Map<String, Int>, ops: Map<String, Instruction>): String {

        val bits = ops.keys.filter { it.startsWith("z") }.sorted()
            .map {
                getValue(it, input::containsKey, { i -> input[i]!! }, ops::get)
            }.joinToString("").reversed()
        return bits
    }

    fun parseInstructions(instructions: List<String>): Map<String, Instruction> =
        instructions.associate {
            val (l, o, r, _, t) = it.split(" ")
            t to Instruction(l, Op.valueOf(o), r)
        }

    fun part1(input: List<String>): Long {
        val (initial, instructions) = input.toBlocksOfLines().toList()
        val states = initial.associate {
            val key = it.substring(0, 3)
            val value = it.substring(5).toInt()

            key to value
        }.toMutableMap()

        val ops = parseInstructions(instructions)

        val bits = execute(states, ops)
        println(bits)
        return bits.toLong(2)

    }



    fun add(x: Long, y: Long, ops: (String) -> Instruction?): Long {
        val target = x + y


        fun bit(i: String) = i.substring(1).toInt()
        fun isInput(i: String) = i[0] == 'x' || i[0] == 'y'
        fun input(i: String): Int {
            return (((when {
                i[0] == 'x' -> x
                else -> y
            }) shr bit(i)) and 1).toInt()
        }
        val cache = HashMap<String, Int>()

        var z = 0L
        (0..45).forEach {
            val thisBit = getValue("z%02d".format(it), ::isInput, ::input, ops, cache)
            if (thisBit != ((target shr it) and 1).toInt()) {
                println("Bug in bit $it")
            }
            z += thisBit shl it
        }
        return z
    }

    fun <K, V> Map<K, V>.swap(a: K, b: K) =
        this - listOf(a, b) + mapOf(a to this[b]!!, b to this[a]!!)

    fun usedIn(key: String, ops: (String) -> Instruction?): Set<String> {
        if (key[0] == 'x' || key[0] == 'y') return emptySet()
        val ins = ops.invoke(key) ?: error("Missing $key")

        return usedIn(ins.left, ops) + usedIn(ins.right, ops) + setOf(key)
    }

    fun debug(ops: Map<String, Instruction>, a: String, b: String) {
        println(usedIn(a, ops::get) - usedIn(b, ops::get))
        (usedIn(a, ops::get) - usedIn(b, ops::get)).forEach {
            println("$it = ${ops[it]}")
        }
    }

    fun part2(input: List<String>): String {
        val (initial, instructions) = input.toBlocksOfLines().toList()
        var x = 0L
        var y = 0L
        initial.forEach {

            val key = it.substring(1, 3).toInt()
            val value = it.substring(5).toInt()
            if (it[0] == 'x') {
               x += value shl key
            } else {
                y += value shl key
            }
        }


        var ops = parseInstructions(instructions)

        add(x,y, ops::get)

        ops = ops.swap("z10", "gpr")
        ops = ops.swap("nks", "z21")
        ops = ops.swap("ghp", "z33")
        debug(ops, "z39", "z38")
        debug(ops, "z40", "z39")
        ops = ops.swap("cpm", "krs")
        add(x,y, ops::get)
        debug(ops, "z39", "z38")
        debug(ops, "z40", "z39")

        //ops = ops.swap("z45", "cpm")


        (0..45).forEach {
            x = 0x1L shl it
            y = 0x3L shl it

            add(x,y,ops::get)
        }
        return listOf("z10", "gpr",
            "ghp", "z33",
            "nks", "z21",
            "krs", "cpm").sorted().joinToString(",")
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 2024L)
    //println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 24)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
