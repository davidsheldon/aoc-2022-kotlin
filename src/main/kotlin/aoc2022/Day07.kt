package aoc2022

import aoc2022.utils.InputUtils

sealed class Day07 {
    abstract fun size(): Int
    data class Directory(val name: String, val parent: Directory?, val contents: MutableList<Day07> = mutableListOf()) :
        Day07() {
        override fun size(): Int = contents.sumOf { it.size() }
        fun dirs() = contents.filterIsInstance<Day07.Directory>()
    }

    data class File(val size: Int, val name: String) : Day07() {
        override fun size(): Int = size
    }
}

class FileSystem() {
    val root: Day07.Directory = Day07.Directory("/", null)
    var cwd = root

    fun dfs(): Sequence<Day07.Directory> = dfs(root)
    fun dfs(dir: Day07.Directory): Sequence<Day07.Directory> = sequence {
        dir.dirs().forEach { yieldAll(dfs(it)) }
        yield(dir)
    }
}

private val cd = Regex("\\$ cd (\\S+)")
private val ls = Regex("\\$ ls")
private val dir = Regex("dir (\\S+)")
private val file = Regex("(\\d+) (\\S+)")

fun FileSystem.create(commands: Iterator<String>) {
    while (commands.hasNext()) {
        val cmd = commands.next()
        cd.matchEntire(cmd)?.let { match ->
            val (dir) = match.destructured
            cwd = when (dir) {
                "/" -> root
                ".." -> cwd.parent!!
                else -> cwd.dirs().first { it.name == dir }
            }
        }
        if (cmd == "${'$'} ls") {
            // Nothing
        }
        dir.matchEntire(cmd)?.let { match ->
            val (name) = match.destructured
            cwd.contents.add(Day07.Directory(name, cwd))
        }
        file.matchEntire(cmd)?.let { match ->
            val (size, name) = match.destructured
            cwd.contents.add(Day07.File(size.toInt(), name))
        }
    }
}

fun main() {
    val testInput = """${'$'} cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} cd e
${'$'} ls
584 i
${'$'} cd ..
${'$'} cd ..
${'$'} cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k""".split("\n")

    fun part1(input: List<String>): Int {
        val fs = FileSystem()
        fs.create(input.iterator())
        return fs.dfs().map { it.size() }.filter { it < 100_000 }.sum()
    }


    fun part2(input: List<String>): Int {
        val fs = FileSystem()
        fs.create(input.iterator())

        val free = 70_000_000 - fs.root.size()
        val target = 30_000_000 - free


        return fs.dfs().map { it.size() }.sorted().filter { it >= target }.first()
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 95437)

    val puzzleInput = InputUtils.downloadAndGetLines(2022, 7).toList()


    println(part1(puzzleInput))
    println(part2(puzzleInput))
}
