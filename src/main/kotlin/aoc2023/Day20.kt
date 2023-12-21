package aoc2023

import utils.InputUtils

enum class SignalLevel{ LOW, HIGH }
data class Pulse(val signal: SignalLevel, val from: Module, val to:String)

sealed class Module

data class FlipFlop(val outputs: List<String>): Module() {

    var state: Boolean = false
    fun pulse(p: SignalLevel): List<Pulse> {
        return when(p) {
            SignalLevel.HIGH -> { listOf() }
            SignalLevel.LOW -> {
                state = !state
                val signal = if (state) SignalLevel.HIGH else SignalLevel.LOW
                outputs.map { Pulse(signal, this, it) }
            }
        }
    }
}

class Conjunction(val inputs: List<String>, val outputs: List<String>): Module(){
    val states = mapOf<String, SignalLevel>()

    fun pulse(p: SignalLevel): List<Pulse> {
        TODO("FOO")
    }
}

class Broadcaster(val outputs: List<String>): Module()


fun main() {
    val testInput = """broadcaster -> a
%a -> inv, con
&inv -> b
%b -> con
&con -> output""".trimIndent().split("\n")



    fun part1(input: List<String>): Int {
        return input.size
    }


    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 11687500)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 20)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
