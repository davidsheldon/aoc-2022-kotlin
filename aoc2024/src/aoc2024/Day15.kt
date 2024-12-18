package aoc2024

import utils.*

private fun String.doubled(): String {
    val sb = StringBuilder(length * 2)
    for (c in this) {
        sb.append(when (c) {
            '#' -> "##"
            else -> ".."
        })
    }
    return sb.toString()
}


private fun ArrayAsSurface.score() = indexed().filter { (_, c) -> c == 'O' }.sumOf { (pos, _) -> (100*pos.y) + pos.x.toLong()}



fun main() {
    data class State(val boxes: List<Coordinates>, val robot: Coordinates, val map: ArrayAsSurface) {

        fun score() = boxes.distinct().sumOf { (x,y) -> (100*y) + x.toLong()}
        fun boxesTouching(c: Coordinates, d: Direction): List<Coordinates> {
            val tc = c.move(d)
            return listOf(tc,tc.dX(-1)).filter { boxes.contains(it) && it != c }
        }
        fun anyWalls(bs: List<Coordinates>, d: Direction): Boolean = anyWalls(bs.map { it.move(d)})
        fun anyWalls(bs: List<Coordinates>): Boolean = bs.any { map.checkedAt(it) == '#' }
        fun allBoxes(c: Coordinates, d: Direction): List<Coordinates> = allBoxes(c, d, setOf(c))

        fun allBoxes(c: Coordinates, d: Direction, seen: Set<Coordinates>): List<Coordinates> {
            val bs = boxesTouching(c,d) - seen

            val newSeen = seen + bs
            return bs + bs.flatMap { allBoxes(it, d, newSeen) + allBoxes(it.dX(1), d, newSeen) }
        }


        fun move(d: Direction): State {
            if (anyWalls(listOf(robot), d)) return this
            val bsPushed = allBoxes(robot, d)
            //println("Moving ${bsPushed.size} boxes, $d")
            if (anyWalls(bsPushed, d) || anyWalls(bsPushed.map { it.dX(1)}, d)) return this
            val newBoxes = (boxes - bsPushed.toSet()) + bsPushed.map { it.move(d)}
            return copy(boxes = newBoxes, robot = robot.move(d))
        }

        override fun toString(): String {
            return boxes.fold(map) { m, b -> m.replace(b, '[').replace(b.dX(1), ']')}.replace(robot, '@').toString()
        }
    }

    val testInput = """##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^""".trimIndent().split("\n")


    fun direction(c: Char) = when (c) {
        '^' -> N
        'v' -> S
        '<' -> W
        '>' -> E
        else -> null
    }

    fun part1(input: List<String>): Long {
        val (mapChars,instructions) = input.toBlocksOfLines().toList()
        val map = ArrayAsSurface(mapChars)
        val moves = instructions.joinToString("").mapNotNull { direction(it) }
        val endMap = moves.fold(map) { m, dir ->
            val robot = m.find { it == '@' }
            val next = robot.move(dir).heading(dir).takeWhilePlusOne { m.checkedAt(it) == 'O' }.last()
            val atNext = m.checkedAt(next)
            if (atNext == '.') {
                m.replace(robot, '.')
                    .replace(next, m.checkedAt(robot.move(dir)))
                    .replace(robot.move(dir), '@')
            } else m
        }
        println(endMap)
        return endMap.score()
    }


    fun part2(input: List<String>): Long {
        val (mapChars,instructions) = input.toBlocksOfLines().toList()
        val moves = instructions.joinToString("").mapNotNull { direction(it) }
        val map = ArrayAsSurface(mapChars)
        val boxes = map.indexed()
            .filter { it.second == 'O' }
            .map { it.first.copy(x=it.first.x * 2) }.toList()
        val r = map.find { it == '@' }
        val robot = r.copy(x=r.x*2)
        val newMap = ArrayAsSurface(mapChars.map { it.doubled() })
        val startState = State(boxes, robot, newMap)
        println(startState)
        val endState = moves.fold(startState) { s, m -> s.move(m) }
        println(endState)
        return endState.score()
    }


    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 10092L)
    println(part2(testInput)) // 9021
    val puzzleInput = InputUtils.downloadAndGetLines(2024, 15)
    val input = puzzleInput.toList()

    println(part1(input))
    println(part2(input))
}
