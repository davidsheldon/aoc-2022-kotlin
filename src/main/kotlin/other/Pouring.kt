package other

import aoc2022.utils.bfs
import java.nio.ByteBuffer


fun main() {
//    val board = Board("aabb", "bbaa", "")
//    println(board)
//    println(board.complete())
//    println(board.valid())
//
//    val end = sequenceOf(0 to 2, 0 to 2, 1 to 0, 1 to 0, 1 to 2, 1 to 2)
//        .fold(board) { b, command -> b.move(command) }
//    println(end)
//
//    println(board.canComplete())
//    println(Board("a", "aaa").canComplete())
//    println(Board("aba", "aa", "bbb").canComplete())

    val board = Board("abca", "bcaa", "cbbc", "")
    val end = sequenceOf(1 to 3, 1 to 3)
    .fold(board) { b, command -> b.move(command) }
    println(end)
    println(board.moves())
    println("---------------------")
    println(board.move(1 to 3).moves())
    println(board.canComplete())
    println(Board("abcb", "baac", "cbca", "").canComplete())
    println(Board("bbyb", "ybyr", "ryrr", "").canComplete())

}

fun <E> Iterable<E>.updated(index: Int, elem: E) = mapIndexed { i, existing ->  if (i == index) elem else existing }

typealias Move = Pair<Int, Int>

data class Board(val vials: List<Vial>) {
    constructor(vararg vials: String): this(vials.toList().map { Vial(it) })

    fun valid() = vials.joinToString("") { it.asString() }
        .groupingBy { it }.eachCount().values.all { it == 4 }

    fun complete() = vials.all { it.isEmpty() || it.complete() }

    fun move(m: Move): Board {
        val (source, col) = vials[m.first].withoutTop()!!
        return withVial(m.first, source).withVial(m.second, vials[m.second].with(col)!!)
    }

    fun canComplete() = valid() && bfs(this) { it.moves().asSequence().map { move -> it.move(move)} }
 //       .onEach { println("\n\nValid: ${it.valid()}\n$it") }
        .any { board -> board.complete() }

    fun size() = vials.size

    fun moves(): List<Move> {
        return vials.indices.flatMap { i ->
            when {
                !vials[i].isEmpty() -> {
                    val col = vials[i].top()
                    vials.indices
                        .filter { it != i }
                        .filter { vials[it].canTake(col)}
                        .map { i to it }
                }

                else -> emptyList<Move>()
            }
        }
    }

    fun withVial(index: Int, v: Vial): Board {
        return Board(vials.updated(index, v))
    }

    override fun toString(): String = vials.joinToString("\n") { it.asString() }
}

private fun Int.toByteArray(): ByteArray =
    ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()

private fun ByteArray.toInt(): Int =
    ByteBuffer.wrap(this).int

data class Vial(val content: String) {
    init {
        assert(content.length <= 4)
    }
    fun asString() = content
    fun hasSpace() = content.length < 4
    fun space() = 4 - size()
    fun isEmpty() = content.isEmpty()
    fun size() = content.length
    fun top() = content.last()
    fun canTake(col: Char) = isEmpty() || (hasSpace() && top() == col)

    fun with(col: Char) : Vial? =
        when {
            canTake(col) -> Vial(content + col)
            else -> null
        }

    fun withoutTop(): Pair<Vial, Char>? =
        when {
            isEmpty() -> null
            else -> Vial(content.dropLast(1)) to content.last()
        }

    fun complete(): Boolean = size() == 4 && content.all { it == content[0] }
    fun asInt(): Int = content.toByteArray().toInt()
}

class Pouring {
}