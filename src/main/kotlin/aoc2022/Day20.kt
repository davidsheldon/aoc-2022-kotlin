package aoc2022

import utils.InputUtils
import kotlin.math.absoluteValue

class DoubleLinkedList<T>(initialContent: List<T>) {
    var head: Node<T>
    val size = initialContent.size
    init {
        val nodes = initialContent.map { Node(it) }
        nodes.zipWithNext { a,b ->
            a.right = b
            b.left = a
        }
        nodes.first().left = nodes.last()
        nodes.last().right = nodes.first()
        head = nodes.first()
    }
    class Node<T>(val n: T) {
        lateinit var right: Node<T>
        lateinit var left: Node<T>
        override fun toString(): String {
            return n.toString()
        }
        fun moveRight() {
            val l = left;
            val r = right;

            l.right = r
            right.left = l

            right = r.right
            right.left = this
            r.right = this
            left = r

        }
        fun moveLeft() {
            val l = left;
            val r = right;

            r.left = l
            left.right = r

            left = l.left
            left.right = this
            l.left = this
            right = l

        }
    }

    fun moveRight(node: Node<T>) {
        node.moveRight()
        if (node == head) head = node.left
    }

    fun moveLeft(node: Node<T>) {
        node.moveLeft()
        if (head.left == node) head = node
    }

    override fun toString(): String {
        return asSequence().toList().toString()
    }

    fun asSequence() = sequence {
        var pos = head;
        do {
            yield(pos)
            pos = pos.right
        } while (pos != head)
    }

    fun asValueSequence() = asSequence().map { it.n }
}

fun main() {
    val testInput = """1
2
-3
3
-2
0
4""".split("\n")

    fun mix(l: DoubleLinkedList<Int>) {
        val values = l.asSequence().toList()
        values.forEach { n ->
            repeat(n.n.absoluteValue % (values.size-1)) {
                if (n.n > 0) l.moveRight(n) else l.moveLeft(n)
            }
        }
    }

    fun mix(l: DoubleLinkedList<Long>, times: Int) {
        val values = l.asSequence().toList()
        repeat(times) {
            values.forEach { n ->
                repeat((n.n.absoluteValue % (values.size-1)).toInt()) {
                    if (n.n > 0) l.moveRight(n) else l.moveLeft(n)
                }
            }
        }
    }


    fun part1(input: List<String>): Int {
        var list = DoubleLinkedList(input.map { it.toInt() })

        mix(list)

        val coordinates = list.asValueSequence().repeatForever()
            .dropWhile { it != 0 }
            .filterIndexed{index, _ -> index % 1000 == 0}.drop(1).take(3).toList()
        println(coordinates)
       return coordinates.sum()
    }

    fun part2(input: List<String>): Long {
        var list = DoubleLinkedList(input.map { it.toInt() * 811589153L })

        mix(list, 10)

        val coordinates = list.asValueSequence().repeatForever()
            .dropWhile { it != 0L }
            .filterIndexed{index, _ -> index % 1000 == 0} .drop(1).take(3).toList()
        println(coordinates)
        return coordinates.sum()
    }

    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 3)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 20).toList()


    println(part1(puzzleInput))
    println(part2(testInput))
    println(part2(puzzleInput))
}

