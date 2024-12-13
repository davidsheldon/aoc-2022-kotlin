package aoc2024

import utils.InputUtils

private data class Block(val id: Int?, val start: Int, val size: Int) {
    fun checkSum(): Long = when {
        (id == null) -> 0
        else -> IntRange(start, start+size-1).sumOf { it.toLong() * id}
    }

    fun toBar(): String = (if (id != null) { ""+ ('0' + id) } else { "." }).repeat(size)
}
private class FileSystem(val blocks: MutableList<Block>) {
    fun defrag() {
        IntRange(0, blocks.size - 1).reversed().forEach {
            if (blocks[it].id != null) {
                tryToMoveBlock(it)
               // println(" $it -> $this")
            }
        }
    }

    fun tryToMoveBlock(i: Int) {
        val target = IntRange(0, i - 1)
            .filter { blocks[it].id == null}
            .firstOrNull { blocks[it].size >= blocks[i].size }
        if (target != null) {
            val targetBlock = blocks[target]
            val sourceBlock = blocks[i]

            val newBlock = sourceBlock.copy(start = targetBlock.start)
            blocks[i] = newBlock
            val newTarget = targetBlock.copy(
                start=targetBlock.start + sourceBlock.size,
                size = targetBlock.size - sourceBlock.size)
            blocks[target] = newTarget
        }
    }

    override fun toString() =
        blocks.asSequence().sortedBy { it.start }
            .map { it.toBar() }
            .joinToString("")

    fun checkSum() = blocks.sumOf(Block::checkSum)
}

fun main() {

    val testInput = """2333133121414131402""".trimIndent().split("\n")



    fun part1(input: List<String>): Long {
        val expanded = input[0].map { (it - '0').toInt() }
        val max = expanded.size / 2
        val all = expanded.chunked(2).flatMapIndexed { i, chunk ->
            val fs = chunk[0]
            val space = chunk.getOrNull(1) ?: 0

            List(fs) { i } + List(space) { -1 }
        }

        var endIndex = all.size - 1

        var index = 0
        var total = 0L
        while(index <= endIndex) {
            var file = all[index]
            while(file < 0) {
                file = all[endIndex--]
            }
            if (endIndex < index) break
            total += index * file
            index=index+1
        }



        return total
    }


    fun part2(input: List<String>): Long {
        val expanded = input[0].map { (it - '0').toInt() }

        var start = 0
        val all = expanded.chunked(2).flatMapIndexed { i, chunk ->
            val fs = chunk[0]
            val space = chunk.getOrNull(1) ?: 0

            val file = Block(i, start, fs)
            val spaceBlock = Block(null, start + fs, space)
            start += fs + space
            listOf(file, spaceBlock).filter { it.size != 0}
        }
        val fs = FileSystem(all.toMutableList())
        //println(fs)
        fs.defrag()
        //println(fs)
        return fs.checkSum()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 1928L)
    println(part2(testInput)) // 2858

    val puzzleInput = InputUtils.downloadAndGetLines(2024, 9)
    val input = puzzleInput.toList()

    println(part1(input)) // 6332450887598 too big, 6332189866718 correct
    println(part2(input))
}
