package aoc2023

import utils.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.math.abs
import kotlin.math.max


private data class Instructions(val dir: Direction, val len: Int, val colour:Int)

private data class Line(val start: Coordinates, val end: Coordinates, val colour: Int) {
    val bb = listOf(start, end).bounds()
    fun includesY(y: Int) = minY() <= y && y<=max(start.y, end.y)
    fun minY() = bb.tl.y
    fun minX() = bb.tl.x
    fun maxX() = bb.br.x
    fun isHorizontal() = start.y == end.y
    fun length() = 1 + abs(start.y - end.y) + abs(start.x + end.x)

    fun contains(loc: Coordinates) = bb.contains(loc)
}

private val parseDigger = "([UDLR]) (\\d+) \\(#([0-9a-f]+)\\)".toRegex()

private fun String.toDigger() = aoc2023.parseMatchOrThrow(this, parseDigger) { match ->
    val (heading, distance, color) = match.destructured
    val dir = when(heading) {
        "U" -> N
        "D" -> S
        "L" -> W
        "R" -> E
        else -> throw IllegalStateException("Bad direction $heading")
    }
    Instructions(dir, distance.toInt(), color.toInt(16))
}

fun main() {
    val testInput = """R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)""".trimIndent().split("\n")


    fun showMap(
        bb: Pair<Coordinates, Coordinates>,
        lines: List<Line>
    ) {
        val image =
            BufferedImage(1 + bb.second.x - bb.first.x, 1 + bb.second.y - bb.first.y, BufferedImage.TYPE_INT_RGB)
        val g: Graphics2D = image.createGraphics()
        g.stroke = BasicStroke(1.0f)
        lines.forEach {
            g.color = Color(it.colour)
            val s = it.start - bb.first
            val e = it.end - bb.first
            g.drawLine(s.x, s.y, e.x, e.y)
        }

        val picLabel = JLabel(ImageIcon(image))
        val jPanel = JPanel()
        jPanel.add(picLabel)
        val f = JFrame()
        f.size = Dimension(image.width, image.height)
        f.add(jPanel)
        f.isVisible = true
    }

    fun part1(input: List<String>): Int {
        val points = input.map { it.toDigger() }
            .runningFold(Coordinates(0,0) to 0) { (loc, col), instructions ->
                loc.move(instructions.dir, instructions.len) to instructions.colour
            }
        val lines = points.zipWithNext { a,b -> Line(a.first, b.first, b.second)}
        val perimeter = lines.map { it.start.lineTo(it.end).toList() }.flatten()
        val bb = perimeter.boundingBox()

        //showMap(bb, lines)

        println(bb)

        return perimeter.groupBy { it.y }
            .entries
            .map { (y, list) ->
                var edgeCount = 0
                var inside = false
                val minX = list.minOf { it.x }
                val maxX = list.maxOf { it.x }
                val linesOnRow = lines.filter { it.includesY(y) }

                (minX..maxX).count{ x->
                    val loc = Coordinates(x,y)
                    if (loc in perimeter) {

                    }
                    linesOnRow.any { line -> line.contains(Coordinates(x,y))}

                }

            }
            .sum()


    }



    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testValue = part1(testInput)
    println(testValue)
    check(testValue == 62)

    println(part2(testInput))

    val puzzleInput = InputUtils.downloadAndGetLines(2023, 18)
    val input = puzzleInput.toList()

    println(part1(input))
    val start = System.currentTimeMillis()
    println(part2(input))
    println("Time: ${System.currentTimeMillis() - start}")
}
